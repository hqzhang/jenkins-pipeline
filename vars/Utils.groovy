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
