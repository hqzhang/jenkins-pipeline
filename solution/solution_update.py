#!/usr/bin/env python3

#import ruamel_yaml as ruamelyaml
import ruamel.yaml
import sys
import os
import shutil
from pathlib import Path
import logging as log
import re
import yaml
import shutil
from collections import OrderedDict
log.basicConfig(format='%(asctime)s:%(levelname)s:%(filename)s:%(lineno)s %(funcName) 2s(): %(message)s', level=log.NOTSET)
params={}
compKey='name'
verKey='version'
cpKey='_JVM_CLASSPATH'
cpJava='_JAVA_CLASSPATH'
dmkey='daemons_allocation'

def loadFile(fileName):
    myyaml = ruamel.yaml.YAML()
    yamlstr = Path(fileName).read_text()
    return myyaml.load(yamlstr)

def saveFile(fileName, data):
    with open(fileName, "w") as file:
        myyaml = ruamel.yaml.YAML()
        myyaml.indent(sequence=4, offset=2)
        myyaml.dump(data, file)

def parseConfig(fileName):
    print("Enter parseConfig()..",fileName)
    global params, compkey, verkey, cpkey,dmKey
    params = loadFile(fileName)
    #print(params)
   
def checkChange (key, valueFrom, valueTo):
    if str(valueFrom) == str(valueTo):
        print('set', key, 'as', valueFrom, 'NoChange')
    else:
        print('set', key, 'from', valueFrom,'to', valueTo)

def doModify(var, top, keys, k, v):
    for key in keys:
        if key==k and top in var :
            checkChange(key, var[top][key], v)
            var[top][key]=v

def updateConfiguration (input, output):
    print("Enter updateConfiguration()")
    data = loadFile(input+'/configuration.yml')

    for var in data['components']: # dynamic data
        for comp in params['components']:
            if var['name']==comp[compKey]:
                print("for:", comp[compKey], 'in configuration.yaml' )
                for k, v in comp.items():
                    keys = ['version','executable']
                    for key in keys:
                        if key==k and key in var:
                            checkChange (key, var[key], v)
                            var[key]=v
                    top = 'configurations'
                    keys = ['Path', 'LaunchScript']
                    doModify(var, top, keys, k, v)

                    top ='java settings'
                    keys =['JVM_CLASSPATH','_JVM_JAVARGS','JVM_MAINCLASS']
                    doModify(var, top, keys, k, v)
                
                    top = 'settings'
                    keys= ['_ION_PACKAGE_NAME','_JAVA_CLASSPATH','_JVM_NP_JAVAARGS']
                    doModify(var, top, keys, k, v)

                    keys=['DB.USER','DB.PASSWORD','DB.URL']
                    for key in keys:
                        print("MMM:",key,k)
                        if key==k and top in var:
                            print("MMMM",key, k, var[top][key])
                            checkChange(key, var[top][key], v)
                            var[top][key]=v

    saveFile(output+'/configuration.yml', data)

def updateEnvironment(input, output):
    print("Enter updateEnvironment()...........v1")
    data = loadFile(input+'/environment.yml')

    print("Update database sources params...v1???")
    for var in data['dbsources']:
        print("Loop:",var['NAME'])
        for comp in params['components']:
            if var['NAME']==comp[compKey]:
                print(var)
                print("For:", comp[compKey], 'in environment.yaml')
                for k, v in comp.items():
                    print("LOOP: ",k,v)
                    keys=['DB_PWD','DB_TYPE', 'DB_URL','DB_USER','DB_CHANGELOG']
                    for key in keys:
                        if key==k:
                            if key in var:
                                checkChange(key, var[key], v)
                            else:  
                                print('New', key, 'as',v)
                            var[key]=v
                   
                        
    print("Update daemon allocation paramsVVVVV")
    for var in data['daemons_allocation'].items(): #solution
        for comp in params['components']:
            if var[0] ==comp[compKey]:
                print("For", comp[compKey], 'in environment.yaml' )
                for k, v in comp.items():
                    if k == dmkey and len(v.strip()) != 0:
                        checkChange(var[0], data['daemons_allocation'][var[0]],v)
                        #print("set", var[0], "value to",v)
                        data['daemons_allocation'][var[0]]=v

    print("Check and set daemon availableVVV")
    for var in data['daemons_allocation' ].items():
        for comp in params['components']:
            if var[0] ==comp[compKey]:
                print("For: ",comp[compKey], "in environment.yaml")
                flag = True
                lis = var[1].split()
                for item in lis:
                    if '~' not in item:
                        flag=False
                if flag:
                    print('Set daemon available')
                    checkChange(var[0], data['daemons_allocation'] [var[0]], var[1][1:] )
                    data['daemons_allocation'][var[0]]=var[1][1:]
    saveFile(output+ '/environment.yml',data)


