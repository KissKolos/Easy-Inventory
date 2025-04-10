from testlib import *

def put_limit(token):
    test("PUT",API+"/warehouses/WH1/storages/ST1/limits/carrot_box",token,'''
    {"amount":15}
    ''',204,None)

def get_limits(token):
    test("GET",API+"/warehouses/WH1/storages/ST1/limits",token,None,200,'''
    [
        {
            "item":{
                "id":"carrot_box",
                "unit":{"id":"small_box","name":"Kis Doboz","deleted":null},
                "name":"Répa",
                "deleted":null
            },
            "amount":15
        }
    ]
    ''')

def get_capacity(token):
    test("GET", API+"/warehouses/WH1/storages/ST1/capacity",token,None,200,'''
    [
        {
            "item":{
                "id":"carrot_box",
                "unit":{"id":"small_box","name":"Kis Doboz","deleted":null},
                "name":"Répa",
                "deleted":null
            },
            "stored_amount":38,
            "limit":15
        }
    ]
    ''')

def get_capacity2(token):
    test("GET", API+"/warehouses/WH1/storages/ST2/capacity",token,None,200,'''
    [
        {
            "item":{
                "id":"carrot_box",
                "unit":{
                    "id":"small_box",
                    "name":"Kis Doboz",
                    "deleted":null
                },
                "name":"Répa",
                "deleted":null
            },
            "stored_amount":135,
            "limit":4
        }
    ]
    ''')





def put_limit_invalid(token):

    test("PUT", API+"/warehouses/WH1/storages/ST1/limits/carrot_box", "invalid_token", "{\"amount\":10}", 401, None)

    test("PUT", API+"/warehouses/WH1/storages/ST1/limits/carrot_box", token, "{\"amount\":\"invalid\"}", 400, None)

    test("PUT", API+"/warehouses/WH1/storages/ST1/limits/carrot_box", token, "{}", 400, None)

def get_limits_invalid(token):

    test("GET", API+"/warehouses/WH1/storages/ST1/limits", "invalid_token", None, 401, None)

    test("GET", API+"/warehouses/WH1/storages/invalid_storage/limits", token, None, 404, None)

def get_capacity_invalid(token):

    test("GET", API+"/warehouses/WH1/storages/ST1/capacity", "invalid_token", None, 401, None)

    test("GET", API+"/warehouses/WH1/storages/invalid_storage/capacity", token, None, 404, None)


def test_storagelimits(token):
    put_limit(token)
    get_limits(token)
    get_capacity(token)
    get_capacity2(token)


    put_limit_invalid(token)
    get_limits_invalid(token)
    get_capacity_invalid(token)
    get_capacity_invalid(token)
    pass