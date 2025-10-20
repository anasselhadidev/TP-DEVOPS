// Fichier : Jenkinsfile
pipeline {
    agent any // Le pipeline peut s'exécuter sur n'importe quel agent Jenkins disponible

    // Définit les outils à installer automatiquement pour ce pipeline
    tools {
        maven 'Maven-3.9' // Le nom doit correspondre EXACTEMENT à celui configuré dans Jenkins
    }

    stages {
        // Étape 1: Cloner le repo [cite: 23]
        // Cette étape est gérée automatiquement par Jenkins quand il récupère le Jenkinsfile,
        // donc on la déclare juste pour la visibilité.
        stage('1. Checkout Code') {
            steps {
                echo 'Code source cloné automatiquement par Jenkins.'
            }
        }

        // Étape 2: Compiler le projet [cite: 24]
        stage('2. Compile Project') {
            steps {
                echo 'Compilation du projet...'
                // La commande 'mvn compile' va compiler le code source
                sh 'mvn compile'
            }
        }

        // Étape 3: Lancer les tests unitaires [cite: 25]
        stage('3. Run Unit Tests') {
            steps {
                echo 'Exécution des tests unitaires...'
                // La commande 'mvn test' exécute les tests
                sh 'mvn test'
            }
        }

        // Étape 4: Générer le package .war [cite: 26]
        stage('4. Package Application') {
            steps {
                echo 'Création du package...' // On peut enlever la mention .war
                sh 'mvn package'

                echo 'Archivage du .jar...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true // <-- Changez .war en .jar ici
            }
        }

        // Étape 5: Déclencher l'analyse SonarQube [cite: 27]
        // Cette étape est préparée pour la suite du TP.
        stage('5. SonarQube Analysis') {
            steps {
                // Cette section configure l'environnement avec les infos du serveur SonarQube
                // que vous avez configuré dans "Configure System".
                withSonarQubeEnv('MySonarQubeServer') {
                    // La commande Maven qui lance l'analyse et envoie les résultats à SonarQube
                    sh 'mvn sonar:sonar'
                }
            }
        }
    }

    post {
        // Actions à exécuter à la fin du pipeline, quel que soit le résultat
        always {
            echo 'Pipeline terminé.'
            // Nettoie l'espace de travail pour économiser de l'espace disque
            cleanWs()
        }
    }
}