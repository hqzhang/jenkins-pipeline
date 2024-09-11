
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions
import jenkins.model.Jenkins
import jenkins.*
import hudson.*
import hudson.model.*
import hudson.model.Run
import groovy.json.JsonSlurper
import groovy.json.JsonOutput 
import jenkins.model.*

//import groovy.yaml.YamlSlurper
//// Remove everything which is currently queued/

@groovy.transform.Field
def githubtokenid='myjenkinspipelinekey'

@groovy.transform.Field
def restAPIHub='https://api.github.com/repos/hqzhang/solution-repo'
@groovy.transform.Field
def folder='release'
@groovy.transform.Field
def mybranch='getsolution'

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials

def getToken(String id){
  def credential = CredentialsProvider.lookupCredentials(
      StandardUsernamePasswordCredentials.class,
      Jenkins.instance,null,null).find { it.id == 'myjenkinspipelinekey' }
  return  credential.password
}


def execmd(String cmd, String directory){
    ProcessBuilder procBuilder = new ProcessBuilder("bash", "-c", cmd);
    procBuilder.directory(new File(directory))
    procBuilder.redirectErrorStream(true);
    def proc = procBuilder.start()
    proc.waitFor()
    def err=proc.exitValue()
    def reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))
    def line = null
    def out=''
    while ((line = reader.readLine()) != null) { out += line+ "\n" }
    if ( err != 0){
        println("ERROR: $out")
        error("Debug for cmd:$cmd")
    }
    return out
}
def execmd(String cmd){
    def proc=cmd.execute()
    proc.waitFor()
    def out=proc.in.text
    def err=proc.err.text
    def code=proc.exitValue()
    if ( code !=0 ) {
        println("Error11:$err")
        error("Jump out for debug")
    }
    return out
}
def test(){
    def cmd="ssh -q -t hongqizhang@localhost < /var/root/.jenkins/workspace/agroovytest/run.sh "
    def dir="/var/root/.jenkins/workspace/agroovytest"
    def out=exeCmd(cmd,dir)
    println "out=$out"
    println "end test()----------2----------" 
}
def getPackList(String mypath){
    println "enter getPackList()"
    def out="ssh hongqizhang@localhost ls ${mypath}/*.tar.gz".execute().text
    out=out.readLines().collect{ it }
    println "out=$out"
    return out
}

def getCompList(String mypack){
    println "enter getCompList()"
    def mykey=mypack.split("\\/")[-1].split("\\.")[0] 
    println "mykey=$mykey"
    def map=[:], lss=[], key='', sel=':selected';
    def out="ssh hongqizhang@localhost /Users/hongqizhang/workspace/myscripts/run.sh".execute().text
    out.readLines().each{ if (it.contains(".tar.gz") ){
    if ( ! key.isEmpty()){ map[key]=lss; key=it.split("\\.")[0]; lss=[] } else { key=it.split("\\.")[0] } }
    else { lss.add(it+sel) } } ; map[key]=lss
    println "map=$map"
    println "mkey=${map[mykey]}"
    return """def map=[:], lss=[], key='', sel=':selected';
    |def mykey=mypack.split("\\\\/")[-1].split("\\\\.")[0] 
    |def out="ssh hongqizhang@localhost /Users/hongqizhang/workspace/myscripts/run.sh".execute().text
    |out.readLines().each{ if (it.contains(".tar.gz") ){
    |if ( ! key.isEmpty()){ map[key]=lss; key=it.split("\\\\.")[0]; lss=[] } 
    |else { key=it.split("\\\\.")[0] } }
    |else { lss.add(it+sel) } } ; map[key]=lss
    |return map[mykey]
    | """.stripMargin()
}
def getCompVerify(){
    println "-------------getCompVerify()------------------------"
    def mypack='/root/workspace/myscripts/file.tar.gz'
def map=[:], lss=[], key='', sel=':selected';
def mykey=mypack.split("\\/")[-1].split("\\.")[0] 
def out="ssh hongqizhang@localhost /Users/hongqizhang/workspace/myscripts/run.sh".execute().text
out.readLines().each{ if (it.contains(".tar.gz") ){
if ( ! key.isEmpty()){ map[key]=lss; key=it.split("\\.")[0]; lss=[] } 
else { key=it.split("\\.")[0] } }
else { lss.add(it+sel) } } ; map[key]=lss
println "map=$map"
return map[mykey]

}

