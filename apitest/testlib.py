import urllib
import urllib.request
import json
import inspect

#API="http://[::1]:8001/api"
API="http://127.0.0.1:8001/api"

ANSI_OK='\033[92m'
ANSI_ERR='\033[91m'
ANSI_WARN='\033[93m'
ANSI_CLEAR='\033[0m'

def fail(msg):
    print(ANSI_ERR+"TEST FAILED: "+ANSI_CLEAR+msg)

def success(msg):
    print(ANSI_OK+"TEST SUCCESS: "+ANSI_CLEAR+msg)

test_count=0

def check_json(data,expected):
    if (type(data) is list) and (type(expected) is list):
        if len(data)!=len(expected):
            return False
        for i in range(0,len(data)):
            if not check_json(data[i],expected[i]):
                return False
        return True
    elif (type(data) is dict) and (type(expected) is dict):
        if len(data)!=len(expected):
            return False
        for k in data:
            if not check_json(data[k],expected[k]):
                return False
        return True
    elif (type(data) is int) and expected=="#int":
        return True
    else:
        return data==expected

def test(method,url,token,body,expected_code,expected_body):
    bad_test=(expected_code==200 and expected_body==None) or ((expected_code<200 or expected_code>=300) and expected_body!=None)
    if bad_test:
        print(ANSI_WARN+"[BAD TEST] "+ANSI_CLEAR,end='')

    global test_count
    data=None
    if body!=None:
        data=bytes(body, 'utf-8')
    
    req = urllib.request.Request(url,data=data, method=method)
    if token!=None:
        req.add_header('Authorization', "Bearer "+token)
    
    rawbody=None
    try:
        code=0
        try:
            response = urllib.request.urlopen(req)
            code=response.getcode()
            rawbody=response.read().decode()
        except urllib.error.HTTPError as e:
            code=e.code

        if code!=expected_code:
            fail("expected {} got {}".format(expected_code,code))
            print(method+" "+url)
            print(rawbody)
            caller=inspect.stack()[1]
            print("{} {}:{}".format(caller.filename,caller.function,caller.lineno))
            exit()
        
        if expected_body!=None:
            body=json.loads(rawbody)
            ebody=json.loads(expected_body)
            if not check_json(body,ebody):
                fail("wrong body")
                print("{} {}".format(method,url))
                print("expected:")
                print(expected_body)
                print("got:")
                print(rawbody)
                caller=inspect.stack()[1]
                print("{} {}:{}".format(caller.filename,caller.function,caller.lineno))
                exit()

        success(url)

        if bad_test:
            print(rawbody)
        test_count+=1
    except Exception as e:
        print(e)
        fail("{} {} unexpected exception".format(method,url))
        print("body: "+rawbody)
        exit()

def test_login(url,body):
    req = urllib.request.Request(url,data=bytes(body, 'utf-8'), method="POST")
    try:
        response = urllib.request.urlopen(req)
        if response.getcode()!=200:
            fail("expected 200 got {}".format(response.getcode()))
            exit()
        
        body=json.loads(response.read().decode())

        success(url)

        return body["token"]
    except Exception as e:
        print(e)
        fail("unexpected exception")
        exit()

def print_stat():
    print("ran "+str(test_count)+" tests")
