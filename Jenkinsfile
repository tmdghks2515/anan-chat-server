pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew bootJar'
            }
        }
        stage('docker build'){
            steps{
                script{
                    docker.build('kifarm')
                }
            }
        }

        stage('docker run'){
            steps{
                sh 'docker ps -f name=kifarm -q | xargs --no-run-if-empty docker container stop'
                sh 'docker container ls -a -f name=kifarm -q | xargs -r docker container rm'
                sh 'docker images --no-trunc --all --quiet --filter="dangling=true" | xargs --no-run-if-empty docker rmi'
                sh 'docker run -d --name kifarm -p 8080:8080 kifarm:latest'

            }
        }

    }
}