def call()
    def key='' 
    def map=[:]
    def lss=[]
    def slt=':selected'
    def out="ssh root@192.168.0.16 /root/workspace/myscripts/run.sh".execute().text
    out.readLines().each{ if (it.contains(".tar.gz") ){
    if ( ! key.isEmpty()){ map[key]=lss; key=it.split("\\.")[0]; lss=[] } else { key=it.split("\\.")[0] } }
    else { lss.add(it+slt) } } ; map[key]=lss
    return map