def checkBuildRunning(){
    //Jenkins.instance.queue.clear()
   
    //def mybuild = Jenkins.instance.getQueue().getItems()[0].getFuture().get()
    //def workspace = mybuild.getWorkspace().getRemote()
    //println("Workspace: " + workspace)

    def buildingJobs = Jenkins.instance.getAllItems(Job.class).findAll {
        it.isBuilding()
    }
    buildingJobs.each {
        def jobName = it.toString()
        def val = jobName.split("\\[|\\]")
        println "val=$val"
        // 'Abort jobs' is the name of the job I have created, and I do not want it to abort itself.
        if((val[1].trim())!='Abort jobs') {
            def job = Jenkins.instance.getItemByFullName(val[1].trim())
            //println Jenkins.instance.getWorkspaceByFullName(val[1].trim())
            for (build in job.builds) {
                //println "build=$build"
                if (build.isBuilding()) {
                    //println ("currentbuild:"); println currentBuild
                    println("build:"); println build
                    //build.doStop();
                }
            }
        }
    }
}

String buildScript(List values){
    def ret=values.collect { '"'+it+'"' }
    return "return ${ret}"
}

String convertScript( String str){
    def ret='"""'+str.replaceAll('"', '\\\\"')+'"""'
    return  "return $ret"
}
def getBranch(){
    println("Enter getBranch()")
    return scm.branches.toString().substring(3,9)
}

def getURL(){
    println("Enter getURL()")
    def obj=scm.userRemoteConfigs[0]
    println obj.getUrl()
    println obj.getCredentialsId()
    println obj.getRefspec()
    println obj.getName()
    return scm.userRemoteConfigs[0].getUrl()
}

def initEnvars(){
    println("Enter initEnvars()")
    def wksp = getClass().protectionDomain.codeSource.location.path
    env.scmWksp = wksp.replace('/jobs/','/workspace/').split('/builds/')[0]
    env.jobName = env.JOB_NAME
    env.buildNo=env.BUILD_NUMBER
    env.scmURL = scm.userRemoteConfigs[0].getUrl()
    env.scmBranch = scm.branches[0].toString().replace('*/','')
}
def stringConvert(String data){
    return data.replaceAll(/\\n/,'\n').replace(/,/,'')
}

def getFileContent(String SolutionDetail){
    def wksp=getWksp()
    def url="https://raw.githubusercontent.com/hqzhang/ansibletest"
    def mf ="ls ${wksp}/release  ".execute().text
    my_tag="curl -k https://raw.githubusercontent.com/hqzhang/ansibletest/main/release/${SolutionDetail}.xml".execute().text 
    def ret= "<textarea name=\"value\"  value  class=\"setting-input  \" type=\"text\">${my_tag}</textarea>"
    convertScript(ret)
}

def parseSolution(String data){
    def flag=false
    def start=true
    type=''
    def lss=[]
    def map=[:]
    rawdata.split("\n").each {
        kv=it.tokenize(":")
        if (it.contains('name')){
            if ( !start ) {
            lss.add(map); map=[:]
            map[kv[0]]=kv[1]
            } else { start=false }
            map[kv[0]]=kv[1]
        } else if (it.contains('daemon')){ 
            flag=true; return true
        } else if ( flag && it.contains('type')){ 
            type=kv[1]; return true
        } else if (type != ''){
            lss.each { cmp->
                if (cmp['type']==type){
                    cmp['version']=kv[1];type = ''
                }
            }
        } else { map[kv[0]]=kv[1]}
    }
    return lss
}

