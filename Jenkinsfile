final String slackChannel = 'components-ci'

final def nexusCredentials = usernamePassword(
        credentialsId: 'nexus-artifact-zl-credentials',
        usernameVariable: 'NEXUS_USER',
        passwordVariable: 'NEXUS_PASSWORD')
final def gitCredentials = usernamePassword(
        credentialsId: 'github-credentials',
        usernameVariable: 'GITHUB_LOGIN',
        passwordVariable: 'GITHUB_TOKEN')
final def artifactoryCredentials = usernamePassword(
        credentialsId: 'artifactory-datapwn-credentials',
        passwordVariable: 'ARTIFACTORY_PASSWORD',
        usernameVariable: 'ARTIFACTORY_LOGIN')
final def sonarCredentials = usernamePassword(
        credentialsId: 'sonar-credentials',
        passwordVariable: 'SONAR_PASSWORD',
        usernameVariable: 'SONAR_LOGIN')

final String PRODUCTION_DEPLOYMENT_REPOSITORY = "TalendOpenSourceSnapshot"

final String branchName = BRANCH_NAME.startsWith("PR-")
        ? env.CHANGE_BRANCH
        : env.BRANCH_NAME

String releaseVersion = ''
String extraBuildParams = ''

final String escapedBranch = branchName.toLowerCase().replaceAll("/", "_")
final boolean isOnMasterOrMaintenanceBranch = env.BRANCH_NAME == "master" || env.BRANCH_NAME.startsWith("maintenance/")

final String devNexusRepository = isOnMasterOrMaintenanceBranch
        ? "${PRODUCTION_DEPLOYMENT_REPOSITORY}"
        : "dev_branch_snapshots/branch_${escapedBranch}"

final String podLabel = "connectors-se-${UUID.randomUUID().toString()}".take(53)

/*final String deploymentSuffix = (env.BRANCH_NAME == "master" || env.BRANCH_NAME.startsWith("maintenance/")) ? "${PRODUCTION_DEPLOYMENT_REPOSITORY}" : ("dev_branch_snapshots/branch_${escapedBranch}")
final String m2 = "/tmp/jenkins/tdi/m2/${deploymentSuffix}"
final String talendOssRepositoryArg = (env.BRANCH_NAME == "master" || env.BRANCH_NAME.startsWith("maintenance/")) ? "" : ("-Dtalend_oss_snapshots=https://nexus-smart-branch.datapwn.com/nexus/content/repositories/${deploymentSuffix}")
*/


