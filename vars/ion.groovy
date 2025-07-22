
def snapshot(String sourceDir, String compNames){
    def cmd="ssh -q -t ${target} 'cd ${installDir} && ./ion.sh status -so ${solutionDir} -cl ${compNames} -ss' "
    return commandExecute()
}


def download(String sourceDir, String compNames){
    def cmd="ssh -q -t ${target} 'cd ${installDir} && ./ion.sh download -so ${solutionDir} -cl ${compNames} ' "
    return commandExecute()
}

def start(String sourceDir, String compNames){
    def cmd="ssh -q -t ${target} 'cd ${installDir} && ./ion.sh start -so ${solutionDir} -cl ${compNames} -ss' "
    return commandExecute()
}

def stop(String sourceDir, String compNames){
    def cmd="ssh -q -t ${target} 'cd ${installDir} && ./ion.sh stop -so ${solutionDir} -cl ${compNames} -ss' "
   return commandExecute()
}

def status(String sourceDir, String compNames){
   def cmd="ssh -q -t ${target} 'cd ${installDir} && ./ion.sh status -so ${solutionDir} -cl ${compNames} -ss' "
   return commandExecute()
}

def deploy(String sourceDir, String compNames){
    def cmd="ssh -q -t ${target} 'cd ${installDir} && ./ion.sh deploy -so ${solutionDir} -cl ${compNames} -ss' "
    return commandExecute()
}

def configure(String sourceDir, String compNames){
    def cmd="ssh -q -t ${target} 'cd ${installDir} && ./ion.sh config -so ${solutionDir} -cl ${compNames} -fl' "
    return commandExecute()
}