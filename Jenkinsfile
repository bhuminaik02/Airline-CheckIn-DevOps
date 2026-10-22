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

        stage('Browser Tests') {
            environment {
                CHROME_BINARY = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
            }
            steps {
                bat '''
                    whoami
                    where java
                    java -version
                    if not exist "%CHROME_BINARY%" (
                        echo Chrome was not found at %CHROME_BINARY% for this Jenkins agent.
                        exit /b 1
                    )
                    "%CHROME_BINARY%" --version
                    call mvnw.cmd -B -Pselenium verify
                '''
            }
            post {
                always {
                    junit testResults: 'target/selenium-output/reports/TEST-*.xml', allowEmptyResults: false
                    archiveArtifacts artifacts: 'target/selenium-output/screenshots/**/*.png',
                            allowEmptyArchive: true, fingerprint: true
                }
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
