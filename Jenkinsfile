def slackChannel = 'components-ci'

def nexusCredentials = usernamePassword(
        credentialsId: 'nexus-artifact-zl-credentials',
        usernameVariable: 'NEXUS_USER',
        passwordVariable: 'NEXUS_PASSWORD')
def gitCredentials = usernamePassword(
        credentialsId: 'github-credentials',
        usernameVariable: 'GITHUB_LOGIN',
        passwordVariable: 'GITHUB_TOKEN')
def dockerCredentials = usernamePassword(
        credentialsId: 'artifactory-datapwn-credentials',
        passwordVariable: 'ARTIFACTORY_PASSWORD',
        usernameVariable: 'ARTIFACTORY_LOGIN')
def sonarCredentials = usernamePassword(
        credentialsId: 'sonar-credentials',
        passwordVariable: 'SONAR_PASSWORD',
        usernameVariable: 'SONAR_LOGIN')

def PRODUCTION_DEPLOYMENT_REPOSITORY = "TalendOpenSourceSnapshot"

def branchName = BRANCH_NAME.startsWith("PR-")
        ? env.CHANGE_BRANCH
        : env.BRANCH_NAME

String releaseVersion = ''
String extraBuildParams = ''

final String escapedBranch = branchName.toLowerCase().replaceAll("/", "_")
final boolean isOnMasterOrMaintenanceBranch = env.BRANCH_NAME == "master" || env.BRANCH_NAME.startsWith("maintenance/")
final String devNexusRepository = isOnMasterOrMaintenanceBranch ? "${PRODUCTION_DEPLOYMENT_REPOSITORY}" : "dev_branch_snapshots/branch_${escapedBranch}"
final String podLabel = "connectors-se-${UUID.randomUUID().toString()}".take(53)
String EXTRA_BUILD_PARAMS = ""

final String deploymentSuffix = (env.BRANCH_NAME == "master" || env.BRANCH_NAME.startsWith("maintenance/")) ? "${PRODUCTION_DEPLOYMENT_REPOSITORY}" : ("dev_branch_snapshots/branch_${escapedBranch}")
final String m2 = "/tmp/jenkins/tdi/m2/${deploymentSuffix}"
final String talendOssRepositoryArg = (env.BRANCH_NAME == "master" || env.BRANCH_NAME.startsWith("maintenance/")) ? "" : ("-Dtalend_oss_snapshots=https://nexus-smart-branch.datapwn.com/nexus/content/repositories/${deploymentSuffix}")


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
    imagePullSecrets:
        - name: talend-registry