pipeline {
    agent {
        kubernetes {
            label podLabel
            yaml """
apiVersion: v1
kind: Pod
spec:
    containers:
        -
            name: main
            image: '${env.TSBI_IMAGE}'
            command: [cat]
            tty: true
            volumeMounts: [
                {name: docker, mountPath: /var/run/docker.sock},
                {name: efs-jenkins-connectors-se-m2, mountPath: /root/.m2/repository}
                {name: dockercache, mountPath: /root/.dockercache}
            ]
            resources: {requests: {memory: 3G, cpu: '2'}, limits: {memory: 8G, cpu: '2'}}
    volumes:
        -
            name: docker
            hostPath: {path: /var/run/docker.sock}
        -
            name: efs-jenkins-connectors-se-m2
            persistentVolumeClaim: 
                claimName: efs-jenkins-connectors-se-m2
        -
            name: dockercache
            hostPath: {path: /tmp/jenkins/tdi/docker}
    imagePullSecrets:
        - name: talend-registry
""".stripIndent()
        }
    }

    environment {
        MAVEN_SETTINGS = "${WORKSPACE}/.jenkins/settings.xml"
        DECRYPTER_ARG = "-Dtalend.maven.decrypter.m2.location=${env.WORKSPACE}/.jenkins/"
        MAVEN_OPTS = "-Dtalend-image.layersCacheDirectory=/root/.dockercache -Dmaven.artifact.threads=128 -Dorg.slf4j.simpleLogger.showThreadName=true -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss ${DECRYPTER_ARG}"
        TALEND_REGISTRY = "registry.datapwn.com"
        VERACODE_APP_NAME = 'Talend Component Kit'
        VERACODE_SANDBOX = 'connectors-se'

        APP_ID = '579232'
        ARTIFACTORY_REGISTRY = "artifactory.datapwn.com"

        TESTCONTAINERS_HUB_IMAGE_NAME_PREFIX = "artifactory.datapwn.com/docker-io-remote/"
    }

    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5', numToKeepStr: (env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('maintenance/')) ? '10' : '2'))
        timeout(time: 120, unit: 'MINUTES')
        skipStagesAfterUnstable()
    }

    triggers {
        cron(env.BRANCH_NAME == "master" ? "@daily" : "")
    }

    parameters {
        choice(name: 'Action',
                choices: ['STANDARD', 'RELEASE', 'DEPLOY'],
                description: '''
			        Kind of run:
                    STANDARD : (default) classical CI
                    RELEASE : Build release, deploy to the Nexus for master/maintenance branches
			        DEPLOY : Build release, deploy it to the Nexus for any branch
                ''')
        string(
                name: 'EXTRA_BUILD_PARAMS',
                defaultValue: "",
                description: 'Add some extra parameters to maven commands. Applies to all maven calls.')
        string(
                name: 'POST_LOGIN_SCRIPT',
                defaultValue: "",
                description: 'Execute a shell command after login. Useful for maintenance.')
        string(
                name: 'DEV_NEXUS_REPOSITORY',
                defaultValue: devNexusRepository,
                description: 'The Nexus repositories where maven snapshots are deployed')
    }

    stages {

        stage('Prepare build') {
            steps {
                container('main') {
                    script {
                        final def pom = readMavenPom file: 'pom.xml'

                        if (params.Action == 'RELEASE' && !pom.version.endsWith('-SNAPSHOT')) {
                            error('Cannot release from a non SNAPSHOT, exiting.')
                        }

                        echo 'Processing parameters'
                        final List<String> buildParamsAsArray = ['--settings', env.MAVEN_SETTINGS, env.DECRYPTER_ARG]
                        if (!isOnMasterOrMaintenanceBranch) {
                            // Properties documented in the pom.
                            buildParamsAsArray.addAll([
                                    '--define', "nexus_snapshots_repository=${params.DEV_NEXUS_REPOSITORY}",
                                    '--define', 'nexus_snapshots_pull_base_url=https://nexus-smart-branch.datapwn.com/nexus/content/repositories'
                            ])
                        }
                        buildParamsAsArray.add(params.EXTRA_BUILD_PARAMS)
                        extraBuildParams = buildParamsAsArray.join(' ')
                        releaseVersion = pom.version.split('-')[0]

                        echo 'Git login'
                        withCredentials([gitCredentials]) {
                            sh """
                                bash .jenkins/git-login.sh \
                                    "\${GITHUB_LOGIN}" \
                                    "\${GITHUB_TOKEN}"
                            """
                        }

                        echo 'Docker login'
                        withCredentials([artifactoryCredentials]) {
                            /* In following sh step, '${ARTIFACTORY_REGISTRY}' will be replaced by groovy */
                            /* but the next two ones, "\${ARTIFACTORY_LOGIN}" and "\${ARTIFACTORY_PASSWORD}", */
                            /* will be replaced by the bash process. */
                            sh """
                                bash .jenkins/docker-login.sh \
                                    '${ARTIFACTORY_REGISTRY}' \
                                    "\${ARTIFACTORY_LOGIN}" \
                                    "\${ARTIFACTORY_PASSWORD}"
                            """
                        }
                        echo "Pre-requisites script"
                        sh "bash .jenkins/prerequisites.sh"
                    }
                }
            }
        }

        stage('Post login') {
            steps {
                container('main') {
                    withCredentials([nexusCredentials, gitCredentials, artifactoryCredentials]) {
                        script {
                            if (params.POST_LOGIN_SCRIPT?.trim()) {
                                try {
                                    sh "bash -c '${params.POST_LOGIN_SCRIPT}'"
                                } catch (ignored) {
                                    // The job must not fail if the script fails
                                }
                            }
                        }
                    }
                }
            }
        }
        stage('Build') {
            when {
                expression { params.Action == 'STANDARD' }
            }
            steps {
                container('main') {
                    script {
                        withCredentials([nexusCredentials]) {
                            sh """
                                bash .jenkins/build.sh '${params.Action}' ${extraBuildParams}
                            """
                        }
                    }
                }
            }

            post {
                always {
                    junit testResults: '*/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Release') {
            when {
                expression { params.Action == 'RELEASE' }
                anyOf {
                    branch 'master'
                    expression { BRANCH_NAME.startsWith('maintenance/') }
                }
            }
            steps {
                withCredentials([gitCredentials,
                                 nexusCredentials,
                                 artifactoryCredentials]) {
                    container('main') {
                        script {
                            sh """
                                bash .jenkins/release.sh "${releaseVersion}" ${extraBuildParams}
                            """
                        }
                    }
                }
            }
        }

        stage('Push maven artifacts') {
            when {
                anyOf {
                    expression { params.Action == 'DEPLOY' }
                }
            }
            steps {
                withCredentials([nexusCredentials]) {
                    container('main') {
                        script {
                            sh """
                                bash .jenkins/deploy.sh '${params.Action}' ${extraBuildParams}
                            """
                        }
                    }
                }
            }
        }

    }
    post {
        success {
            slackSend(color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", channel: "${slackChannel}")
        }
        failure {
            slackSend(color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", channel: "${slackChannel}")
        }
    }
}
