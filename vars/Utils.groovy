@groovy.transform.Field
def urlBaseGithub='https://github.com/hqzhang/'
@groovy.transform.Field
def githubtokenid='githubtokenid'

def gitCloneGithub(String repoName, String repoBranch, String myDir=''){

  String tmp=myDir
  if (!myDir?.trim()) { tmp = repoName}
  
  dir(tmp) {
    withCredentials([usernamePassword(
    credentialsId: 'githubtokenid',
    usernameVariable: 'GITHUB_USER',
    passwordVariable: 'GITHUB_TOKEN'
)]) {
    echo "GitHub username: ${GITHUB_USER}"

    // Use GITHUB_TOKEN here without printing it
    sh '''
        git ls-remote https://github.com/hqzhang/solution-repo.git
    '''
}
    //checkout([
    //    $class: 'GitSCM',
    //    branches: [[name: '*/' + repoBranch]],
    /*    userRemoteConfigs: [[
            credentialsId: 'githubtokenid',
            url: 'https://github.com/hqzhang/solution-repo.git'
        ]]
    ])*/
  }
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
