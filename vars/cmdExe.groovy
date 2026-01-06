def call(String cmd){
    ProcessBuilder pb = new ProcessBuilder('sh','-c',cmd);
    pb.environment().put("PATH", PATH);
    Process process = pb.redirectErrorStream(true).start();
    return process.text
}