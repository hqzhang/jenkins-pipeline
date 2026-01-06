def call(String cmd){
    println "Enter cmdExe()"
    ProcessBuilder pb = new ProcessBuilder('sh','-c',cmd);
    println "PATH=$PATH"
     // Set a completely new PATH
    pb.environment().env.put("PATH", PATH);
    println "PATH=$PATH"
    Process out = pb.redirectErrorStream(true).start().text;
    return process
}