from testlib import *

def get_storages(token):
    test("GET",API+"/warehouses/WH1/storages",token,None,200,'''
    [
        {
            "warehouse":
            {"id":"WH1",
                "name":"Warehouse 1",
                "address":"fake street",
                "deleted":null
            },
            "id":"ST1",
            "name":"Storage 1",
            "deleted":null
        },
        {
            "warehouse":
            {
                "id":"WH1",
                "name":"Warehouse 1",
                "address":"fake street",
                "deleted":null
            },
            "id":"ST2",
            "name":"Storage 2",
            "deleted":null
        }
    ]
    ''')

def get_storage(token):
    test("GET",API+"/warehouses/WH1/storages/ST1",token,None,200,'''
    {
        "warehouse":
        {"id":"WH1",
            "name":"Warehouse 1",
            "address":"fake street",
            "deleted":null
        },
        "id":"ST1",
        "name":"Storage 1",
        "deleted":null
    }
    ''')

def modify_storage(token):
    test("PUT", API+"/warehouses/WH1/storages/ST1?update=true",token,'''
    {
        "name":"ST1_m"
    }    
    ''',204,None)
    test("GET",API+"/warehouses/WH1/storages/ST1",token,None,200,'''
    {
        "warehouse":
        {"id":"WH1",
            "name":"Warehouse 1",
            "address":"fake street",
            "deleted":null
        },
        "id":"ST1",
        "name":"ST1_m",
        "deleted":null
    }
    ''')
    test("PUT", API+"/warehouses/WH1/storages/ST1?update=true",token,'''
    {
        "id":"ST1",
        "name":"Storage 1"
    }    
    ''',204,None)

def get_items(token):
    test("GET", API+"/warehouses/WH1/storages/ST1/items",token,None,200,'''
    [
        {
            "lot":"L00001",
            "amount":20,
            "available_amount":20,
            "type":"carrot_box",
            "global_serial":null,
            "manufacturer_serial":null
        },
        {
            "lot":"L00002",
            "amount":15,
           "available_amount":15,
          "type":"carrot_box",
            "global_serial":null,
            "manufacturer_serial":null
        }
    ]
    ''')

def add_remove_storage(token):
    test("PUT",API+"/warehouses/WH1/storages/ST3?create=true",token,'''
    {
        "name":"Storage 3",
        "deleted":null
    }
    ''',201,None)
    test("GET",API+"/warehouses/WH1/storages/ST3",token,None,200,'''
    {
        "warehouse":
        {"id":"WH1",
            "name":"Warehouse 1",
            "address":"fake street",
            "deleted":null
        },
        "id":"ST3",
        "name":"Storage 3",
        "deleted":null
    }
    ''')
    test("DELETE",API+"/warehouses/WH1/storages/ST3",token,None,204,None)

def move_storage(token):
    test("POST", API+"/warehouses/WH1/storages",token,'''
    {
        "from":"ST1",
        "to":"storage1"
    }
    ''',204,None)
    test("GET",API+"/warehouses/WH1/storages/storage1",token,None,200,'''
    {
        "warehouse":
        {"id":"WH1",
            "name":"Warehouse 1",
            "address":"fake street",
            "deleted":null
        },
        "id":"storage1",
        "name":"Storage 1",
        "deleted":null
    }
    ''')
    test("POST", API+"/warehouses/WH1/storages",token,'''
    {
        "from":"storage1",
        "to":"ST1"
    }
    ''',204,None)

def search_storage(token):
    test("GET",API+"/warehouses/WH1/storages/ST1/search?q=pa",token,None,200,'''
    [{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":38,"available_amount":38,"global_serial":null,"manufacturer_serial":null,"lot":null,"warehouse":null,"storage":null}]
    ''')

def createOrUpdateStorage(token):
    test("PUT", API+"/warehouses/WH1/storages/ST1?update=true&create=true",token,'''
    {
        "name":"ST1_m"
    }    
    ''',204,None)
    test("GET",API+"/warehouses/WH1/storages/ST1",token,None,200,'''
    {
        "warehouse":
        {"id":"WH1",
            "name":"Warehouse 1",
            "address":"fake street",
            "deleted":null
        },
        "id":"ST1",
        "name":"ST1_m",
        "deleted":null
    }
    ''')
    test("PUT", API+"/warehouses/WH1/storages/ST1?update=true&create=true",token,'''
    {
        "id":"ST1",
        "name":"Storage 1"
    }    
    ''',204,None)

    test("PUT",API+"/warehouses/WH1/storages/ST13?create=true&update=true",token,'''
    {
        "name":"Storage 3",
        "deleted":null
    }
    ''',201,None)
    test("GET",API+"/warehouses/WH1/storages/ST13",token,None,200,'''
    {
        "warehouse":
        {"id":"WH1",
            "name":"Warehouse 1",
            "address":"fake street",
            "deleted":null
        },
        "id":"ST13",
        "name":"Storage 3",
        "deleted":null
    }
    ''')
    test("DELETE",API+"/warehouses/WH1/storages/ST13",token,None,204,None)







def get_storages_invalid(token):

    test("GET", API+"/warehouses/WH1/storages", "invalid_token", None, 401, None)

    test("GET", API+"/warehouses/WH1/storagez", token, None, 404, None)

def get_storage_invalid(token):

    test("GET", API+"/warehouses/WH1/storages/ST1", "invalid_token", None, 401, None)

    test("GET", API+"/warehouses/WH1/storages/STX", token, None, 404, None)

def modify_storage_invalid(token):
    test("PUT", API+"/warehouses/WH1/storages/not_existing?update=true",token,'''
    {
        "name":"ST1_m"
    }    
    ''',404,None)

    test("PUT", API+"/warehouses/WH1/storages/ST1?update=true",token,'''
    {
        "bad body":true
    }    
    ''',400,None)

    test("PUT", API+"/warehouses/WH1/storages/ST1?update=true","invalid token",'''
    {
        "name":"ST1_m"
    }    
    ''',401,None)

def add_remove_storage_invalid(token):

    test("PUT", API+"/warehouses/WH1/storages/ST4?create=true", token, "{\"invalid_field\":\"value\"}", 400, None)

    test("GET", API+"/warehouses/WH1/storages/STX", token, None, 404, None)

    test("DELETE", API+"/warehouses/WH1/storages/ST4", "invalid_token", None, 401, None)

def move_storage_invalid(token):

    test("POST", API+"/warehouses/WH1/storages", token, "{\"from\":\"nonexistent_storage\",\"to\":\"another_nonexistent_storage\"}", 404, None)

    test("POST", API+"/warehouses/WH1/storages", token, "{\"from\":\"ST1\",\"to\":\"ST1\"}", 404, None)

def search_storage_invalid(token):

    test("GET", API+"/warehouses/WH1/storages/ST1/search?q=unknown_item", token, None, 200, "[]")

    test("GET", API+"/warehouses/WH1/storages/ST1/search?q=carrot", "invalid_token", None, 401, None)


def test_storages(token):
    get_storages(token)
    get_storage(token)
    modify_storage(token)
    add_remove_storage(token)
    move_storage(token)
    search_storage(token)
    createOrUpdateStorage(token)


    get_storages_invalid(token)
    get_storage_invalid(token)
    modify_storage_invalid(token)
    add_remove_storage_invalid(token)
    move_storage_invalid(token)
    search_storage_invalid(token)
    pass