def getSolution(String refvar ){
    println "enter getSolution()=========================="
   def ref='OKNG'
   def script="./run.sh ${ref}"
   def out="ssh hongqizhang@localhost ${script}".execute().text
   println "out=$out"
   return """def script="./run.sh \${${refvar}}"
      |def out="ssh hongqizhang@localhost \${script}".execute().text
      |return \"\"\" <textarea name=\"value\"  value  class=\"setting-input  \" type=\"text\" rows="8" cols="25">\${out}</textarea> \"\"\"
      | """.stripMargin()
}

def getContent(String refvar ){
   def url="https://raw.githubusercontent.com/hqzhang"
   def urlext=""
   def wksp=getWksp()
    def repo=getRepo()
    def brch=getBranch()
   return """def wksp=\"${wksp}\"
      |def url=\"${url}\"
      |def urlext=\"${urlext}\"
      |//def map="curl -k \${url}/${repo}/${brch}/release/\${${refvar}}.yaml\$urlext".execute().text
      |def map="cat \${wksp}/release/\${${refvar}}.yaml".execute().text 
      |map=map.replaceAll('components:\\n','')
      |return \"\"\" <textarea name=\"value\"  value  class=\"setting-input  \" type=\"text\" rows="8" cols="25">\${map}</textarea> \"\"\"
      | """.stripMargin()
}
def getContentSimple(String refvar ){
    def wksp=getWksp()
    def repo=getRepo()
    def brch=getBranch()
    def url="https://raw.githubusercontent.com/hqzhang"
    def urlext=""
    return """
      |def out="cat ${wksp}/release/\${${refvar}}.yaml".execute().text
      |if (out.isEmpty()) { out="curl -k ${url}/${repo}/${brch}/release/\${${refvar}}.yaml${urlext}".execute().text }
      |out=out.replaceAll('components:\\n','')
      |return \"\"\" <textarea name=\"value\"  value  class=\"setting-input  \" type=\"text\" rows="10" cols="25">\${out}</textarea> \"\"\"
      | """.stripMargin()
}
def getContentTest(String refvar ){
    println "enter getContentTest()........."
   return """
   |return getContentSimpleTest(${refvar})
   | """.stripMargin()
}
def getVerify(){
     println "enter getVerify()........."
    def refvar='solution'
    return getContentSimpleTest(refvar)
}
def getContentSimpleVerify(){
    println "enter getContentSimpleverify()================74582375270=================="
    def SolutionConfig='solution'
    def out="cat /var/root/.jenkins/workspace/acgroovytest/release/${SolutionConfig}.yaml".execute().text
    if (out.isEmpty()) { out="curl -k https://bitbucket.org/wave-cloud/groovytest/raw/mymenuchange/release/${SolutionConfig}.yaml".execute().text }
    out=out.replaceAll('components:\n','')
    return """ <textarea name="value"  value  class="setting-input  " type="text" rows="10" cols="25">${out}</textarea> """
}
def getContentTableVerify(){
def wksp="/var/root/.jenkins/workspace/agroovytest"
def url="https://raw.githubusercontent.com/hqzhang"
def ret=''
//ret="curl -k ${url}/groovytest/mymenu/release/${Config}.yaml".execute().text
ret="cat ${wksp}/release/${Config}.yaml".execute().text
ret=(Map)new Yaml().load(ret)
def rendered = "<table><tr>"
ret['components'].each { mark="-"; 
 it.each { kk,vv->
  if ( kk != "name") {  mark="&nbsp;&nbsp;" }
  rendered = """${rendered}<tr>
  <td><input name="value" alt="${kk}" json="${kk}" type="checkbox" style="opacity:0" class=" " checked>
  <span>${mark}&nbsp;</span>
  <label name="value" class=" " value="${kk}">${kk}</label></td>
  <td><input  type="text" class=" " name="value" value="${vv}"> </br> </td></tr> """    } }
return "${rendered}</tr></table>"
}

