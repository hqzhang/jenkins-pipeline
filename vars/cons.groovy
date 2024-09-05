def envList=['DEV','BAT','PAT','PROD','DR']
//def envList=['DEV','BAT']
//@groovy.transform.Field
//def componentTypes=['ROUTER_M','CPS']
def getDate(){
    return "date +%D".execute().text.replaceAll('\\/','-').replaceAll('\\n','')
}

@groovy.transform.Field
def dftCompList=['NY_APP_R','s11']

@groovy.transform.Field
def componentTypes=['ROUTER_M','CPS']

@groovy.transform.Field
def componentList=['ROUTER_M':['NY_APP_R','SG_APP_R'],
                    'CPS':['ANV_CPS_GL']
               ]
List getCompTypes(Map myList=componentList ){
   List tmp = []
   myList.each {
      tmp.add(it.key)
   }
   return tmp
}

String getComponents(String myenv,
                     List Types=[],
                     List defaultList=dftCompList,
                     Map  srcMap=componentList) { 
    def ret=''
    if (Types.isEmpty() ) { 
      Types = getCompTypes(srcMap) 
    }
    
    Types.eachWithIndex{ it,index->
            String myList = buildScriptDefault(srcMap[it], defaultList[index])
            String myStr ="if(${myenv}.equals('$it')){ $myList }"+"\n"
            if ( !ret?.trim() ) {  ret += myStr }
            else { ret += "else "+myStr }
    }
    ret += """else{  return ["N/A"] }"""
    return "$ret"
}

String getComponents1(String typeName,
                     List Types=componentTypes,
                     List defaultList=dftCompList,
                     Map  srcMap=componentList) { 

            def ret=''
            Types.eachWithIndex{ it, index->
                  String myList = buildScriptDefault(srcMap[it],dftCompList[index])
                  String myStr = "if(${typeName}.equals('$it')){ $myList }"+"\n"
                  if ( !ret?.trim() ) { ret += myStr }
                  else { ret += "else "+ myStr}
            }
            ret += """else{ return ["N/A"]}"""
}

@groovy.transform.Field
def url='git@github.com:hqzhang/octest.git'
@groovy.transform.Field
def serversList=['DEV': ['APP': ['s1','s22'],
                      'DB':['s3']],
              'BAT': ['APP': ['s3','s55'],
                      'DB':['s6']],  
              'PAT': ['APP': ['s5','s23'],
                      'DB':['s3']],
               'PROD': ['APP': ['s7','s24'],
                      'DB':['s3']],
               'DR': ['APP': ['s9','s25'],
                      'DB':['s3']],  
                'QA': ['APP': ['s11','s26'],
                      'DB':['s3']],    
            ] 
@groovy.transform.Field
def defaultList=['s22', 's55', 's23','s24','s25','s26']
String buildScript(List values){
   def ret=values.collect { '"'+it+'"' }
   return "return ${ret}"
}


@groovy.transform.Field
def serverUser='user'

String getTarget(String server)
{
   ret = server
    if ( server.equals('ERROR')) {
      ret = defalutList[0]
    }
   return "$serverUser@$ret"
}

String getComp(String comp)
{
    ret = comp
    if ( ret.equals('ERROR')) {
      ret = defalutCompList[0]
    }
   return ret
}
String buildScriptDefault(List values,String key){
   List tmp = []
   values.each{ 
      if ( it.contains(key) ) {
            tmp.add( it+":selected" )
      } else { 
            tmp.add( it)
      }
   }
   buildScript(tmp)
}     
String getServers(String myenv){
    def ret=''
    envList.eachWithIndex{ it,index->
            String myList = buildScriptDefault(serversList[it]['APP'], defaultList[index])
            String myStr ="if(${myenv}.equals('$it')){ $myList }"+"\n"
            if ( !ret?.trim() ) {  ret += myStr }
            else { ret += "else "+myStr }
    }
    ret += """else{  return ["Not QApplicable"] }"""
    return "$ret"
}

String getTags(String myurl='' ){
   if ( !myurl?.trim() ) { myurl = url  }
   def gettags = ("git ls-remote --refs --tags  $url  ").execute().text
   def ret = gettags.readLines().collect { it.split()[1].replaceAll('refs/tags/', '') }
   buildScript(ret)
}

String getDefaultTag( String myurl='' ){
   if (  !myurl?.trim() ) { myurl = url  }
   def gettags = ("git ls-remote --refs --tags  $myurl  ").execute().text
   def ret = gettags.readLines().collect { it.split()[1].replaceAll('refs/tags/', '') }[2]
   return '"'+ret+'"'
}

String getBranches( String myurl='' ){
   if ( !myurl?.trim() ) { myurl = url  }
   def gettags = ("git ls-remote --heads  $myurl  ").execute().text
   def ret = gettags.readLines().collect { it.split()[1].replaceAll('refs/heads/', '') }
   buildScript(ret)
}
String getFileBit(){
   println("Enter getFileBit()")
   //if ( !mycmd?.trim() ) { 
   def  mycmd = """curl -u "hqzhang:LknMlBaa0gKeZMCBweh3AFE0" -X GET https://bitbucket.org/rest/api/1.0/projects/myproject/repos/solution-repo/browse/release"""
   //}
   def mf = mycmd.execute().text
   def ret = mf.readLines().collect{ it.split()[0].minus('.xml')}
   buildScript(ret)
}

import groovy.json.JsonSlurper
String getFileHub(String repo, String folder ){
   def tmp=[]
   def cmd = """curl https://api.github.com/repos/${repo}/git/trees/master?recursive=2  """
   println "cmd=$cmd"
   def out = cmd.execute().text
   def jsonSlurper = new JsonSlurper()
   def obj = jsonSlurper.parseText(out)
   println "obj=$obj"
   
   obj['tree'].each{
      if(it['path'].contains("${folder}/") ) { tmp.add(it['path'])}
    }
   return tmp
}

String getFiles(String mycmd='' ){
   if ( !mycmd?.trim() ) { 
      mycmd = "ls /Users/hongqizhang/workspace/groovytest/mydir "
   }
   def mf = mycmd.execute().text
   def ret = mf.readLines().collect{ it.split()[0].minus('.xml')}
   buildScript(ret)
}

String getEnvironment(String val='',List values=[]){
   if ( values.isEmpty() ) { values = envList }
   if ( val?.trim() ) { 
      buildScriptDefault(values, val)
   } else {
      buildScript(values)
   }
}

println getFileHub("hqzhang/groovytest","releases")


