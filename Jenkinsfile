pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                echo 'Code source cloné automatiquement par Jenkins.'
            }
        }

        stage('2. Compile Project') {
            steps {
                echo 'Compilation du projet...'
                sh 'mvn compile'
            }
        }

        stage('3. Run Unit Tests') {
            steps {
                echo 'Exécution des tests unitaires...'
                sh 'mvn test'
            }
        }

        stage('4. Package Application') {
            steps {
                echo 'Création du package...'
                sh 'mvn package'
                echo 'Archivage du .jar...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('5. SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('6. Build & Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials',
                                                     passwordVariable: 'DOCKER_PASSWORD',
                                                     usernameVariable: 'DOCKER_USERNAME')]) {
                        def imageNameWithBuildNumber = "anasselhadi850/tp-devops:${env.BUILD_NUMBER}"
                        def imageNameLatest = "anasselhadi850/tp-devops:latest"

                        echo "Construction de l'image Docker..."
                        sh "docker build -t ${imageNameWithBuildNumber} -t ${imageNameLatest} ."

                        echo "Connexion à Docker Hub..."
                        sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin"

                        echo "Push de l'image avec le numéro de build..."
                        sh "docker push ${imageNameWithBuildNumber}"

                        echo "Push de l'image avec le tag 'latest'..."
                        sh "docker push ${imageNameLatest}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé.'
            cleanWs()
        }
    }
}