def getContentTable(String refvar){
    println "enter getContentTable()=================================="
    def url="https://raw.githubusercontent.com/hqzhang"
    def urlext=""
    def wksp=getWksp()
    def repo=getRepo()
    def brch=getBranch()
    return """import org.yaml.snakeyaml.Yaml
    |def wksp=\"${wksp}\"
    |def url=\"${url}\"
    |def ret=''
    |//ret="curl -k \${url}/${repo}/${brch}/release/\${${refvar}}${urlext}".execute().text
    |ret="cat \${wksp}/release/\${${refvar}}".execute().text
    |ret=(Map)new Yaml().load(ret)
    |def rendered = "<table><tr>"
    |ret['components'].each { mark="-"; 
    | it.each { kk,vv->
    |  if ( kk != "name") {  mark="&nbsp;&nbsp;" }
    |  rendered = \"\"\"\${rendered}<tr>
    |  <td><input name=\"value\" alt=\"\${kk}\" json=\"\${kk}\" type=\"checkbox\" style=\"opacity:0\" class=\" \" checked>
    |  <span>\${mark}&nbsp;</span>
    |  <label name=\"value\" class=\" \" value=\"\${kk}\">\${kk}</label></td>
    |  <td><input  type=\"text\" class=\" \" name=\"value\" value=\"\${vv}\"> </br> </td></tr> \"\"\"    } }
    |return "\${rendered}</tr></table>"
    |""".stripMargin()
}
def getRepo(){
    def repo=scm.getUserRemoteConfigs().toString()
    repo=repo.substring(17, repo.length()-12).split('/')[2]
    return  repo
}
def getJob(){
    def jobstr = Thread.currentThread().toString().substring(38,).split('/')[0]
    return jobstr
}
def getWksp(){
    def ret = getClass().protectionDomain.codeSource.location.path
    ret = ret.replace('/jobs/','/workspace/').split('/builds/')[0]
    return ret
}
def stringParse(String str){
    def data=str.split(',')
    def len= data.size()/2
    def ret=[:]
    def lss=[]
    def map=[:]
    for (int i = 0; i < len; i++) {
        if ( data[2*i] != data[0] ){
            map[ data[2*i]] = data[2*i+1]
        }else{
            if ( i != 0 ){ 
               lss.add(map)
               map=[:]  
            }
            map[ data[2*i]] = data[2*i+1]
        }
    }
    lss.add(map)
    println "lss=$lss"
    return [ components: lss ]
}

def getFileList(String dft){
    println "enter getFileList().."
    def wksp=getWksp()
    def mf ="ls ${wksp}/release  ".execute().text
    println "mf1=$mf"
    def out=mf.readLines().collect{  it.split("\\.")[0] } 
    println "out2=$out"
    def index=0
    if (out.isEmpty()){
        return [ dft ]
    }
    out.eachWithIndex{ it, id-> 
        if ( it.contains(dft) ){ index=id }
    }
    out.add(0, out.remove(index))
    println "out3=$out"
    return out 
}
def getFile(String mypath,String type, String dft){
  println("enter test()")
  def out="ssh root@192.168.0.16 ls ${mypath}/*.${type}".execute().text
  out=out.readLines().collect{ it.split("/")[-1] }
  out.eachWithIndex{ it, id-> if ( it.contains(dft) ){ index=id } }
  out.add(0, out.remove(index))
  return out
}
def getFileList88(String dft){
    def wksp=getWksp()
    def mf ="ls ${wksp}/release  ".execute().text
    def out=mf.readLines().collect{ it.split("\\.")[0] }
    return out
}

def getFileDft(String dft){
    def wksp=getWksp()
   return """def wksp=\"${wksp}\"
      |def mf ="ls \${wksp}/release  ".execute().text
      |def out=mf.readLines().collect{ 
      |    if(it.contains(\"$dft\") ){ '"'+it.split("\\\\.")[0]+':selected"' } 
      |    else { '"'+it.split("\\\\.")[0]+'"' } }
      |return \"\"\"return \$out \"\"\"
      | """.stripMargin()
}

def readYamlString(String str){
    return  (Map) new Yaml().load(str)
}
def readYamlFile(String fileName){
    String fileConts = new File(fileName).text
    return  (Map) new Yaml().load(fileConts)
}

