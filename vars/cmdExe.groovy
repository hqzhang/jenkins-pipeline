def call(cmd){
    println "Enter cmdExe()"
    ProcessBuilder pb = new ProcessBuilder('sh','-c',cmd);
    pb.environment().env.put("PATH", PATH);
    Process out = pb.redirectErrorStream(true).start().text;
    return process
}