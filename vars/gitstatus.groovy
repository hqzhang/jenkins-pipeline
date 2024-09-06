#!/usr/bin/env groovy
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.Field
import java.text.SimpleDateFormat
def copyFile(String srcFile, String destFile){
    def sourcePath = Paths.get(srcFile)
    def destinationPath = Paths.get(destFile)
    Files.copy(sourcePath, destinationPath,StandardCopyOption.REPLACE_EXISTING)
}

USERNAME='fredzhang123'


def executeCmd(String cmd, String directory){
    ProcessBuilder procBuilder = new ProcessBuilder("bash", "-c", cmd);
    procBuilder.directory(new File(directory))
    procBuilder.redirectErrorStream(true);
    def proc = procBuilder.start()
    proc.waitFor()
    
    def err=proc.exitValue()
    def reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))
   
    def line = null
    def output=''
    while ((line = reader.readLine()) != null) {
        output = output +line+ "\n"
    }
    
    println( "-----------------")
    println("exitValue: " + err)
    println( "-----------------")
    println output
    return output
}
def exeCmd(String cmd){
    
    def proc=cmd.execute()
    //def b = new StringBuffer()
    //proc.consumeProcessErrorStream(b)
    proc.waitFor()
    def out=proc.in.text
    def err=proc.err.text
    def code=proc.exitValue()
    //println ("out:$out")
    println("err:$err")
    println ("code=$code")
    return out
}
def gitPrep(String workbr, String mergebr, String directory){
    
    def command = "git branch -r ;git checkout $workbr; git branch; git pull origin $mergebr"
    def output = executeCmd(command, directory)
    
    return output
}
def uploadFile(String fileName,String workbr, String workspace, String repo){
    println "enter uploadFile()"
    def cmd = " curl -v -u ${USERNAME}:${PASSWORD} \
              -X POST https://api.bitbucket.org/2.0/repositories/${workspace}/${repo}/src\
              -F ${fileName}=@${fileName}  \
              -F message=updatecurl -F branch=${workbr}"
    println cmd
    def output=exeCmd(cmd)
    return output
}

def gitFinal(String src, String workbr, String mergebr, String directory){
    println "enter gitFinal()"
    def dest="$directory/CI.yml"
    def cmd="""pwd; rm -rf upload-test; git clone https://fredzhang123:$token@bitbucket.org/wave-cloud/upload-test.git -b workbr ."""
    println cmd
    //def out= executeCmd(cmd, directory)
    cmd="""pwd; git remote set-url origin https://fredzhang123:$token@bitbucket.org/wave-cloud/upload-test.git """
    println cmd
    def out = executeCmd(cmd, directory)

    println "git preparation"
    //cmd = "git branch -r ;git checkout $workbr; git pull origin $mergebr"
    //out = executeCmd(cmd, directory)
    //println "git copy update.."
    //copyFile(src, dest)

    //println "git push ..."
    //cmd = "git status; git add . ; git commit -m up; git push"
    //out = executeCmd(cmd, directory)
    uploadFile( fileName, workbr)
    return out
}
//@NonCPS
def getPrid(String repoPR){
    def ret=null
    def cmd="curl -u $USERNAME:$PASSWORD -X GET ${repoPR}?state=OPEN "
    def out=exeCmd(cmd)
    println "out=$out"
    
        def json=new JsonSlurper()
        def obj=json.parseText(out)
    if (obj.size != 0){
        ret=obj.values[0].id
    }
    println("prid=$ret")
    return ret
}

def getMergestatus(String repoPR, int prid){
    def cmd="curl -u $USERNAME:$PASSWORD -X GET ${repoPR}/$prid/merge"
    def output=exeCmd(cmd)
    def json=new JsonSlurper()
    def obj=json.parseText(output)
    return obj.canMerge
}

import java.text.SimpleDateFormat
def getDate(){
    def sdf = new SimpleDateFormat("yyyy-mm-dd")
    return sdf.format(new Date())
}

def createPR(String workbr, String mergebr,String workspace, String repo){
    def repoPR="https://bitbucket.org/api/2.0/repositories/$workspace/$repo/pullrequests"
    def date=getDate()
    def data=[ title: "Dependency Updates $date",
        description: "Automated Dependency Updates for $date",
        source: [ branch:  [ name: "$workbr" ],
                  repository: [ full_name: "$workspace/$repo" ] ],
        destination: [ branch:  [ name: "$mergebr" ] ],
        close_source_branch: true ]
    
    def body=JsonOutput.toJson(data)
    println body
    body=JsonOutput.toJson(body)
    println body
    def cmd=""" curl -u $USERNAME:$PASSWORD -X POST ${repoPR} --data $body """
    println cmd
    def output=exeCmd(cmd)
    println output
    def json=new JsonSlurper()
    def obj=json.parseText(output)

    return obj.id
}

