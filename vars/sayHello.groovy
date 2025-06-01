// vars/sayHello.groovy
def call(String name = 'human') {
    // Any valid steps can be called from this code, just like in other
    // Scripted Pipeline
    echo "Hello, ${name}."
}
def xxx(String name){

   echo "Just for Testing $name"
   echo "I am in libary:master"
}
return this
