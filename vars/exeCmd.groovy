// vars/buildPlugin.groovy
def call(String cmd, String directory){
    ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
    pb.directory(new File(directory))
    pb.redirectErrorStream(true);
    def proc = pb.start()
    proc.waitFor()
    def err=proc.exitValue()
    def reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))
    def out='',line = null
    while ((line = reader.readLine()) != null) { out += line+ "\n" }
    if ( err != 0){
        println("ERROR: $out")
        error("Debug for cmd:$cmd")
    }
    return out
}


