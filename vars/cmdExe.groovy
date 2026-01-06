def call(){
    println "Enter cmdExe()"
    //def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    ProcessBuilder pb = new ProcessBuilder('sh','-c',"helm version");
    Map<String, String> env = pb.environment();
    String currentPath = env.get("PATH");
    println "PATH=$PATH"
     // Set a completely new PATH
    env.put("PATH", "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin");
    Process process = pb.start();
    return process.text
}