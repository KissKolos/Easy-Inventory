#!/bin/python

import json
import os
import shutil

WEB_FRONTEND="../frontend/"
DESKTOP_FRONTEND="../EasyInventoryDesktop/src/resources/lang/"
MOBILE_FRONTEND="../EasyInventoryMobile/app/src/main/res/"
ANDROID_VALUES={
    "en_US.json":"values",
    "hu_HU.json":"values-hu"
}

def list_files():
    files=[]
    for f in os.listdir():
        if f.endswith(".json"):
            files.append(f)
    return files


def check_keys(files):
    '''
    exits if json files don't have the same keys
    '''
    allkeys=[]
    data={}
    for f in files:
        lang=open(f, 'r', encoding="utf-8")
        keys = json.load(lang).keys()
        for k in keys:
            if not (k in allkeys):
                allkeys.append(k)
        data[f]=keys
        lang.close()
    
    failed=False
    for f in data.keys():
        for k in allkeys:
            if not (k in data[f]):
                print(f+": missing "+k)
                failed=True
    
    if failed:
        exit(1)

def convert(inp,out):
    lang=open(inp, 'r', encoding="utf-8")
    data = json.load(lang)
    lang.close()

    androidlang=open(out,"w", encoding="utf-8")
    androidlang.write("<resources>\n")

    for k in data.keys():
        v=data[k]
        androidlang.write("<string name=\""+k.replace(".","_")+"\">"+v.replace("'","\\'")+"</string>\n")

    androidlang.write("</resources>")
    androidlang.close()


files=list_files()
check_keys(files)

for f in files:
    shutil.copyfile(f,WEB_FRONTEND+f)
    shutil.copyfile(f,DESKTOP_FRONTEND+f)
    convert(f,MOBILE_FRONTEND+ANDROID_VALUES[f]+"/strings.xml")
