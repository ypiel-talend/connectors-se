def slackChannel = 'components'
def mavenName = 'maven-3.5.3'
def jdkName = 'jdk8-latest'

pipeline {
  agent any

  options {
    buildDiscarder(logRotator(artifactNumToKeepStr: '5', numToKeepStr: env.BRANCH_NAME == 'master' ? '10' : '2'))
    timeout(time: 60, unit: 'MINUTES')
    skipStagesAfterUnstable()
  }

  triggers {
    cron(env.BRANCH_NAME == "master" ? "H H(19-21) * * *" : "")
  }

  stages {
   stage('Compile') {
     steps {
       withMaven(maven: mavenName, jdk: jdkName) {
         sh 'mvn clean install -DskipTests'
       }
     }
   }
   stage('Test') {
     steps {
       withMaven(maven: mavenName, jdk: jdkName) {
         sh 'mvn clean install'
       }
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
