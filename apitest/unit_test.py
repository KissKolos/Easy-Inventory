from testlib import *

def get_units(token):

    test("GET",API+"/units",token,None,200,'''
    [
        {
            "id":"pallet",
            "name":"Raklap",
            "deleted":null
        },
        {
            "id":"small_box",
            "name":"Kis Doboz",
            "deleted":null
        }
    ]
    ''')

def get_unit(token):
    test("GET", API+"/units/pallet", token, None, 200,'''
    {
        "id":"pallet",
        "name":"Raklap",
        "deleted":null
    }
    ''')

def modify_unit(token):
    test("PUT", API+"/units/pallet?update=true",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',204,None)
    test("GET", API+"/units/pallet",token,None,200,'''
        {
            "id":"pallet",
            "name":"Raklap2",
            "deleted":null
        }
    ''')
    test("PUT", API+"/units/pallet?update=true",token,'''
    {
        "name":"Raklap",
        "deleted":null
    }
    ''',204,None)

def add_remove_unit(token):
    test("PUT", API+"/units/pallet2?create=true",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',201,None)
    test("GET", API+"/units/pallet2",token,None,200,'''
        {
            "id":"pallet2",
            "name":"Raklap2",
            "deleted":null
        }
    ''')
    test("DELETE", API+"/units/pallet2",token,None,204,None)

def move_unit(token):
    test("POST", API+"/units", token, '''
    {
        "from":"pellet",
        "to":"valami"
    }
    ''',200,None)
    test("GET", API+"/units/valami", token, None, 200,'''
    {
        "id":"valami",
        "name":"Raklap",
        "deleted":null
    }
    ''')
    test("POST", API+"/units", token, '''
    {
        "from":"valami",
        "to":"pellet"
    }
    ''',200,None)

def createOrUpdateUnit(token):
    test("PUT", API+"/units/pallet?update=true&create=true",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',204,None)
    test("GET", API+"/units/pallet",token,None,200,'''
        {
            "id":"pallet",
            "name":"Raklap2",
            "deleted":null
        }
    ''')
    test("PUT", API+"/units/pallet?update=true&create=true",token,'''
    {
        "name":"Raklap",
        "deleted":null
    }
    ''',204,None)

    test("PUT", API+"/units/pallet22?create=true&update=true",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',201,None)
    test("GET", API+"/units/pallet22",token,None,200,'''
        {
            "id":"pallet22",
            "name":"Raklap2",
            "deleted":null
        }
    ''')
    test("DELETE", API+"/units/pallet22",token,None,204,None)

def get_units_invalid(token):
    test("GET",API+"/units","invalid token",None,401,None)
    test("GET",API+"/unicc","invalid token",None,404,None)

def get_unit_invalid(token):
    test("GET", API+"/units/non_existing", token, None, 404,None)
    test("GET", API+"/units/pallet", "invalid token", None, 401,None)

def modify_unit_invalid(token):
    test("PUT", API+"/units/pallet",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',403,None)
    test("PUT", API+"/units/non_existing?update=true",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',404,None)
    test("PUT", API+"/units/pallet?update=true",token,'''
    {
        "alma":true
    }
    ''',400,None)
    test("PUT", API+"/units/pallet?update=true","invalid token",'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',401,None)

    test("DELETE", API+"/units/non_existing",token,None,404,None)
    test("DELETE", API+"/units/pallet","invalid token",None,401,None)

def add_remove_unit_invalid(token):
    test("PUT", API+"/units/pallet2",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',403,None)
    test("PUT", API+"/units/pallet?create=true",token,'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',409,None)
    test("PUT", API+"/units/pallet2?create=true","invalid token",'''
    {
        "name":"Raklap2",
        "deleted":null
    }
    ''',401,None)
    test("PUT", API+"/units/pallet2?create=true",token,'''
    {
        "alma"=true
    }
    ''',400,None)

def test_units(token):
    get_units(token)
    get_unit(token)
    modify_unit(token)
    add_remove_unit(token)
    createOrUpdateUnit(token)

    get_units_invalid(token)
    get_unit_invalid(token)
    modify_unit_invalid(token)
    add_remove_unit_invalid(token)
    pass
