from testlib import *
def get_users(token):
    test("GET",API+"/users",token,None,200,'''
    [
        {
            "id":"admin",
            "name":"Admin",
            "manager":null
        },
        {
            "id":"janos1990",
            "name":"János",
            "manager":
            {
                "id":"admin",
                "name":"Admin",
                "manager":null            
            }
        }
    ]
    ''')

def get_user(token):
    test("GET",API+"/users/janos1990",token,None,200,'''
    {
        "id":"janos1990",
        "name":"János",
        "manager":
        {
            "id":"admin",
            "name":"Admin",
            "manager":null            
        }
    }
    ''')

def modify_user(token):
    test("PUT",API+"/users/janos1990?update=true",token,'''
    {
        "name":"Test User"
    }
    ''',204,None)
    test("GET",API+"/users/janos1990",token,None,200,'''
    {
        "id":"janos1990",
        "name":"Test User",
        "manager":{
            "id":"admin",
            "name":"Admin",
            "manager":null
            }
    }
    ''')
    test("PUT",API+"/users/janos1990?update=true",token,'''
    {
        "name":"János"
    }
    ''',204,None)

def add_remove_user(token):
    test("PUT",API+"/users/testuser?create=true",token,'''
    {
        "name":"Test User",
        "password":"admin"
    }
    ''',201,None)
    test("GET",API+"/users/testuser",token,None,200,'''
    {
        "id":"testuser",
        "name":"Test User",
        "manager":
        {
            "id":"admin",
            "name":"Admin",
            "manager":null            
        }
    }
    ''')
    test("DELETE",API+"/users/testuser",token,None,204,None)

def move_user(token):
    test("POST",API+"/users",token,'''
    {
        "from":"janos1990",
        "to":"jani1990"
    }
    ''',204,None)
    test("GET",API+"/users/jani1990",token,None,200,'''
    {"id":"jani1990","name":"János","manager":{"id":"admin","name":"Admin","manager":null}}
    ''')
    test("POST",API+"/users",token,'''
    {
        "from":"jani1990",
        "to":"janos1990"
    }
    ''',204,None)

def createOrUpdateUser(token):
    test("PUT",API+"/users/janos1990?update=true&create=true",token,'''
    {
        "name":"Test User"
    }
    ''',204,None)
    test("GET",API+"/users/janos1990",token,None,200,'''
    {
        "id":"janos1990",
        "name":"Test User",
        "manager":{
            "id":"admin",
            "name":"Admin",
            "manager":null
            }
    }
    ''')
    test("PUT",API+"/users/janos1990?update=true&create=true",token,'''
    {
        "name":"János"
    }
    ''',204,None)   

    test("PUT",API+"/users/testuser2?create=true&update=true",token,'''
    {
        "name":"Test User",
        "password":"admin"
    }
    ''',201,None)
    test("GET",API+"/users/testuser2",token,None,200,'''
    {
        "id":"testuser2",
        "name":"Test User",
        "manager":
        {
            "id":"admin",
            "name":"Admin",
            "manager":null            
        }
    }
    ''')
    test("DELETE",API+"/users/testuser2",token,None,204,None)




def get_users_invalid(token):

    test("GET", API+"/users", "invalid_token", None, 401, None)

def get_user_invalid(token):

    test("GET", API+"/users/janos1990", "invalid_token", None, 401, None)

    test("GET", API+"/users/nonexistent_user", token, None, 404, None)

def modify_user_invalid(token):
    test("PUT",API+"/users/not_existing?update=true",token,'''
    {
        "name":"Test User"
    }
    ''',404,None)

    test("PUT",API+"/users/janos1990",token,'''
    {
        "bad body":True
    }
    ''',400,None)

    test("PUT",API+"/users/janos1990","invalid token",'''
    {
        "name":"Test User"
    }
    ''',401,None)

def add_remove_user_invalid(token):
    test("PUT",API+"/users/testuser",token,'''
    {
        "alma":"banán"
    }
    ''',400,None)
    test("PUT",API+"/users/testuser","invalid token",'''
    {
        "name":"Test User",
        "password":"admin"
    }
    ''',401,None)

    test("DELETE",API+"/users/not_existing",token,None,404,None)

    test("DELETE",API+"/users/testuser","invalid token",None,401,None)

def move_user_invalid(token):

    test("POST", API+"/users", token, "{}", 400, None)

    test("POST", API+"/users", token, "{\"from\":\"nonexistent_user\",\"to\":\"another_nonexistent_user\"}", 404, None)

    test("POST", API+"/users", token, "{\"from\":\"janos1990\",\"to\":\"janos1990\"}", 404, None)


def test_users(token):
    get_users(token)
    get_user(token)
    modify_user(token)
    add_remove_user(token)
    move_user(token)
    createOrUpdateUser(token)

    get_users_invalid(token)
    get_user_invalid(token)
    modify_user_invalid(token)
    add_remove_user_invalid(token)
    move_user_invalid(token)
    pass
