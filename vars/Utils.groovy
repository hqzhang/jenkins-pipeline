@groovy.transform.Field
def urlBaseGithub='https://github.com/hqzhang/'
@groovy.transform.Field
def githubtokenid='myjenkinspipelinekey'

def getCloneGithub(String repoName, String repoBranch, String myDir=''){

  String tmp=myDir
  if (!myDir?.trim()) { tmp = repoName}

  checkout([$class: 'gitSCM',
            branches: [[name: '*/'+repoBranch]]
            extensions:[[$class: 'RelativeTargetDirectory', relativeTargetDir: myDir]]
            userRemoteConfigs: [[credentialsId: githubtokenid, url: urlBaseGithub+repoName]]
            ] )
}

def hello(){
  echo "Hello HQ"

}
def param="xyz"

def getSth(){
  def ret=getClass()
  println "getC=$ret"
  ret=ret.protectionDomain
  println "proDm=$ret"
  ret=ret.codeSource
  println "CodeS=$ret"
  ret=ret.location
  println "loc=$ret"
  ret=ret.path.replace('/jobs/','/workspace/').split('/builds/')[0]
  println "wksp=$ret"
  return ret
}
