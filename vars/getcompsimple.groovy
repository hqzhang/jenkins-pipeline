
def call(String name, String wksp){
    //def url="https://raw.githubusercontent.com/hqzhang"
    //def urlext=""
    //out="curl -k ${url}/${repo}/${brch}/release/${it}.yaml$urlext".execute().text
    def ret="cat ${wksp}/release/${name}.yaml".execute().text 
    return ret
}