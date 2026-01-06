def call(String cmd){
    ProcessBuilder pb = new ProcessBuilder('sh','-c',cmd);
    //Map<String, String> env = pb.environment();
    pb.environment().put("PATH", PATH);
    //println "PATH=$PATH"
    Process process = pb.redirectErrorStream(true).start();
    return process.text
}