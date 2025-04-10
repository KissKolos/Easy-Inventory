from testlib import *

def get_warehouses(token):
    test("GET",API+"/warehouses",token,None,200,'''
    [
        {
            "id":"WH1",
            "name":"Warehouse 1",
            "address":"fake street",
            "deleted":null
        }
    ]
    ''')

def get_warehouse(token):
    test("GET",API+"/warehouses/WH1",token,None,200,'''
    {
        "name":"Warehouse 1",
        "address":"fake street",
        "deleted":null
    }
    ''')

def modify_warehouse(token):
    test("PUT",API+"/warehouses/WH1?update=true",token,'''
    {
        "name":"Warehouse 1 (modified)",
        "address":"fake street2",
        "deleted":null
    }
    ''',204,None)
    test("GET",API+"/warehouses/WH1",token,None,200,'''
    {
        "name":"Warehouse 1 (modified)",
        "address":"fake street2",
        "deleted":null
    }
    ''')
    test("PUT",API+"/warehouses/WH1?update=true",token,'''
    {
        "name":"Warehouse 1",
        "address":"fake street",
        "deleted":null
    }
    ''',204,None)

def add_remove_warehouse(token):
    test("PUT",API+"/warehouses/WH3?create=true",token,'''
    {
        "name":"Warehouse 3",
        "address":"fake street3",
        "deleted":null
    }
    ''',201,None)
    test("GET",API+"/warehouses/WH3",token,None,200,'''
    {
        "name":"Warehouse 3",
        "address":"fake street3",
        "deleted":null
    }
    ''')
    test("DELETE",API+"/warehouses/WH3",token,None,204,None)

def move_warehouse(token):
    test("POST", API+"/warehouses",token,'''
    {
        "from":"WH1",
        "to":"WHBP"
    }
    ''',204,None)
    test("GET",API+"/warehouses/WHBP",token,None,200,'''
    {
        "name":"Warehouse 1",
        "address":"fake street",
        "deleted":null
    }
    ''')
    test("POST", API+"/warehouses",token,'''
    {
        "from":"WHBP",
        "to":"WH1"
    }
    ''',204,None)

def search_warehouse(token):
    test("GET",API+"/warehouses/WH1/search?q=pa",token,None,200,'''
    [{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":173,"available_amount":173,"global_serial":null,"manufacturer_serial":null,"lot":null,"warehouse":null,"storage":null}]
    ''')

def createOrUpdateWarehouse(token):
    test("PUT",API+"/warehouses/WH1?update=true&create=true",token,'''
    {
        "name":"Warehouse 1 (modified)",
        "address":"fake street2",
        "deleted":null
    }
    ''',204,None)
    test("GET",API+"/warehouses/WH1",token,None,200,'''
    {
        "name":"Warehouse 1 (modified)",
        "address":"fake street2",
        "deleted":null
    }
    ''')
    test("PUT",API+"/warehouses/WH1?update=true&create=true",token,'''
    {
        "name":"Warehouse 1",
        "address":"fake street",
        "deleted":null
    }
    ''',204,None)

    test("PUT",API+"/warehouses/WH31?create=true&update=true",token,'''
    {
        "name":"Warehouse 3",
        "address":"fake street3",
        "deleted":null
    }
    ''',201,None)
    test("GET",API+"/warehouses/WH31",token,None,200,'''
    {
        "name":"Warehouse 3",
        "address":"fake street3",
        "deleted":null
    }
    ''')
    test("DELETE",API+"/warehouses/WH31",token,None,204,None)



def get_warehouses_invalid(token):

    test("GET", API+"/warehouses", "invalid_token", None, 401, None)

    test("GET", API+"/warehousez", token, None, 404, None)

def get_warehouse_invalid(token):

    test("GET", API+"/warehouses/WH1", "invalid_token", None, 401, None)

    test("GET", API+"/warehouses/WHX", token, None, 404, None)

def modify_warehouse_invalid(token):

    test("PUT", API+"/warehouses/WH1?update=true", token, "{\"invalid_field\":\"value\"}", 400, None)

    test("PUT", API+"/warehouses/WH1?update=true", token, "{}", 400, None)

    test("PUT", API+"/warehouses/WH1?update=true", "invalid_token", None, 401, None)

def add_remove_warehouse_invalid(token):

    test("PUT", API+"/warehouses/WH2?create=true", token, "{\"invalid_field\":\"value\"}", 400, None)

    test("PUT", API+"/warehouses/WH2?create=true", token, "{}", 400, None)

    test("DELETE", API+"/warehouses/WHX_not_existing", token, None, 404, None)

    test("DELETE", API+"/warehouses/WH2", "invalid_token", None, 401, None)

def move_warehouse_invalid(token):

    test("POST", API+"/warehouses", token, "{}", 400, None)

    test("POST", API+"/warehouses", token, "{\"from\":\"nonexistent_warehouse\",\"to\":\"another_nonexistent_warehouse\"}", 404, None)

    test("POST", API+"/warehouses", token, "{\"from\":\"WH1\",\"to\":\"WH1\"}", 404, None)

def search_warehouse_invalid(token):

    test("GET", API+"/warehouses/WH1/search?q=unknown_item", token, None, 200, "[]")

    test("GET", API+"/warehouses/WH1/search?q=carrot", "invalid_token", None, 401, None)


def test_warehouses(token):
    get_warehouses(token)
    get_warehouse(token)
    modify_warehouse(token)
    add_remove_warehouse(token)
    move_warehouse(token)
    search_warehouse(token)
    createOrUpdateWarehouse(token)

    get_warehouses_invalid(token)
    get_warehouse_invalid(token)
    modify_warehouse_invalid(token)
    add_remove_warehouse_invalid(token)
    move_warehouse_invalid(token)
    search_warehouse_invalid(token)
    pass
