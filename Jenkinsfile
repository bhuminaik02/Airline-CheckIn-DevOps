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
                    java -version
                    if not exist "%CHROME_BINARY%" (
                        echo Chrome was not found at %CHROME_BINARY% for this Jenkins agent.
                        exit /b 1
                    )
                    echo Reading Chrome version metadata without starting the browser.
                    "%SystemRoot%\\System32\\WindowsPowerShell\\v1.0\\powershell.exe" -NoLogo -NoProfile -NonInteractive -ExecutionPolicy Bypass -Command "$ErrorActionPreference = [System.Management.Automation.ActionPreference]::Stop; $version = (Get-Item -LiteralPath $env:CHROME_BINARY -ErrorAction Stop).VersionInfo.ProductVersion; if ([string]::IsNullOrWhiteSpace($version)) { throw [System.InvalidOperationException]::new() }; Write-Output $version"
                    if errorlevel 1 (
                        echo Failed to read Chrome version from the installed executable metadata.
                        exit /b 1
                    )
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
