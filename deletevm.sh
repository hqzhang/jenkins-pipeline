#!/bin/bash

VBoxManage controlvm "node-01" poweroff

VBoxManage unregistervm "node-01" --delete



