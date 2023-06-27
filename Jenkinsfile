pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-northeast-2'
        CODECOMMIT_REPO_URL = 'https://git-codecommit.ap-northeast-2.amazonaws.com/v1/repos/klovers-server'
        CODECOMMIT_CREDENTIALS = 'codecommit-key'
        DOCKER_IMAGE_NAME = '106809242629.dkr.ecr.ap-northeast-2.amazonaws.com/klovers-server'
        DOCKERFILE_PATH = 'Dockerfile'
        GRADLE_WRAPPER = './gradlew'
        GRADLE_OPTIONS = '--no-daemon'
        CONTAINER_NAME="klovers-server"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: 'main']],
                          userRemoteConfigs: [[url: env.CODECOMMIT_REPO_URL, credentialsId: env.CODECOMMIT_CREDENTIALS]]])
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    def dockerImage = docker.build(env.DOCKER_IMAGE_NAME, "-f ${env.DOCKERFILE_PATH} .")

                    withCredentials([usernamePassword(credentialsId: 'aws-ecr', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh 'aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin 106809242629.dkr.ecr.ap-northeast-2.amazonaws.com'
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Build and Test') {
            steps {
                sh "${env.GRADLE_WRAPPER} ${env.GRADLE_OPTIONS} clean build"
            }
        }

        stage('Deploy') {
            steps {
                // Add your deployment steps here
                sh 'docker ps -f name=$CONTAINER_NAME -q | xargs --no-run-if-empty docker container stop'
                sh 'docker container ls -a -f name=$CONTAINER_NAME -q | xargs -r docker container rm'
                sh 'docker images --no-trunc --all --quiet --filter="dangling=true" | xargs --no-run-if-empty docker rmi'
                sh 'docker run -d --name $CONTAINER_NAME -p 8080:8080 $DOCKER_IMAGE_NAME:latest'
            }
        }
    }
}