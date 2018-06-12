def slackChannel = 'components'

pipeline {
  agent any

  options {
    buildDiscarder(logRotator(artifactNumToKeepStr: '5', numToKeepStr: env.BRANCH_NAME == 'master' ? '10' : '2'))
    timeout(time: 60, unit: 'MINUTES')
    skipStagesAfterUnstable()
  }

  triggers {
    cron '@daily'
    pollSCM '@hourly'
  }

  tools{
    maven 'maven 3'
    jdk 'java 8'
  }

  stages {
   stage('Compile') {
     steps {
       sh 'mvn clean install -DskipTests'
     }
   }
   stage('Test') {
     steps {
       sh 'mvn clean install'
     }
     post {
       always {
         archive 'target/**/*'
         junit 'target/surefire-reports/*.xml'
       }
     }
   }
  }
  post {
    success {
      slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", channel: "${slackChannel}")
    }
    failure {
      slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", channel: "${slackChannel}")
    }
  }
}
