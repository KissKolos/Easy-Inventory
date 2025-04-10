from testlib import *

def get_authorizations(token):
    test("GET",API+"/users/janos1990/authorizations/system",token,None,200,'''
    ["view_warehouses","delete_warehouses"]
    ''')
    test("GET",API+"/users/janos1990/authorizations/local/WH1",token,None,200,'''
    []
    ''')

def add_remove_system_auth(token):
    test("PUT",API+"/users/janos1990/authorizations/system/delete_types",token,None,204,None)
    test("DELETE",API+"/users/janos1990/authorizations/system/delete_types",token,None,204,None)

def add_remove_local_auth(token):
    test("PUT",API+"/users/janos1990/authorizations/local/WH1/configure",token,None,204,None)
    test("DELETE",API+"/users/janos1990/authorizations/local/WH1/configure",token,None,204,None)

def get_system_authorizations_invalid(token):
    test("GET",API+"/users/not_existing/authorizations/system",token,None,404,None)
    test("GET",API+"/users/janos1990/authorizations/system","invalid token",None,401,None)

def get_local_authorizations_invalid(token):
    test("GET",API+"/users/janos1990/authorizations/local/not_existing",token,None,404,None)
    test("GET",API+"/users/janos1990/authorizations/local/WH1","invalid token",None,401,None)

def add_remove_system_auth_invalid(token):
    test("PUT",API+"/users/not_existing/authorizations/system/delete_types",token,None,404,None)
    test("PUT",API+"/users/janos1990/authorizations/system/delete_types","invalid token",None,401,None)
    test("PUT",API+"/users/janos1990/authorizations/system/not_existing",token,None,404,None)

    test("DELETE",API+"/users/janos1990/authorizations/system/not_existing",token,None,404,None)   
    test("DELETE",API+"/users/not_existing/authorizations/system/delete_types",token,None,404,None)
    test("DELETE",API+"/users/janos1990/authorizations/system/delete_types","invalid token",None,401,None)

def add_remove_local_auth_invalid(token):
    test("PUT",API+"/users/not_existing/authorizations/local/WH1/configure",token,None,404,None)
    test("PUT",API+"/users/janos1990/authorizations/local/WH1/configure","invalid token",None,401,None)
    test("PUT",API+"/users/janos1990/authorizations/local/WH1/not_existing",token,None,404,None)

    test("DELETE",API+"/users/not_existing/authorizations/local/WH1/configure",token,None,404,None)
    test("DELETE",API+"/users/janos1990/authorizations/local/WH1/configure","invalid token",None,401,None)
    test("DELETE",API+"/users/janos1990/authorizations/local/WH1/not_existing",token,None,404,None)

def test_authorizations(token):
    get_authorizations(token)
    add_remove_system_auth(token)
    add_remove_local_auth(token)

    #get_system_authorizations_invalid(token)
    #get_local_authorizations_invalid(token)
    #add_remove_system_auth_invalid(token)
    #add_remove_local_auth_invalid(token)
    pass