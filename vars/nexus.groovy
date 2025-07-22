//Nexus curl API

def nexusRepo = "https://repo.td.com/repository/${repository}/${upDir}/${fileNmae}"

def verify(String nexusRepo){
    def cmd = "curl -k -u ${user}:${pass} -w '%{http_code}' --fail --silent --head ${nexusRepo}"
    return commandExecute(cmd)
}

def upload(repository, uploadDir, source){
    def fileName=source.split('/')[-1]

    def nexusStaginguRL="htttps://repo.td.com/service/rest/v1/componets?repository=${repository}"
    def cmd = "curl -k -u ${user}:${pass} -w '%{http_code}'--progress-bar  -X POST ${nexusStaginguRL} \
              -F raw.directory=${uploadDir} \
              -F raw.asset1=@${source}  \
              -F raw.asset1.fileName=${fileName}"

    commandExecute(cmd)
    commandExecute("rm -rf soruce") 
    return commandExecute(cmd)
}

def delete(String nexusRepo){
    def cmd = "curl -k -u ${user}:${pass} -w '%{http_code}'--request  DELETE --silent ${nexusRepo} "
    return commandExecute(cmd)
}

def download(String nexusRepo){

    def cmd = "curl -k -u ${user}:${pass} -w '%{http_code}' --fail --progress-bar -O ${nexusRepo}"
    return commandExecute(cmd)
}

def nexusUploads(String repository, String uploadDir, String source){ 
    def fileNames=source.split().collect {  it.split('/')[-1] }.join(' ')
    def soruceList=source.split()

    fileNames.plit().eachWithIndex { fileName, index->
        def filepath="https://repo.td.com/repository/${repository}/${uploadDir}/${fileName}"
        def ret=verify(filepath)
        if(ret==0){
            println "Clean existed and then upload"
            delete(filePath)
        } 
        else { println "Not existed, upload" }
        upload(repository, uploadDir, sourceList[index])
    }
}

def partyVerify(){
    def fileNames=source.split().collect {  it.split('/')[-1] }.join(' ')
    def ret=0
    fileNames.split().eachWithIndex { fileName, index->
        def filepath="https://repo.td.com/repository/${repository}/${uploadDir}/${fileName}"
        def ret=verifyParty(filepath)
        if(ret==0){
            println "Clean existed and then upload"
            error("Nexus release is existed stop")
        } 
        else { println "Not existed, break" }
        upload(repository, uploadDir, sourceList[index])
    }


}
def finalVerify(String repository, String uploadDir, String source){ 
    def fileNames=source.split().collect {  it.split('/')[-1] }.join(' ')
    def ret=0
    for (int i=0; i< 200; i++){
        def ii=6*i
        println("wait time:"+ii+"seconds")
        sleep(time 6000, unit: "MILLISECONDS")

        def count=1
        fileNames.split().each { fileName ->
            def filepath="https://repo.td.com/repository/${repository}/${uploadDir}/${fileName}"
          
            if (repository == '3rd-Party'){
                ret=verifyParty(filepath) }
            else { 
                ret=verify(filepath) }

            if (ret==0){  count *=1 }
            else { count*=0 }
        }
        if (count==1){ println "SUCCESS: Upload Successfull"}
        else{ println "FAILURE: Upload Failed"}
    }
}