def getNameTypes (input):
    print("Enter getNameTypes()")
    cfg=loadFile(input+'/configuration.yml')
    #print('cfg=',cfg)
    names = {}
    types = {}
    for cmp in cfg['components']:
        print (cmp['name'])
    for comp in params['components']:
        if 'type' not in comp:
            for cmp in cfg['components']:
                if comp['name'] == cmp['name']:
                    comp['type'] = cmp['type']
        if 'version' not in comp:  
            comp['version']=comp['Path'].split('/')[-1]
        for k, v in comp.items():
            if k=='name': names[comp[k]]=comp['type']
            
            if k=='type':
                if comp[k] not in types:
                    types[comp[k]]=str(comp['version'])
                elif str(comp['version']) not in types[comp[k]]:
                    type[comp[k]] += ' '+str(comp['version'])
    print("types=",types)
    return types

def updateSolution( input, output):
    print("Enter updateSolution()")
    data=loadFile(input+'/solution.yml')
    keys=getNameTypes(input)

    for var in data['components']:
        print("For:", var['type'], 'in solution.yaml')
        if var['type']=='daemon':
            data['components'].remove(var)
        for typ, vers in keys.items():
            if var['type']==typ:
                checkChange ('versions', var['versions'],vers)
                var['versions']=vers
    print('data=',data)
    saveFile(output+ '/solution.yml',data)

def updateMachine( input, output):
    print("Enter updateMachine()")
    #.yaml'
    data=loadFile(input+'/machines.yml')
   
    for var in data['machines']:
        if 'address' in var:
            print(var['daemon_name'],var['address'])
        else:

            print(var['daemon_name']+'removed because of no address')

    saveFile(output+ '/machines.yml',data)

def getAllComponents (fulldir):
    print("Enter getAllComponents()")
    data= loadFile(fulldir+'/configuration.yml')
    dict={}
    cnt=0
    for var in data['components']:
        cnt=cnt+1
        if 'type' in var:
            if var['type'] not in dict:
                dict[var['type'] ]=[var['name']]
                #print(var['type'], var['name'])
            else:
                dict[var['type']].append(var['name'])
                #print(var['type'], var['name'])
    for k,v in dict.items():
        print(k, v)

    print("cnt", cnt)
    return dict

def getAllMachines (fulldir):
    print("Enter getAllMachines()")
    data = loadfile(fulldir+'/machines.yml')
    dict={}
    cnt=0
    for var in data['machines']:
        print (var["daemon_name"], var['address'] )
        dict[var['daemon_name']]= [var['address']]
        cnt=cnt+1
    print("cnt=", cnt)
    return dict

if __name__== "__main__":
    log.info("enter solution update main()...")
    
    config='config.yaml'
    input='examples'
    output='output'
    fulldir='examples'
    if len(sys.argv) == 5:
        input=sys.argv[1]
        output=sys.argv[2]
        config=sys.argv[3]
        fulldir=sys.argv[4]
    else:  
        print("Please follow usage: solution_update.py inputdir outputdir configfile fulldir")
   
    if os.path.exists(output):
        shutil.rmtree(output)
    
    if not os.path.exists(input):
        print('Not exists for InportDir:',input)
        sys.exit()
    try:
        file = open(config,'r')
    except IOError:
        print('Can not open config file:',config)
        sys.exit()
    os.makedirs(output)
    parseConfig(config)

    print("Update all solution Files")
    
    #updateSolution (input, output)
    #updateConfiguration(input, output)
    updateEnvironment (input, output)
    #updateMachine(fulldir, output)
    print("end solution update.")