def writeYamlFile(output,data){
    println("INPUT:"+data)
   
    DumperOptions options = new DumperOptions()
    options.setPrettyFlow(true)
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    yaml = new Yaml(options)
    yaml.dump(data, new FileWriter(output)) 
}

def getFileBitScript(String refvar){
    def repos="hqzhang/solution-repo"
    def brch="getsolution"
    def folder="release"
    return """import groovy.json.JsonSlurper
    |def ret=[]
    |if ($refvar == 'on') return null
    |def branch="git ls-remote https://hqzhang@bitbucket.org/${repos}.git -b ${brch}".execute().text.substring(0,40)
    |def out="curl https://api.bitbucket.org/2.0/repositories/${repos}/src/\${branch}/${folder}".execute().text
    |def obj=new JsonSlurper().parseText(out)
    |obj['values'].each { ret.add(it['path'])}
    |if (ret.isEmpty()) {return ['NotFound']}
    |return ret
    | """.stripMargin()
}

def getContentScript(String refvar){
    println("enter getContentScript()....")
    def repos="hqzhang/solution-repo"
    def branch="getsolution"
    return """def branch="git ls-remote https://hqzhang@bitbucket.org/hqzhang/solution-repo.git -b getsolution ".execute().text.substring(0,40)
    |def out="curl https://bitbucket.org/${repos}/raw/\${branch}/\${${refvar}}".execute().text
    |out=out.replaceAll('components:\\n','')
    |return \"\"\" <textarea name=\"value\"  value  class=\"setting-input  \" type=\"text\" rows="10" cols="25">\${out}</textarea> \"\"\"
    | """.stripMargin()
}
def getString(String refvar){
    return """return \"\"\" <textarea name=\"value\"  value  class=\"setting-input  \" type=\"text\" rows="1" cols="1">\${${refvar}}</textarea> \"\"\" """
}
def getStringInstant(String refvar){
    return """return \${${refvar}} """
}


def verify1(){
    println("enter verify1().Shell...")
    return new GroovyShell().evaluate(getFileBitScript())
}

def verify2(String yamfile){
    println("enter verify2()....")
    def str="""def SolutionConfig="${yamfile}";"""+\
       getContentScript("SolutionConfig")
    println str
    //return new GroovyShell().evaluate(getContentScript("SolutionConfig"))
    return new GroovyShell().evaluate(str)
}
def getTypeScript(String refvar){
    println("enter getTypeScript()")
    def wksp=getWksp()
    return """import org.yaml.snakeyaml.Yaml
    |def ret=[],type=''
    |def fileName="${wksp}/solution/\${${refvar}}/solution.yml"
    |String fileConts = "cat \$fileName".execute().text.replaceAll('- !component\\n','').replaceAll('components:\\n','')
    |fileConts.readLines().eachWithIndex {it, idx-> var=it.split(': ') 
    |   if (idx%2==0) { type=var[-1]} else { ret.add( type+'/'+var[-1])}   }
    |return ret
    | """.stripMargin()
}

def getRollbackList(String refvar, String filter){
    def wksp=getWksp()
    return """
    |def ret=["ABC"]
    |ret.add(0,${refvar})
    |if ( ${refvar} !=  'on') { return ["Empty"] }
    |def out ="ls ${wksp}/solution".execute().text
    |out.readLines().each { if ( it.contains(\"${filter}\") )  { ret.add(0, it) }    } 
    |return ret
    | """.stripMargin()
}

def buildScript(List ls,String dft){
    def ret = []
    ls.each { 
        if ( it.contains(dft)) { ret.add(0,'"'+it+'"') } 
        else { ret.add('"'+it+'"') } }
    return "return ${ret}"
}

def getServerScript(String ref){
    def map=[ DEV: ['server1','server2']]
    return """
    |def map=${map}
    |return map[${ref}]
    |""".stripMargin()
}

