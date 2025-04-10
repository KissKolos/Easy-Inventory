from testlib import *

def test_user_info(token):
    test("GET", API+"/userinfo",token,None,200,'''{"user":"admin","username":"Admin"}''')

def test_change_password(token):
    test("PUT",API+"/userinfo",token,'''{"old_password":"admin","new_password":"123"}''',204,None)
    test("PUT",API+"/userinfo",token,'''{"old_password":"123","new_password":"admin"}''',204,None)

def test_token_renew(token):
    test("POST",API+"/token",token,None,200,'''{}''')

def test_delete_token(token):
    test("DELETE", API+"/token",token,None,204,None)
    test("GET",API+"/items",token,None,401,None)

def test_authentication(token):
    test_user_info(token)
    test_change_password(token)
    #test_token_renew(token) 
    #test_delete_token(token)