def mergePR(String repoPR){
    def prid=getPrid(repoPR)
    println("prid=$prid")
    //System.exit(1)
    //def version=getVersion(repoPR)
    //println ("version=$version")
    def data=[ 
        type: "hqsettype",
        message: "good work",
        close_source_branch: true,
        merge_strategy: "merge_commit"
    ]
    def body=JsonOutput.toJson(JsonOutput.toJson(data))
    def cmd="""curl -u $USERNAME:$PASSWORD -X POST --header 'Content-Type: application/json' \
        '${repoPR}/$prid/merge' \
        --data '{ "type": "anytype", 
         "message": "good work", 
         "close_source_branch": true,
          "merge_strategy": "merge_commit" 
          }'"""
    //def cmd="""curl -u $USERNAME:$PASSWORD -X POST -H "Content-Type: applicatin/json" $repoPR/$prid/merge?version=$version"""
   println cmd
    def output=exeCmd(cmd)
    println output
}
def gitClone(String workspace, String repo, String workbr, String directory){
    println "enter gitClone()"
    //def dest="$directory/CI.yml"
    def cmd="""rm -rf upload-test/*; git clone https://${USERNAME}:$PASSWORD@bitbucket.org/$workspace/${repo}.git -b ${workbr} ."""
    println cmd
    return  executeCmd(cmd, directory)
}
//@NonCPS
def getConfig(String workspace, String repo, String workbr, String directory){
    println "enter getConfig()"
    def cmd="""git remote set-url origin https://${USERNAME}:$PASSWORD@bitbucket.org/$workspace/${repo}.git """
    println cmd
    return  executeCmd(cmd, directory)
}
def updateAll(String src, String workspace, String repo, String workbr, String mergebr, String directory){
    println "enter gitFinal()"
    def dest="$directory/CI.yml"
    def repoPR="https://api.bitbucket.org/2.0/repositories/$workspace/$repo/pullrequests"
    
    println("1.  git clone..")
    def out=gitClone(workspace, repo, workbr, directory)
    println out

    println("2.   git config..")
    out=getConfig(workspace, repo, workbr, directory)
    println out

    println "3.   git push ..."
    out=uploadFile(src, workbr)
    println out

    println "4.   git createPR ..."
    //out=createPR(workbr, mergebr, workspace, repo)
    println out//createPR(String workbr, String mergebr,String workspace, String repo){

     println "5.   git mergePR ..."
    out=mergePR(repoPR)
    println out

    return out
}

   def workbr='feature-test'
    def mergebr='main'
    def directory='/tmp/upload-test'
    def src='/tmp/CI.yml'
   // def project="GRP"
    def workspace='wave-cloud'
    def repo="upload-test"
    def repoPR="https://api.bitbucket.org/2.0/repositories/$workspace/$repo/pullrequests"
    def fileName='CI.yml'
createPR(workbr, mergebr,workspace, repo)
//println updateAll(src, workspace, repo, workbr, mergebr, directory)//println uploadFile('README.md', workbr)
/*def proc = "ls -al".execute()
proc.waitFor()
println proc.in.text
println  proc.err.text 
println proc.exitValue()*/ 
def mycheck() {
    def mylist="aa bb"
   
    def flag="tt"
    def timeout=25*1000
    def len=timeout/5000
    
    for( int i; i<len; i++ ){
      int ii=i*5
      println("wait time:"+ii.toString())
      Thread.sleep(5000)
      int pass=1
      for( var in mylist.split()) {
            println "var =$var"
            if ( flag == 'tt' ){
                println "check $var success"
                pass *=1
                continue;
            }else {
                println "wait to check $var again" 
                pass *=0
            }
       }
       if ( pass == 1){
           println "Check  SUCCESS"
           return
       }
    }
    println "Check FAILURE"
}
fileName="test.xml"


sdf = new SimpleDateFormat("yyyy-MM-dd")
println sdf.format(new Date())


//println getPrid(repoPR)
//println createPR(repoPR)//