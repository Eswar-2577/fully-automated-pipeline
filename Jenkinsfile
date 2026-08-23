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
        APP_NAME      = 'java-app'
        APP_PORT      = '9090'
        ANSIBLE_DIR   = '/var/lib/jenkins/ansible/java-app'
        JAVA_HOME     = '/usr/lib/jvm/java-21-amazon-corretto.x86_64'
        PATH          = "/usr/lib/jvm/java-21-amazon-corretto.x86_64/bin:${env.PATH}"
        APPROVER_MAIL = 'you@example.com'   // <-- change this to where approval emails should go
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

                    env.APP_VERSION = (params.DEPLOY_VERSION == 'AUTO')
                        ? pomVersion
                        : params.DEPLOY_VERSION

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

        stage('Deployment Approval') {
            steps {
                script {
                    mail(
                        to: "${APPROVER_MAIL}",
                        subject: "Approval needed: Deploy ${APP_NAME} v${APP_VERSION}",
                        body: "Build #${BUILD_NUMBER} of ${APP_NAME} v${APP_VERSION} is ready to deploy.\n\n" +
                              "Click here to approve or abort (expires in 10 minutes):\n${BUILD_URL}input\n\n" +
                              "Console output:\n${BUILD_URL}console"
                    )

                    timeout(time: 10, unit: 'MINUTES') {
                        input message: "Deploy ${APP_NAME} v${APP_VERSION} to the app server?", ok: 'Deploy'
                    }
                }
            }
        }

        stage('Prepare Artifact') {
            steps {
                sh '''
                    set -e

                    mkdir -p "${ANSIBLE_DIR}/deploy"

                    cp "target/${APP_NAME}-${APP_VERSION}.jar" \
                       "${ANSIBLE_DIR}/deploy/java-app.jar"

                    echo "Artifact prepared:"
                    ls -lh "${ANSIBLE_DIR}/deploy/java-app.jar"
                '''
            }
        }

        stage('Deploy with Ansible') {
            steps {
                echo "Deploying ${APP_NAME} version ${APP_VERSION} to app server on port ${APP_PORT}"

                sh '''
                    ansible-playbook \
                        -i "${ANSIBLE_DIR}/inventory" \
                        "${ANSIBLE_DIR}/deploy.yml" \
                        -e "app_version=${APP_VERSION}"
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    set -e

                    echo "======================================"
                    echo "Checking systemd service..."
                    echo "======================================"

                    ansible \
                        -i "${ANSIBLE_DIR}/inventory" \
                        app \
                        -m shell \
                        -a "systemctl is-active --quiet ${APP_NAME}"

                    echo ""
                    echo "======================================"
                    echo "Waiting for application on port ${APP_PORT}..."
                    echo "======================================"

                    ansible \
                        -i "${ANSIBLE_DIR}/inventory" \
                        app \
                        -m shell \
                        -a "for i in \\$(seq 1 30); do if curl -fsS --max-time 5 http://localhost:${APP_PORT} > /dev/null; then echo 'Application is healthy.'; exit 0; fi; echo \\\"Application not ready yet. Attempt \\$i/30...\\\"; sleep 2; done; echo 'Application failed health check after 60 seconds.'; systemctl status ${APP_NAME} --no-pager || true; journalctl -u ${APP_NAME} -n 50 --no-pager || true; exit 1"

                    echo ""
                    echo "======================================"
                    echo "Final HTTP health check..."
                    echo "======================================"

                    ansible \
                        -i "${ANSIBLE_DIR}/inventory" \
                        app \
                        -m shell \
                        -a "curl -fsS --max-time 10 http://localhost:${APP_PORT} > /dev/null"

                    echo "HTTP health check passed."
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
                Health URL  : http://localhost:${APP_PORT}
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
            echo "Pipeline failed or was not approved in time."
        }

        always {
            sh 'rm -rf "${ANSIBLE_DIR}/deploy" || true'
            cleanWs()
        }
    }
}
