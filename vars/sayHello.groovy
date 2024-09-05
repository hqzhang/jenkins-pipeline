// vars/sayHello.groovy
def call(String name = 'human') {
    // Any valid steps can be called from this code, just like in other
    // Scripted Pipeline
    echo "Hello, ${name}."
}
def xxx(String name){
   sh "pwd"
   echo "Just for Testing $name"
   sh "pwd"
   echo "I am in libary:master"
}
return this
