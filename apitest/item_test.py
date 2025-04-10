from testlib import *

def get_items(token):
    test("GET",API+"/items",token,None,200,'''
    [
        {
            "id":"carrot_box",
            "name":"Répa",
            "unit":{
                "id":"small_box","name":"Kis Doboz","deleted":null
            },
            "deleted":null
        }
    ]
    ''')

def get_item(token):
    test("GET",API+"/items/carrot_box",token,None,200,'''
    {
        "id":"carrot_box",
        "name":"Répa",
        "unit":
        {
            "id":"small_box",
            "name":"Kis Doboz",
            "deleted":null
        },
        "deleted":null
    }
    ''')

def modify_item(token):
    test("PUT",API+"/items/carrot_box?update=true",token,'''
    {
        "name":"Krumpli",
        "unit":"small_box",
        "deleted":null
    }
    ''',204,None)
    test("GET",API+"/items/carrot_box",token,None,200,'''
    {
        "id":"carrot_box",
        "name":"Krumpli",
        "unit":
        {
            "id":"small_box",
            "name":"Kis Doboz",
            "deleted":null
        },
        "deleted":null
    }
    ''')
    test("PUT",API+"/items/carrot_box?update=true",token,'''
    {
        "name":"Répa",
        "unit":"small_box",
        "deleted":null
    }
    ''',204,None)

def add_remove_item(token):
    test("PUT",API+"/items/potato_box2?create=true",token,'''
    {
        "name":"Krumpli",
        "unit":"small_box",
        "deleted":null
    }
    ''',201,None)
    test("GET",API+"/items/potato_box2",token,None,200,'''
    {
        "id":"potato_box2",
        "name":"Krumpli",
        "unit":
        {
            "id":"small_box",
            "name":"Kis Doboz",
            "deleted":null
        },
        "deleted":null
    }
    ''')
    test("DELETE", API+"/items/potato_box2",token,None,204,None)

def move_item(token):
    test("POST",API+"/items",token,'''
    {
        "from":"carrot_box",
        "to":"repa_box"
    }
    ''',204,None)
    test("GET",API+"/items/repa_box",token,None,200,'''
    {
        "id":"repa_box",
        "name":"Répa",
        "unit":
        {
            "id":"small_box",
            "name":"Kis Doboz",
            "deleted":null
        },
        "deleted":null
    }
    ''')
    test("POST",API+"/items",token,'''
    {
        "from":"repa_box",
        "to":"carrot_box"
    }
    ''',204,None)

def search(token):
    test("GET",API+"/search?q=pa",token,None,200,'''
    [{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":173,"available_amount":173,"global_serial":null,"manufacturer_serial":null,"lot":null,"warehouse":null,"storage":null}]
    ''')

def createOrUpdateItem(token):
    test("PUT",API+"/items/carrot_box?update=true&create=true",token,'''
    {
        "name":"Krumpli",
        "unit":"small_box",
        "deleted":null
    }
    ''',204,None)
    test("GET",API+"/items/carrot_box",token,None,200,'''
    {
        "id":"carrot_box",
        "name":"Krumpli",
        "unit":
        {
            "id":"small_box",
            "name":"Kis Doboz",
            "deleted":null
        },
        "deleted":null
    }
    ''')
    test("PUT",API+"/items/carrot_box?update=true&create=true",token,'''
    {
        "name":"Répa",
        "unit":"small_box",
        "deleted":null
    }
    ''',204,None)

    test("PUT",API+"/items/potato_box12?create=true&update=true",token,'''
    {
        "name":"Krumpli",
        "unit":"small_box",
        "deleted":null
    }
    ''',201,None)
    test("GET",API+"/items/potato_box12",token,None,200,'''
    {
        "id":"potato_box12",
        "name":"Krumpli",
        "unit":
        {
            "id":"small_box",
            "name":"Kis Doboz",
            "deleted":null
        },
        "deleted":null
    }
    ''')
    test("DELETE", API+"/items/potato_box12",token,None,204,None)





def get_items_invalid(token):

    test("GET", API+"/items", "invalid_token", None, 401, None)
    test("GET", API+"/itemz", token, None, 404, None)

def get_item_invalid(token):

    test("GET", API+"/items/carrot_box", "invalid_token", None, 401, None)

    test("GET", API+"/items/nonexistent_item", token, None, 404, None)

def modify_item_invalid(token):
    test("PUT",API+"/items/not_existing_item?update=true",token,'''
    {
        "name":"Krumpli",
        "unit":"small_box",
        "deleted":null
    }
    ''',404,None)
    test("PUT",API+"/items/carrot_box?update=true",token,'''
    {
        "bad body":True
    }
    ''',400,None)
    test("PUT",API+"/items/carrot_box?update=true","invalid token",'''
    {
        "name":"Krumpli",
        "unit":"small_box",
        "deleted":null
    }
    ''',401,None)

def add_remove_item_invalid(token):

    test("PUT", API+"/items/potato_box?create=true", token, "{\"invalid_field\":\"value\"}", 400, None)

    test("PUT", API+"/items/potato_box?create=true", token, "{}", 400, None)

    test("GET", API+"/items/nonexistent_item", token, None, 404, None)

    test("DELETE", API+"/items/potato_box", "invalid_token", None, 401, None)

def move_item_invalid(token):

    test("POST", API+"/items", token, "{}", 400, None)

    test("POST", API+"/items", token, "{\"from\":\"nonexistent_box\",\"to\":\"another_nonexistent_box\"}", 404, None)

    test("POST", API+"/items", token, "{\"from\":\"carrot_box\",\"to\":\"carrot_box\"}", 404, None)

def search_invalid(token):

    test("GET", API+"/search?q=unknown_item", token, None, 200, "[]")

    test("GET", API+"/search?q=carrot", "invalid_token", None, 401, None)


def test_items(token):
    get_items(token)
    get_item(token)
    modify_item(token)
    add_remove_item(token)
    move_item(token)
    search(token)
    createOrUpdateItem(token)

    get_items_invalid(token)
    get_item_invalid(token)
    modify_item_invalid(token)
    add_remove_item_invalid(token)
    move_item_invalid(token)
    search_invalid(token)
    pass
