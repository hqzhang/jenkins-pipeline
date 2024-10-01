import org.yaml.snakeyaml.Yaml

def updateRoute(Map kind, Map config){
    println("Enter updateRoute() ")

    kind.metadata.namespace=config.namespace
    kind.metadata.name=config.Route
    kind.metadata.labels.app=config.app
    kind.metadata.labels.name=config.label
    kind.spec.to.name=config.Service
  
    println ('meta.ns='+kind.metadata.namespace)
    println ('meta.name='+kind.metadata.name)
    println ('meta.labels='+kind.metadata.labels)
    println ('spec.host='+kind.spec.host)
    println ('spec.port='+kind.spec.port)
    println "=====ssss======"
    println ('spec.to='+kind.spec.to)
}
def updateService(Map kind, Map config){
    println("Enter updateService() ")
    kind.metadata.namespace=config.namespace
    kind.metadata.name=config.Service
    kind.metadata.labels.app=config.app
    kind.metadata.labels.name=config.label
    kind.spec.selector.app=config.app
    kind.spec.selector.deploymentconfig=config.DeploymentConfig
    kind.spec.selector.name=config.label
    println ('meta.ns='+kind.metadata.namespace)
    println ('meta.name='+kind.metadata.name)
    println ('meta.labels='+kind.metadata.labels)
    println ('spec.clustIP='+kind.spec.clusterIP)
    println ('spec.ports='+kind.spec.ports)
    println "=====ssss======"
    println ('spec.select='+kind.spec.selector)
}

def updateDeploy(Map kind, Map config){
    println("Enter updateDeploy() ")
    
    kind.metadata.namespace=config.namespace
    kind.metadata.name=config.DeploymentConfig
    kind.metadata.labels.app=config.app
    kind.metadata.labels.name=config.label
    kind.spec.selector.app=config.app
    kind.spec.selector.depoymentconfig=config.DeploymentConfig
    kind.spec.selector.label=config.label
    println ('meta.ns='+kind.metadata.namespace)
    println ('meta.name='+kind.metadata.name)
    println ('meta.labels='+kind.metadata.labels)
    println ('spec.select='+kind.spec.selector)
    println ('spec.template.spec.meta='+kind.spec.template.metadata.labels)
    println ('spec.template.spec.conts.name='+kind.spec.template.spec.containers[0].name)
    println ('spec.template.spec.conts.image='+kind.spec.template.spec.containers[0].image)
    println ('spec.template.spec.conts.ports='+kind.spec.template.spec.containers[0].ports)
    println "=====ssss======"
    println ('spec.triggers.from='+kind.spec.triggers[1].imageChangeParams.from)
}
def updateImage(Map kind, Map config){
    println("Enter updateImage() ")
    
    kind.metadata.namespace=config.namespace
    kind.metadata.name=config.ImageStream
    kind.metadata.labels.app=config.app
    kind.metadata.labels.name=config.label
    
    println ('spec.tags='+kind.spec.tags.from)
    println ('meta.ns='+kind.metadata.namespace)
    println ('meta.name='+kind.metadata.name)
    println ('meta.labels='+kind.metadata.labels)
    println ('spec.tags='+kind.spec.tags.from)
}

def updateDataModel(String fileName, String configName){
    println("Enter parseConfig() ")
    String fileConts = new File(fileName).text
    String configConts = new File(configName).text
    def config = new Yaml().load(configConts).components
    println("config="+config)
    def map = new Yaml().load(fileConts)
    map.items.each { kind ->
       println ("In data model:----------"+kind.kind)
       switch(kind.kind) {
       case 'Route': 
          updateRoute(kind, config)
          break
       case 'Service':
          updateService(kind, config)
          break
       case 'ImageStream':
          updateImage(kind, config)
          break
       case 'DeploymentConfig':
          updateDeploy(kind, config)
          break
      }
    }

}

//println parseConfig('projectkube.yaml.std')
//updateDataModel('mydatamodel.yaml','config.yaml')

