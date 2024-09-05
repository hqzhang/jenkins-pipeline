
def call(String wksp, String repo ,String brch){
    def url="https://raw.githubusercontent.com/hqzhang"
    def urlext=""
    def mf ="ls ${wksp}/release  ".execute().text
    def out=mf.readLines().collect{ it.split("\\.")[0]}
    def map=[:]
    out.each { 
        map[it]="curl -k ${url}/${repo}/${brch}/release/${it}.yaml$urlext".execute().text
        if ( map[it].contains('404: Not Found')){ 
            map[it]="cat ${wksp}/release/${it}.yaml".execute().text 
    } }
    return map
}