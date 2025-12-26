pipeline {
    agent any

    tools {
        maven 'Maven3'      // Jenkins → Global Tool Configuration name
        jdk 'JDK17'         // Change if using JDK 8 / 11
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/manickaselvam-s/project_one.git'
            }
        }

        stage('Clean & Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t project_one:latest .'
            }
        }
    }

    post {
        success {
            echo '✅ Build & Docker image created successfully'
        }
        failure {
            echo '❌ Build failed'
        }
    }
}
