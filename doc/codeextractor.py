import sys

def countWhitespace(line):
    ws=0
    for c in line:
        if c==' ' or c=='\t':
            ws+=1
        else:
            return ws
    return ws


def printUntilClosing(file,ws,start_char,end_char):
    line = ""
    level=1
    
    while True:
        line = file.readline()
        print(line[ws:],end="");

        for c in line:
            if c==start_char:
                level+=1
            elif c==end_char:
                level-=1

            if level<=0:
                return

        if line == "":
            return

def extractCode(file,start_keyword,start_char,end_char, obj_name):
    line = ""
    
    while True:
        line = file.readline()
        if obj_name in line and start_keyword in line:
            ws=countWhitespace(line)
            print(line[ws:],end="")
            printUntilClosing(file,ws,start_char,end_char)
            return
        if line == "":
            return

def containsMemeber(line,members):
    if "private" in line:
        return False
    if len(members)==0:
        return True
    for m in members:
        if (" "+m+"(" in line) or (" "+m+";" in line):
            return True
    return False

def printClass(file,members):
    line = ""
    level=1
    
    while True:
        line = file.readline()
        olevel=level

        for c in line:
            if c=='{':
                level+=1
            elif c=='}':
                level-=1

            if level<=0:
                if len(members)>0:
                    print("    ...")
                print(line,end="")
                return

        if containsMemeber(line,members):
            if olevel==1 and level>1:
                print(line.rstrip(),"... }")
            elif olevel==1 and len(line.strip())>0 and not ("*" in line):
                print(line,end="")

        if line == "":
            return

def extractClass(file,members,classname):
    line = ""
    
    while True:
        line = file.readline()
        if (("class "+classname) in line) or (("interface "+classname) in line):
            print(line,end="")
            printClass(file,members)
            return
        if line == "":
            return


p=sys.argv[2];
if p=="":
    p=[]
else:
    p=p.split(',')

file=open(sys.argv[1],"r")
if sys.argv[1].endswith(".js"):
    extractClass(file,p,sys.argv[3])
elif sys.argv[1].endswith(".php"):
    if len(sys.argv)>3 and sys.argv[3]=="function":
        extractCode(file,'function','{','}',sys.argv[2])
    else:
        extractClass(file,p,"")
elif sys.argv[1].endswith(".sql"):
    extractCode(file,'create','#','$',sys.argv[2])
elif sys.argv[1].endswith(".java"):
    extractClass(file,p,"")

file.close()