"""
        }
    }

    environment {
        MAVEN_SETTINGS = "${WORKSPACE}/.jenkins/settings.xml"
        MAVEN_OPTS = "-Dmaven.artifact.threads=128 -Dorg.slf4j.simpleLogger.showThreadName=true -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss -Dtalend.maven.decrypter.m2.location=${WORKSPACE}/.jenkins/"

        TALEND_REGISTRY = 'registry.datapwn.com'

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
        choice(
                name: 'Action',
                choices: ['STANDARD', 'RELEASE', 'DEPLOY'],
                description: """
			        Kind of run:
			        STANDARD (default): standard building
			        RELEASE : build release
			        DEPLOY : Build release, deploy it to the Nexus for any branch""")
        booleanParam(
                name: 'FORCE_SONAR',
                defaultValue: false,
                description: 'Force Sonar analysis')
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
                        def pom = readMavenPom file: 'pom.xml'

                        if (params.Action == 'RELEASE' && !pom.version.endsWith('-SNAPSHOT')) {
                            error('Cannot release from a non SNAPSHOT, exiting.')
                        }

                        echo 'Processing parameters'
                        final buildParamsAsArray = ['--settings', env.MAVEN_SETTINGS, params.EXTRA_BUILD_PARAMS]
                        if (!isOnMasterOrMaintenanceBranch) {
                            // Properties documented in the pom.
                            buildParamsAsArray.addAll([
                                    '--define', "nexus_snapshots_repository=${params.DEV_NEXUS_REPOSITORY}",
                                    '--define', 'nexus_snapshots_pull_base_url=https://nexus-smart-branch.datapwn.com/nexus/content/repositories',
                                    '--define', 'talend.maven.decrypter.m2.location=${env.WORKSPACE}/.jenkins/'
                            ])
                        }
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
                    }
                }
            }
        }
        stage('Post login') {
            steps {
                container('main') {
                    withCredentials([nexusCredentials, gitCredentials, dockerCredentials]) {
                        script {
                            if (params.POST_LOGIN_SCRIPT?.trim()) {
                                try {
                                    sh "${params.POST_LOGIN_SCRIPT}"
                                } catch (error) {
                                    //
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
                    publishHTML(target: [
                            allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true,
                            reportDir   : 'target/talend-component-kit', reportFiles: 'icon-report.html',
                            reportName  : "Icon Report"
                    ])
                    publishHTML(target: [
                            allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true,
                            reportDir   : 'target/talend-component-kit', reportFiles: 'repository-dependency-report.html',
                            reportName  : "Dependencies Report"
                    ])
                }
            }
        }

        /** A REVOIR ******************************************************/
        stage('Post Standard Build Steps') {
            when {
                expression { params.Action == 'STANDARD' }
            }
            stage('Documentation') {
                when {
                    anyOf {
                        branch 'master'
                        expression { env.BRANCH_NAME.startsWith('maintenance/') }
                    }
                }
                steps {
                    container('main') {
                        withCredentials([dockerCredentials]) {
                            sh """
			                     |cd ci_documentation
			                     |mvn ${EXTRA_BUILD_PARAMS} -B -s .jenkins/settings.xml clean install -DskipTests
			                     |chmod +x .jenkins/generate-doc.sh && .jenkins/generate-doc.sh
			                     |""".stripMargin()
                        }
                    }
                }
                post {
                    always {
                        publishHTML(target: [
                                allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true,
                                reportDir   : 'ci_documentation/target/talend-component-kit_documentation/', reportFiles: 'index.html', reportName: "Component Documentation"
                        ])
                    }
                }
            }
            stage('Site') {
                when {
                    anyOf {
                        branch 'master'
                        expression { env.BRANCH_NAME.startsWith('maintenance/') }
                    }
                }
                steps {
                    container('main') {
                        sh 'cd ci_site && mvn ${EXTRA_BUILD_PARAMS} -B -s .jenkins/settings.xml clean site site:stage -Dmaven.test.failure.ignore=true'
                    }
                }
                post {
                    always {
                        publishHTML(target: [
                                allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true,
                                reportDir   : 'ci_site/target/staging', reportFiles: 'index.html', reportName: "Maven Site"
                        ])
                    }
                }
            }
            stage('Nexus') {
                when {
                    anyOf {
                        branch 'master'
                        expression { env.BRANCH_NAME.startsWith('maintenance/') }
                    }
                }
                steps {
                    container('main') {
                        withCredentials([nexusCredentials]) {
                            sh "cd ci_nexus && mvn ${EXTRA_BUILD_PARAMS} -B -s .jenkins/settings.xml clean deploy -e -Pdocker -DskipTests ${talendOssRepositoryArg}"
                        }
                    }
                }
            }
            stage('Sonar') {
                when {
                    anyOf {
                        branch 'master'
                        expression { env.BRANCH_NAME.startsWith('maintenance/') }
                        expression { params.FORCE_SONAR == true }
                    }
                }
                environment {
                    LIST_FILE = sh(returnStdout: true, script: "find \$(pwd) -type f -name 'jacoco.xml'  | sed 's/.*/&/' | tr '\n' ','").trim()
                }
                steps {
                    container('main') {
                        withCredentials([sonarCredentials]) {
                            sh "mvn ${EXTRA_BUILD_PARAMS} -Dsonar.host.url=https://sonar-eks.datapwn.com -Dsonar.login='$SONAR_LOGIN' -Dsonar.password='$SONAR_PASSWORD' -Dsonar.branch.name=${env.BRANCH_NAME} -Dsonar.coverage.jacoco.xmlReportPaths='${LIST_FILE}' sonar:sonar -PITs -s .jenkins/settings.xml -Dtalend.maven.decrypter.m2.location=${env.WORKSPACE}/.jenkins/"
                        }
                    }
                }
            }
        }
        /** *****************************************************/

        stage('Release') {
            when {
                expression { params.Action == 'RELEASE' }
                anyOf {
                    branch 'master'
                    expression { BRANCH_NAME.startsWith('maintenance/') }
                }
            }
            steps {
                withCredentials([gitCredentials, nexusCredentials, dockerCredentials, artifactoryCredentials]) {
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
