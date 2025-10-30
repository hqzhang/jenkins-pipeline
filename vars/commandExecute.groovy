def call(String cmd){
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}