@Library('scm-builders') _ 
pipeline {
    environment {

        //-- Cloud provider - <provider>
        // AWS:      AWS
        // Google:   google
        cloudProvider       = 'google'

        //--- Cloud Disk Repository - <host>
        // AWS:         <user-account>.dkr.ecr.<zone>.amazonaws.com
        // Google:      gcr.io
        appDiskRepoDev      = "gcr.io/pass-223419/${getRepoName()}-develop"
        appDiskRepoHml      = "gcr.io/pass-223419/${getRepoName()}-homolog"
        appDiskRepoPrd      = "gcr.io/pass-223419/${getRepoName()}"

        //--- Secrets Repository - <account/repository.git>
        secretsRepository   = 'igta/pass-secrets.git'

        //--- Cloud Credencial - <credencial>
        // AWS:      ecr:<zone>:<credencial-name>
        // Google:   gcr:<credencial-name>
        
        cloudCredencialHml  = 'gcr:credencialhml'
        cloudCredencialPrd  = 'gcr:credencialprd'
        
        
        //--- K8s Namespaces
        namespacek8sDev     = 'develop'
        namespacek8sHml     = 'homolog'
        namespacek8sPrd     = 'default'

        //--- Notication Vars
        slackChannel        = '<channel>' 

        // Internal Control
        userInput = false
	}
    agent any
    stages {
        stage('Checkout') {			
            steps {
                script {
                    slackSend (channel: "#${slackChannel}", color: '#74E862', 
                        message: "START Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                    echo "Checkout ${env.BRANCH_NAME}..."
                    deleteDir()
                    git([url: getRepoUrl(), branch: env.BRANCH_NAME, credentialsId: appCredencial()])
               }
            }
        }
        stage('Build') {		
            steps {
                executeBuild()
            }
        }
        stage('Unit Test') {
            steps {
                executeUnitTest()   
            }
        }
        stage('Integration Test') {
			when {
                expression{ return (env.BRANCH_NAME == "develop")}
            }
           	steps {
				executeIntegrationTest()   
           	}
	    }
        stage('Sonar') {
            when {
                expression{ return (env.BRANCH_NAME == "develop")}
            }
            steps {
                script{
                    echo 'Sonar Execution..'
                    projectName = env.JOB_NAME.replace("/${env.BRANCH_NAME}",'')
                    projectKey = projectName.replace('-','.').toLowerCase()

                    withSonarQubeEnv('SonarQube') {

                        withMaven(maven : 'maven_default'){
                            SONAR_VALIDATION = sh (
                                script: "mvn sonar:sonar \
                                -Dsonar.projectKey=Pass \
                                -Dsonar.projectVersion=${env.BUILD_NUMBER} \
                                -Dsonar.projectName=\"${projectName}\"",
                                returnStdout: true).trim()
                                echo "Sonar validation status: ${SONAR_VALIDATION}"
                        }

                    }
                    // SONAR TRAVANDO NESTE PROJETO
                    echo 'Sonar Validation..'
                    qualitygate = waitForQualityGate()
                    if (qualitygate.status != "OK") {
                        error "Pipeline aborted due to quality gate coverage failure: ${qualitygate.status}"
                    }
                }
            }
        }
        stage('Send Alert') {
            when {
                expression { return (env.BRANCH_NAME.find("release"))}
            }
            steps {
                script {
                    if (slackChannel != '' && slackChannel != null ) {  
                        echo 'Send slack notification ...'
                        slackSend (channel: "#${slackChannel}", color: '#FFFF00', 
                            message: "Waiting Confirmation to Deploy: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                    } else echo 'Slack notification disable...'    

                    sendEmail('$DEFAULT_RECIPIENTS', "approve", 
                        "[JENKINS - RELEASE] Novo job aguardando aprova��o")
                }                 
            }
        }
        stage('Promotion RC?') {
			when {
                expression { return (env.BRANCH_NAME.find("release"))}
            }
			steps {
               script {
                    timeout(time: 30, unit: 'MINUTES') {
				        userInput = input(
								id: 'Proceed1', message: 'Was this successful?', parameters: [
								[$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Confirm deploy?']
								])
                   }                 
               }
            }
        } 	
        stage('Docker DEV') {
			when {
                expression { return (env.BRANCH_NAME == "develop")}
            }
            steps {
                pushImageToRepo("${appDiskRepoDev}",cloudCredencialHml, env.BUILD_NUMBER,"",cloudProvider)                               
            }
        }
        stage('Docker HML') {
			when {
                expression { return (env.BRANCH_NAME.find("release")  && userInput == true)}
            }
            steps {
                pushImageToRepo("${appDiskRepoHml}",cloudCredencialHml, env.BUILD_NUMBER,"",cloudProvider)                             
            }
        }
        stage('Deploy Services') {
			when {
                expression { return ( (env.BRANCH_NAME.find("release") && userInput == true) || env.BRANCH_NAME == "develop" )}
            }
			steps {
				echo 'Services apply ...'
               	script {
                    git([url: getRepoUrl(), branch: env.BRANCH_NAME, credentialsId: appCredencial()])   
                    if(env.BRANCH_NAME == "develop"){   
                        replaceContentFile('branch',env.BRANCH_NAME , 'k8s-service-develop.yaml')
                        replaceContentFile('appName',getRepoName(),'k8s-service-develop.yaml')
                        executeKubectl("k8s-service-develop.yaml",namespacek8sDev,cloudProvider)
                    } 
                    else if(env.BRANCH_NAME.find("release")){
                        replaceContentFile('branch',env.BRANCH_NAME , 'k8s-service-homolog.yaml')
                        replaceContentFile('appName',getRepoName(),'k8s-service-homolog.yaml')
                        executeKubectl("k8s-service-homolog.yaml",namespacek8sHml,cloudProvider)
                    }     
                }
            }
        }
        stage('Deploy Secret') {
			when {
                expression { return ( (env.BRANCH_NAME.find("release") && userInput == true) || env.BRANCH_NAME == "develop" )}
            }
			steps {
               	script {
                    git([url: getRepoSecrets(secretsRepository), branch: 'master', credentialsId: appCredencial()])
                    
                    if(env.BRANCH_NAME == "develop"){   
                        if (fileExists("${getRepoName()}/k8s-secret-develop.yaml")) {
                            echo 'Secret apply develop...'
                            replaceContentFile('branch',env.BRANCH_NAME , "${getRepoName()}/k8s-secret-develop.yaml")
                            replaceContentFile('appName',getRepoName(),"${getRepoName()}/k8s-secret-develop.yaml")
                            executeKubectl("${getRepoName()}/k8s-secret-develop.yaml",namespacek8sDev,cloudProvider)
                        }
                    } 
                    else if(env.BRANCH_NAME.find("release")){
                        if (fileExists("${getRepoName()}/k8s-secret-homolog.yaml")) {
                            echo 'Secret apply homolog...'
                            replaceContentFile('branch',env.BRANCH_NAME , "${getRepoName()}/k8s-secret-homolog.yaml")
                            replaceContentFile('appName',getRepoName(),"${getRepoName()}/k8s-secret-homolog.yaml")
                            executeKubectl("${getRepoName()}/k8s-secret-homolog.yaml",namespacek8sHml,cloudProvider)
                        }    
                    }     
                }
            }
        }
        
        stage('Deploy Image') {
            when {
                 expression { return ( (env.BRANCH_NAME.find("release") && userInput == true) || env.BRANCH_NAME == "develop" )}
            }
            steps {
                unstash 'app'
                script {
                    if (env.BRANCH_NAME == "develop") {
                        echo 'Deploying....Develop'
                        replaceContentFile('branch',env.BRANCH_NAME,'k8s-deployment-develop.yaml')
                        replaceContentFile('version',env.BUILD_NUMBER,'k8s-deployment-develop.yaml')
                        replaceContentFile('appName',getRepoName(),'k8s-deployment-develop.yaml')
                        executeKubectl("k8s-deployment-develop.yaml",namespacek8sDev,cloudProvider)
                    } else if (env.BRANCH_NAME.find("release")) {
                        echo 'Deploying....Homolog'
                        replaceContentFile('branch',env.BRANCH_NAME,'k8s-deployment-homolog.yaml')
                        replaceContentFile('version',env.BUILD_NUMBER,'k8s-deployment-homolog.yaml')
                        replaceContentFile('appName',getRepoName(),'k8s-deployment-homolog.yaml')
                        executeKubectl("k8s-deployment-homolog.yaml",namespacek8sHml,cloudProvider)
                    }
                } 
           }
        }   
        stage('Docker PRD') {
			when {
                expression { return (env.BRANCH_NAME.find("release")  && userInput == true)}
            }
            steps {
                pushImageToRepo("${appDiskRepoPrd}",cloudCredencialPrd, env.BUILD_NUMBER,'',cloudProvider)                
            }
        }
    }    
    post {
        success {
            archiveArtifacts "target/**/*"
            //junit 'target/surefire-reports/*.xml'
            slackSend (channel: "#${slackChannel}",color: '#62A7E8', 
                message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
        failure {
            slackSend (channel: "#${slackChannel}", color: '#E04422', 
                message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            sendEmail(emailextrecipients([ [$class: 'CulpritsRecipientProvider'] ]),
                "build-failure", "[JENKINS - ERRO] Build Failure")    
        }
    }     
}
