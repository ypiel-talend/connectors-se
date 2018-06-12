pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        readMavenPom(file: 'pom.xml')
      }
    }
  }
}