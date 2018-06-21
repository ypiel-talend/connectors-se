def slackChannel = 'components'

pipeline {
  agent {
    kubernetes {
      label 'connectors-se'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
    - name: docker
      image: docker
      command:
      - cat
      tty: true
      volumeMounts:
      - name: docker
        mountPath: /var/run/docker.sock
    - name: maven
      image: maven:3.5.3-jdk-8
      command:
      - cat
      tty: true
      volumeMounts:
      - name: m2
        mountPath: /root/.m2/repository
      resources:
          requests:
            memory: "1G"
            cpu: "500m"
          limits:
            memory: "4G"
            cpu: "2"

  volumes:
  - name: docker
    hostPath:
      path: /var/run/docker.sock
  - name: m2
    hostPath:
      path: /tmp/jenkins/tdi/m2
"""
    }
  }

  environment {
    MAVEN_OPTS = '-Dmaven.artifact.threads=128 -Dorg.slf4j.simpleLogger.showThreadName=true -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss'
  }

  options {
    buildDiscarder(logRotator(artifactNumToKeepStr: '5', numToKeepStr: env.BRANCH_NAME == 'master' ? '10' : '2'))
    timeout(time: 60, unit: 'MINUTES')
    skipStagesAfterUnstable()
  }

  triggers {
    cron(env.BRANCH_NAME == "master" ? "@daily" : "")
  }

  stages {
    stage('Run maven') {
      steps {
        container('maven') {
          sh 'mvn clean install -T1C -Pdocker'
        }
      }
    }
    stage('Build Docker Components Image') {
      when {
        expression { sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim() == 'master' }
      }
      steps {
        container('docker') {
          sh 'mvn clean install -T1C -Pdocker -DskipTests -Dinvoker.skip=true'
          dir ('connectors-se-docker/target/classes') {
            withCredentials([  usernamePassword(
                credentialsId: 'docker-registry-credentials',
                passwordVariable: 'DOCKER_PASSWORD',
                usernameVariable: 'DOCKER_LOGIN')
            ]) {
              sh 'chmod +x build.docker.sh && ./build.docker.sh'
            }
          }
        }
      }
    }
    stage('Publish Site') {
      steps {
        container('maven') {
          sh 'mvn clean site:site site:stage -T1C -Dmaven.test.failure.ignore=true'
        }
      }
    }
  }
  post {
    always {
      junit testResults: '*/target/surefire-reports/*.xml', allowEmptyResults: true
      publishHTML (target: [
        allowMissing: true,
        alwaysLinkToLastBuild: false,
        keepAll: true,
        reportDir: 'target/staging',
        reportFiles: 'index.html',
        reportName: "Maven Site"
      ])
    }
    success {
      slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", channel: "${slackChannel}")
    }
    failure {
      slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", channel: "${slackChannel}")
    }
  }
}
