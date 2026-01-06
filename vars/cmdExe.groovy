def call(){
    println "Enter cmdExe()"
    //def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    ProcessBuilder pb = new ProcessBuilder('sh','-c',cmd);
    Map<String, String> env = pb.environment();
    String currentPath = env.get("PATH");
    println "PATH=$PATH"
     // Set a completely new PATH
    env.put("PATH", PATH);
    println "PATH=$PATH"
    Process process = pb.redirectErrorStream(true).start().text;
    return process
}