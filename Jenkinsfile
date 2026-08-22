pipeline {

    agent any

    parameters {
        choice(
            name: 'DEPLOY_VERSION',
            choices: ['AUTO'],
            description: 'AUTO uses the version from pom.xml.'
        )
    }

    environment {
        APP_NAME    = 'java-app'
        APP_PORT    = '9090'
        ANSIBLE_DIR = '/var/lib/jenkins/ansible/java-app'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Read Version') {
            steps {
                script {
                    def pomVersion = sh(
                        script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout',
                        returnStdout: true
                    ).trim()

                    env.APP_VERSION = (params.DEPLOY_VERSION == 'AUTO') ? pomVersion : params.DEPLOY_VERSION
                    currentBuild.description = "Version ${APP_VERSION}"
                    echo "Deployment version: ${APP_VERSION}"
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Prepare Artifact') {
            steps {
                sh '''
                    mkdir -p ${ANSIBLE_DIR}/deploy
                    cp target/${APP_NAME}-${APP_VERSION}.jar ${ANSIBLE_DIR}/deploy/java-app.jar
                '''
            }
        }

        stage('Deploy with Ansible') {
            steps {
                echo "Deploying ${APP_NAME} version ${APP_VERSION} to app server on port ${APP_PORT}"
                sh '''
                    ansible-playbook \
                        -i ${ANSIBLE_DIR}/inventory \
                        ${ANSIBLE_DIR}/deploy.yml \
                        -e "app_version=${APP_VERSION}"
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    ansible -i ${ANSIBLE_DIR}/inventory app -m shell -a "systemctl is-active --quiet java-app"
                    ansible -i ${ANSIBLE_DIR}/inventory app -m shell -a "curl -f --max-time 10 http://localhost:${APP_PORT}"
                '''
            }
        }

        stage('Deployment Summary') {
            steps {
                echo """
                ======================================
                DEPLOYMENT SUCCESSFUL
                ======================================
                Application : ${APP_NAME}
                Version     : ${APP_VERSION}
                Port        : ${APP_PORT}
                ======================================
                """
            }
        }
    }

    post {
        success {
            echo "Deployment successful."
        }
        failure {
            echo "Pipeline failed."
        }
        always {
            sh 'rm -rf ${ANSIBLE_DIR}/deploy || true'
            cleanWs()
        }
    }
}
