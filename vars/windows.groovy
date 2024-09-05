// vars/windows.groovy
def call(Closure body) {
    node('windows') {
        body()
    }
}
