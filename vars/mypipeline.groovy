//////

def call(Map params){
println "begin pipeline=$params"
def map
def list
def remote = [:]
remote.name = "root"
remote.host = "192.168.0.16"
remote.allowAnyHosts = true
//println menu.getCompList('mypack')
//println menu.gitCompVerify()
pipeline {
    agent any
    stages {
        stage('Create List') {
            steps {
                script {
                    echo "STAGE: create List..."
                    println(menu.getWksp())
                    myMethods.greet("Alice")
                    echo "params======$params"
                    echo "components=${env.compNames}"
                    echo "init=$env.initMenu"
                    println "get use function"
                    def comps=["b","a"]
                    def versions=["1","2"]
                    comps.eachWithIndex{ var, id->
                          echo "com=$comps[id]"
                          echo "ver=$versions[id]"

                    }
                     
                        sh """
                        echo comp
                        """
                    //println menu.getCompList('mypack')
                   
                    
                   /* withCredentials([sshUserPrivateKey(credentialsId: 'b2c30d83-4635-4fe4-870c-91b9fafef3c9',
                             keyFileVariable: 'identity', passphraseVariable: '', usernameVariable: 'userName')]) {
                        remote.user = userName
                        remote.identityFile = identity
                  
                        writeFile file: 'abc.sh', text: 'ls'
                        sshCommand remote: remote, command: 'for i in {1..5}; do echo -n \"Loop \$i \"; date ; sleep 1; done'
                        sshPut remote: remote, from: 'abc.sh', into: '.'
                        sshGet remote: remote, from: 'abc.sh', into: 'bac.sh', override: true
                        sshScript remote: remote, script: 'abc.sh'
                       */
                    /*sh """
                        ls -al ./scripts/run.sh
                        chmod a+x ./scripts/run.sh
                        ./scripts/run.sh
                    """

                    if ( env.initMenu.toBoolean() == true ){
                        echo "Menu is initialized Successfully!"
                        return
                    }
                    echo "parsing yaml"
                    println ("Components={env.Components}") 
                    println ("Component={env.Component}")
                    if ( env.Components == '' ){
                        echo "Components is not SET"
                        sh 'exit 1'
                    }
                    def file = new File("${wksp}/releases/${filebackup}")
                    echo  "file Exist: ${file.exists()}"
                    if (file.exists()) {
                        echo "delete file"
                        file.delete()
                    }
                    echo  "file Exist: ${file.exists()}"
                   
                    println "for comma string"
                    def read = menu.stringParse(env.Components)
                    println read
                    
                    writeYaml file: "${wksp}/releases/${filebackup}", data: read
                    //writeFile file: "releases/${filebackup}", text: multiLineStr
                    //def st=new File("/path-to-file").exists()

                    //menu.writeYamlFile("releases/${filebackup}",read)
                    sh "cat releases/${filebackup}"

                     ///println for yaml string
                    def multiLineStr = params.Component.replaceAll(/\\n/,"\n").replaceAll(',','')
                    println "multiLineStr=$multiLineStr"
                     */
                    
                    
                    /*def targtServer=params.servers
                    echo "params=$params"
                    if ( targtServer.equals('ERROR') ) 
                    {
                        targtServer='s23'
                    }
                    
                    echo "targtServer=$targtServer"
                    workspace=WORKSPACE
                    println "WS=${env.WORKSPACE}"
                    println "WS=${WORKSPACE}"
                    println "pwd="+pwd()
                    println "workspace=$workspace"
                    // you may create your list here, lets say reading from a file after checkout
                    //list = ["Test-1", "Test-2", "Test-3", "Test-4", "Test-5"]
                    list = readXMLList("${workspace}/manifest_Lynx.xml")
                    echo "***************"
                    def jsonText = parseXML("${workspace}/manifest_Lynx.xml")
                    map = readJSON text: jsonText
                    echo "#################"
                     def remote = [:]
                    remote.name = 'test'
                    remote.host = '192.168.2.27'
                    remote.user = 'root'
                    remote.password = 'password'
                    remote.allowAnyHosts = true
    
                    sshCommand remote: remote, command: "ls -lrt"*/
    
                }
            }
        }
   }
}

}