def getFileHubFullSW(){
    println("Enter getFileHubFullSW()")
    def baseUrl="${restAPIHub}/git/trees"
    return """
    |import groovy.json.JsonSlurper
    |import com.cloudbees.plugins.credentials.CredentialsProvider
    |import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
    |import hudson.security.ACL
    |import jenkins.model.Jenkins
    |def envar='DEV'
    |def ret=['INIT.yaml']
    |try {
    |   def credential = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class,
    |                    Jenkins.instance,ACL.SYSTEM,[]).find { it.id == '${githubtokenid}' }
    |   def token=credential.password
    |   def cmd=\"\"\"curl -kLs -H "Authorization: Bearer \${token}" ${baseUrl}/${mybranch}?recursive=2 \"\"\"
    |   def out=new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    |   def obj=new JsonSlurper().parseText(out)
    |   obj['tree'].each {
    |       def var=it['path'].replaceAll('${folder}/','')
    |       if ( !(var in ret) && it['path'].contains('${folder}') ){ ret.add(var) }  }  }
    |catch (Exception e) { ret.add( e.message) }
    |return ret
    |""".stripMargin()
}

def getContentInstant(String ref ){
    println("Enter getContentInstant()")
    def baseUrl="${restAPIHub}/contents/${folder}"
    return """
    |import groovy.json.JsonSlurper
    |import com.cloudbees.plugins.credentials.CredentialsProvider
    |import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
    |import jenkins.model.Jenkins
    |def ret = ''
    |if ( ${ref} == null || ${ref}.isEmpty() ) { return null }
    |try {
    |   def credential = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class,
    |                    Jenkins.instance,null,null).find { it.id == '${githubtokenid}' }
    |   def token=credential.password
    |   def cmd=\"curl -kLs -H 'Authorization: Bearer \${token}' -H 'Accept application/vnd.github.v3.raw' ${baseUrl}/\${${ref}}?ref=${mybranch} \"
    |   def out=new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    |   def obj=new JsonSlurper().parseText(out)['content'].replaceAll('\\\\s','')
    |   ret=new String(Base64.decoder.decode(obj), "UTF-8") 
    |   ret=ret.replaceAll('components:\\n','') }
    |catch (Exception e) { ret += e }
    |return \"<textarea name='value' rows='10' cols='120' >\${ret}</textarea>\"
    |""".stripMargin()

}

def getSolutionBackup(String ref){
    println("Enter getSolutionBackup()")
    return """import org.yaml.snakeyaml.Yaml
    |def ret='',single='S',name='',version=''
    |try {
    |def obj=new Yaml().load(${ref})
    |name=obj[0]['name']
    |version=obj[0]['Path'].split('/')[-1]
    |if (obj.size()> 1){ single='M' } 
    |ret = 'DEV-'+name+'-'+single+'-'+version+'.yaml' }
    |catch (Exception e) { ret += e }
    |return \"<textarea name='value' rows='1' cols='60' >\${ret}</textarea>\"
    |""".stripMargin()
}
def getRollBackScript(){}

def saveSolutionBackup(String solutionBackupPath){
    println( "Enter saveSolutionBackup:${solutionBackup}")
    def solutionBackup=solutionBackupPath.split('/')[-1]
    def baseUrl="${restAPIHub}/contents/${folder}"
    def sha = commandExecute("cat ${solutionBackupPath}| git hash-object --stdin").trim()
    def content = commandExecute("base64 ${solutionBackupPath}")
    println "content=$content"
    def msg=""
    def token=getToken(githubtokenid)
    
    def cmd="curl -kls -w '%{http_code}' -H 'Authorization: Bearer ${token}' ${baseUrl}/${solutionBackup}?ref=${mybranch} "
    def out=commandExecute(cmd)
    def obj=new JsonSlurper().parseText(out)

    println "obj.sha=${obj.sha}"
    println "obj=$obj"
    if (obj.sha != null  ) { msg='Update file message'}
    else { msg='Create file message' }

    println "sha=$sha"
    println "obj.sha=${obj.sha}"
    if (obj.sha != sha ) {
        println "create or update file !!!! ${obj.sha}"
        sha=obj.sha
        body=[  branch: "${mybranch}",
                message: "${msg}",
                committer: [ name: 'hongqi',email: 'hq@hotmail.com'],
                content: "${content}",
                sha: "${sha}"]
        if (obj.sha == null)  { 
            body.remove('sha')
            println "new body=$body"
        }
        
        body=JsonOutput.toJson(JsonOutput.toJson(body))
        println "create body=$body"
        cmd="curl -kLs -X PUT -o /dev/null -w '%{http_code}' -H 'Authorization: Bearer ${token}' \
            ${baseUrl}/${solutionBackup} --data ${body}"
        println "create cmd=$cmd"
        out=commandExecute(cmd).trim()
        println "result=$out"
        if (out!='200' && out!='201') { error("Create file Failure!!") }
        else { println "${msg} successfull"}
        return out
    } else { println "No need update file successfully"}

    return '200'
}

