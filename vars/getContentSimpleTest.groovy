def call(String SolutionConfig){
    def out="cat /var/root/.jenkins/workspace/acgroovytest/release/${SolutionConfig}.yaml".execute().text
    out=out.replaceAll('components:\n','')
    return """ <textarea name="value"  value  class="setting-input  " type="text" rows="10" cols="25">${out}</textarea> """
}