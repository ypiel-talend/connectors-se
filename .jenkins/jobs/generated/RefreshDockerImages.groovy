#!/usr/bin/groovy

pipeline {
    options {
        disableConcurrentBuilds()
    }
    agent {
        kubernetes {
            label 'connectors-se_refresh-docker-images'
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
                - name: maven
                  image: jenkinsxio/builder-maven:0.0.319
                  command:
                  - cat
                  tty: true
                  volumeMounts:
                  - name: docker
                    mountPath: /var/run/docker.sock
                  - name: m2
                    mountPath: /root/.m2/repository
            
              volumes:
              - name: docker
                hostPath:
                  path: /var/run/docker.sock
              - name: m2
                hostPath:
                  path: /tmp/jenkins/tdi/m2
            """.stripMargin()
        }
    }

    triggers {
        cron(env.BRANCH_NAME == "master" ? "@daily" : "")
    }

    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5', numToKeepStr: env.BRANCH_NAME == 'master' ? '10' : '2'))
        timeout(time: 60, unit: 'MINUTES')
        skipStagesAfterUnstable()
    }

    stages {
        stage('Refresh Proposals') {
            steps {
                container('maven') {
                    withCredentials([
                            usernamePassword(
                                    credentialsId: 'artifactory-credentials',
                                    passwordVariable: 'JFROG_TOKEN',
                                    usernameVariable: 'JFROG_LOGIN')
                    ]) {
                        script {
                            def src = new URL('https://raw.githubusercontent.com/Talend/connectors-se/master/.jenkins/jobs/templates/Artifactory.groovy').text
                            new File('Artifactory.groovy').text = src
                            def artifactory = load 'Artifactory.groovy'
                            artifactory.token = "${env.JFROG_TOKEN}"

                            def datasetTags = artifactory.listTags('talend/data-catalog/dataset')
                            println(datasetTags)
                        }
                    }
                }
            }
        }
    }
}
