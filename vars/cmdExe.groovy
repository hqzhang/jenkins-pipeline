def call(String cmd){
    println "Enter cmdExe()"
    ProcessBuilder pb = new ProcessBuilder('sh','-c',cmd);
    Map<String, String> env = pb.environment();
    println "PATH=$PATH"
     // Set a completely new PATH
    env.put("PATH", PATH);
    println "PATH=$PATH"
    Process out = pb.redirectErrorStream(true).start().text;
    return process
}