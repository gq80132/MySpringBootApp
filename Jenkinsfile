pipeline {
    agent any

    tools {
        maven 'Maven' // Specify the Maven tool configured in Jenkins
    }

    environment {
        DOCKER_IMAGE = 'my-springboot-app'
        REPO_URL = 'https://github.com/gq80132/MySpringBootApp.git'
        EC2_HOST = '18.191.161.237'
        EC2_USER = 'ec2-user'
        SSH_KEY_PATH = '/Users/gq/CS/Phase4Project/myKeyPair.pem'
        // Add Docker path if needed
        PATH = "/usr/local/bin:/opt/homebrew/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Getting source code...'
                git branch: 'main', url: env.REPO_URL
            }
        }

        stage('Build Locally') {
            steps {
                echo 'Building Spring Boot app locally...'
                sh '''
                    # Build with Maven using the configured Maven tool
                    mvn clean package -DskipTests -q
                    ls -la target/*.jar

                    # Check if Docker is available
                    which docker || echo "Docker not found in PATH: $PATH"

                    # Build Docker image locally
                    docker build -t ${DOCKER_IMAGE}:latest .
                    echo "✅ Docker image built locally: ${DOCKER_IMAGE}:latest"
                '''
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo 'Deploying to EC2 instance...'
                sh '''
                    # Save Docker image as tar file for transfer
                    docker save ${DOCKER_IMAGE}:latest | gzip > ${DOCKER_IMAGE}.tar.gz
                    echo "✅ Docker image saved as tar.gz"

                    # Transfer image to EC2
                    scp -i ${SSH_KEY_PATH} -o StrictHostKeyChecking=no ${DOCKER_IMAGE}.tar.gz ${EC2_USER}@${EC2_HOST}:~/
                    echo "✅ Image transferred to EC2"

                    # Deploy on EC2 via SSH
                    ssh -i ${SSH_KEY_PATH} -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << 'EOF'
                        # Load the Docker image
                        docker load < ~/${DOCKER_IMAGE}.tar.gz

                        # Stop and remove existing container
                        docker stop my-springboot-app || true
                        docker rm my-springboot-app || true

                        # Run new container
                        docker run -d --name my-springboot-app -p 9090:9090 ${DOCKER_IMAGE}:latest

                        # Clean up tar file
                        rm ~/${DOCKER_IMAGE}.tar.gz

                        # Verify deployment
                        sleep 5
                        if docker ps | grep my-springboot-app; then
                            echo "✅ Container deployed successfully on EC2"
                            echo "🌐 App accessible at: http://${EC2_HOST}:9090/news/headline"
                        else
                            echo "❌ Deployment failed"
                            docker logs my-springboot-app
                        fi
                    EOF

                    # Clean up local tar file
                    rm ${DOCKER_IMAGE}.tar.gz
                    echo "✅ Local cleanup completed"
                '''
            }
        }
    }

    post {
        always {
            echo "Pipeline completed. Check EC2 instance for deployment status."
        }
        success {
            echo "🎉 Deployment successful! Your app is running on EC2."
        }
        failure {
            echo "❌ Deployment failed. Check the logs above for errors."
        }
    }
}