@Library('my-shared-library') _
pipeline {
    agent any
    parameters {
        choice (name: 'mychoice',choices: ['zhang','hongqi'],description: 'my choice para')
        string(name:'mystring',defaultValue: 'hongqi',description:'string para')
    }
    //echo "${params}"
    stages {
        stage('Build') {
            steps {
                script {
                    echo "I am jenkins_entry: master to run"
                    echo "qweqInput Parameters:${params}"
                    sayHello.xxx("hongqi zhang") 
               
                    
               
                
                 //z.checkOutFrom('git-plugin.git')
                }
            }
        }
        
    }
}
