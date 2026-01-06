def call(String cmd){
    def KUBECFG='/Users/hongqizhang/.kube/config'
    ProcessBuilder pb = new ProcessBuilder('sh','-c',cmd);
    pb.environment().put("KUBECONFIG", KUBECFG);
    pb.environment().put("PATH", PATH);
    Process process = pb.redirectErrorStream(true).start();
    return process.text
}