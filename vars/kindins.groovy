#!/usr/bin/env groovy

println "Enter kindins file"
def call() {
def cmd=''
def kind=commandExecute('command -v kind')
println "kind=$kind"
if ( kind != "" ) {
   println "install kind binary"
   cmd=" curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.30.0/kind-linux-amd64"
   commandExecute(cmd)
}

def cluster = commandExecute("printenv; kind get clusters")
if (cluster != "" ){
   cmd="kind delete cluster --name $cluster"
   commandExecute(cmd)
}

println "kind install cluster"
cmd="""cat > ingress-config.yaml <<EOF
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    extraPortMappings:
      - containerPort: 80
        hostPort: 80
        protocol: TCP
      - containerPort: 443
        hostPort: 443
        protocol: TCP
EOF
"""
  commandExecute(cmd)
  println "kind create cluster"
  cmd="kind create cluster --config ingress-config.yaml"
  println commandExecute(cmd)

  println "install ingress"
  cmd="kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml"
  println commandExecute(cmd)

  def namespace = "ingress-nginx"
  def label = "app.kubernetes.io/component=controller"
  def seconds = 0

  println "⏳ Waiting for Ingress NGINX controller container to be Ready..."
  while (true) {
      if (seconds> timeout) error("ERROR: Ingress ccontroller not started.")
      cmd="kubectl get pods -n ${namespace} -l ${label} -o jsonpath='{.items[0].status.containerStatuses[0].ready}' 2>/dev/null || true"
      def ready = commandExecute(cmd)

      if (ready == "true") {
          println "✅ Ingress NGINX controller container is Ready after ${seconds}s."
          break
      } else if (ready == "") {
          println "⚠️  Pod not created yet... (${seconds}s elapsed)"
      } else {
          println "🚧 Container not ready yet... (${seconds}s elapsed)"
      }

      seconds += 5
      sleep(5)
  }

}