def commandExecute(String cmd){
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}
def executeCmd(String){
    def pb = new ProcessBuilder('sh','-c',cmd)
    pb.redirectErrorStream(true)
    def pr = pb.start()
    def err = pr.exitValue()
    return pr.text
}

def getPrintEnv(){
    return """
    |import groovy.json.JsonSlurper
    |import com.cloudbees.plugins.credentials.CredentialsProvider
    |import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
    |import jenkins.model.Jenkins
    |def ret = ''
    |try {
    |   def credential = CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class,
    |                    Jenkins.instance,null,null).find{ it.id == '${githubtokenid}' }
    |   def token=credential.password
    |   def cmd=\"printenv \"
    |   ret =new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text }
    |catch (Exception e) { ret += e }
    |return \"<textarea name='value' rows='5' cols='120' >\${ret}</textarea>\"
    |""".stripMargin()

}

def saveBackupFile(String components,String backupFile){
    println("Enter saveBackupFile: $components")
    println("And  BackupFile: $backupFile")
    def config = [:]
    configContent=stringConvert(components)
    println "content=$configContent"
    writeFile file: backupFile, text: 'components:\n'+configContent
    
    config=readYaml text: configContent
    return config
}

def deployInstallBin(String components, Map binPath){
    println("Enter deployInstallBin()")
    //def target='tgrpanvl@bagrpapv06'
    def config = setInstallPath(components)
    //println config
    
    config.each { comp ->
        //println comp
        baseBin = comp['file_sys']+'/bin'
        //println baseBin
        myPath = comp['Path'].replaceAll('\\$MKVBINDIR/','')
        //println myPath
        binPath.each { type, source ->
            if (type==comp['type']){
                cmd="""ssh -q -t ${target} "mkdir -p ${baseBin}/${myPath} && cp -rf ${source}/* ${baseBin}/${myPath}/" """
                println "cmd=$cmd"
                //commandExecute(cmd)
            }
        }
    }
}
def setInstallPath(components){
    println("Enter setInstallPath()")
    def myyaml=new Yaml()
    def config = stringConvert(components)
    config=new Yaml().load(config)
    //println config
    //def scmWksp="/Users/hongqizhang/workspace/jenkins-pipeline"
    
    def fileBase="${env.scmWksp}/solution/${env.destFile}"
    def machines=new File("${fileBase}/machines.yml").text.replaceAll(/!\w*/,'')
    machines=myyaml.load(machines)
    //println machines
    def solution=new File("${fileBase}/environment.yml").text.replaceAll(/!\w*/,'')
    solution=myyaml.load(solution)['daemons_allocation']
    //println solution
    file_sys=machines['file_sys'].values()[0]
    //println file_sys
    config.each { comp ->
        comp['file_sys']=file_sys
        if(!comp.containsKey('daemon_allocation')) {
            comp['daemon_allocation']=solution[comp['name']]
        }
        comp['daemon_allocation'].split().each{ daemon ->
            machines['machines'].each { machine ->
                if ( !daemon.contains('~') && machine.containsKey('file_sys') && daemon == machine['daemon_name']) {
                    comp['file_sys']=machine['file_sys']
                }
            }
        }
    }
}





