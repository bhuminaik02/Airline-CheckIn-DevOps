pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'call mvnw.cmd -B clean verify'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/checkin-demo-portal-0.0.1-SNAPSHOT.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Build and tests passed. JAR archived successfully.'
        }
        failure {
            echo 'Pipeline failed. Review the console output.'
        }
    }
}
