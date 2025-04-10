from testlib import *

def add_operation1(token):
    test("PUT",API+"/warehouses/WH1/operations/TestOP0001",token,'''
    {
        "name":"Test Operation",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "amount":25
            }
        ]
    }
    ''',201,None)

def add_operation2(token):
    test("PUT",API+"/warehouses/WH1/operations/OP0003",token,'''
    {
        "name":"Test Operation 3",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "amount":25
            }
        ]
    }
    ''',201,None)

def add_operation3(token):
    test("PUT",API+"/warehouses/WH1/operations/OP0002",token,'''
    {
        "is_add":false,
        "name":"Test Operation3",
        "items":[
            {
                "type":"carrot_box",
                "lot":"L00001",
                "amount":25
            }
        ]
    }
    ''',201,None)

def add_operation4(token):
    test("PUT",API+"/warehouses/WH1/operations/OPelfelejtettem",token,'''
    {
        "name":"Test Operation elfelejtettem",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 4,
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OPelfelejtettem",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP0005",token,'''
    {
        "name":"Test Operation 5",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 4,
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP0005",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP0006",token,'''
    {
        "name":"Test Operation 6",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 6,
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP0006",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP0007",token,'''
    {
        "name":"Test Operation 7",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 6,
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP0007",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP0008",token,'''
    {
        "name":"Test Operation 8",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 8,
                "manufacturer_serial": "cb12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP0008",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP0009",token,'''
    {
        "name":"Test Operation 9",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 8,
                "manufacturer_serial": "cb12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP0009",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00010",token,'''
    {
        "name":"Test Operation 10",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "global_serial":100000,
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00010",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00011",token,'''
    {
        "name":"Test Operation 11",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "global_serial":100001,
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00011",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00012",token,'''
    {
        "name":"Test Operation 12",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 12,
                "lot": "LOT12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00012",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00013",token,'''
    {
        "name":"Test Operation 4",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 12,
                "lot": "LOT12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00013",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00015",token,'''
    {
        "name":"Test Operation 15",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "lot": "LOT12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00015",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00016",token,'''
    {
        "name":"Test Operation 16",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "lot": "LOT12",
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00016",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00017",token,'''
    {
        "name":"Test Operation 17",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "global_serial":10006,
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00017",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00018",token,'''
    {
        "name":"Test Operation 18",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "lot": "LOT12",
                "manufacturer_serial": "cb12",
                "global_serial":10007,
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00018",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00019",token,'''
    {
        "name":"Test Operation 19",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 19,
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00019",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00020",token,'''
    {
        "name":"Test Operation 20",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 19,
                "storage": "ST1",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00020",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00021",token,'''
    {
        "name":"Test Operation 21",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 21,
                "manufacturer_serial": "cb12",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00021",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00022",token,'''
    {
        "name":"Test Operation 22",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 21,
                "manufacturer_serial": "cb12",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00022",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00023",token,'''
    {
        "name":"Test Operation 23",
        "is_add":true,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 23,
                "lot": "LOT12",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00023",token,'''{"cancel":false}''',204,None)

    test("PUT",API+"/warehouses/WH1/operations/OP00024",token,'''
    {
        "name":"Test Operation 24",
        "is_add":false,
        "items":[
            {
                "type":"carrot_box",
                "global_serial": 23,
                "lot": "LOT12",
                "amount":1
            }
        ]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/OP00024",token,'''{"cancel":false}''',204,None)

def commit_operations(token):
    test("PUT",API+"/warehouses/WH1/operations/commit1",token,'''
    {
        "name":"Commit 1",
        "is_add":true,
        "items":[{"type":"carrot_box","amount":1}]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/commit1",token,'''{"cancel":false}''',204,None)
    test("GET",API+"/search?q=pa",token,None,200,'''
    [{"item":{
        "id":"carrot_box",
        "unit":{"id":"small_box","name":"Kis Doboz","deleted":null},
        "name":"R\u00e9pa","deleted":null},
        "amount":174,
        "available_amount":124,
        "global_serial":null,"manufacturer_serial":null,"lot":null,"warehouse":null,"storage":null
    }]
    ''')

    test("PUT",API+"/warehouses/WH1/operations/commit2",token,'''
    {
        "name":"Commit 2",
        "is_add":false,
        "items":[{"type":"carrot_box","amount":1}]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/commit2",token,'''{"cancel":false}''',204,None)
    test("GET",API+"/search?q=pa",token,None,200,'''
    [{"item":{
        "id":"carrot_box",
        "unit":{"id":"small_box","name":"Kis Doboz","deleted":null},
        "name":"R\u00e9pa","deleted":null},
        "amount":173,
        "available_amount":123,
        "global_serial":null,"manufacturer_serial":null,"lot":null,"warehouse":null,"storage":null
    }]
    ''')

    test("PUT",API+"/warehouses/WH1/operations/commit3",token,'''
    {
        "name":"Commit 3",
        "is_add":false,
        "items":[{"type":"carrot_box","amount":1}]
    }
    ''',201,None)
    test("DELETE",API+"/warehouses/WH1/operations/commit3",token,'''{"cancel":true}''',204,None)
    test("GET",API+"/search?q=pa",token,None,200,'''
    [{"item":{
        "id":"carrot_box",
        "unit":{"id":"small_box","name":"Kis Doboz","deleted":null},
        "name":"R\u00e9pa","deleted":null},
        "amount":173,
        "available_amount":123,
        "global_serial":null,"manufacturer_serial":null,"lot":null,"warehouse":null,"storage":null
    }]
    ''')

def get_operations(token):
    test("GET", API+"/warehouses/WH1/operations",token,None,200,'''
[{"id":"OP3","name":"OP3","is_add":false,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[]},{"id":"OP0001","name":"Test Operation","is_add":false,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[]},{"id":"TestOP0001","name":"Test Operation","is_add":false,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":3,"storage":{"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"id":"ST1","name":"Storage 1","deleted":null},"global_serial":null,"manufacturer_serial":null,"lot":""},{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":20,"storage":{"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"id":"ST1","name":"Storage 1","deleted":null},"global_serial":null,"manufacturer_serial":null,"lot":"L00001"},{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":2,"storage":{"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"id":"ST1","name":"Storage 1","deleted":null},"global_serial":null,"manufacturer_serial":null,"lot":"L00002"}]},{"id":"OP0003","name":"Test Operation 3","is_add":true,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":25,"storage":{"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"id":"ST1","name":"Storage 1","deleted":null},"global_serial":null,"manufacturer_serial":null,"lot":""}]},{"id":"OP0002","name":"Test Operation3","is_add":false,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[{"item":{"id":"carrot_box","unit":{"id":"small_box","name":"Kis Doboz","deleted":null},"name":"R\u00e9pa","deleted":null},"amount":25,"storage":{"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"id":"ST2","name":"Storage 2","deleted":null},"global_serial":null,"manufacturer_serial":null,"lot":"L00001"}]}]''')

def get_operation(token):
    test("GET",API+"/warehouses/WH1/operations/OP0001",token,None,200,'''
    {"id":"OP0001","name":"Test Operation","is_add":false,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[]}    ''')

def add_remove_operation(token):
    test("PUT",API+"/warehouses/WH1/operations/OP0004",token,'''
    {
        "is_add":false,
        "name":"alma",
        "items":[]
    }
    ''',201,None)
    test("GET",API+"/warehouses/WH1/operations/OP0004",token,None,200,'''
    {"id":"OP0004","name":"alma","is_add":false,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[]}
    ''')
    test("DELETE",API+"/warehouses/WH1/operations/OP0004",token,'''
    {
        "cancel":false
    }
    ''',204,None)

def move_operation(token):
    test("POST",API+"/warehouses/WH1/operations",token,'''
    {
        "from":"OP0001",
        "to":"OP0000"
    }
    ''',201,None)
    test("GET",API+"/warehouses/WH1/operations/OP0000",token,None,200,'''
    {"id":"OP0000","name":"Test Operation","is_add":false,"warehouse":{"id":"WH1","name":"Warehouse 1","address":"fake street","deleted":null},"created":"#int","commited":null,"items":[]}
    ''')
    test("POST",API+"/warehouses/WH1/operations",token,'''
    {
        "from":"OP0000",
        "to":"OP0001"
    }
    ''',201,None)
    



def add_operation_invalid(token):

    test("PUT", API+"/warehouses/WH1/operations/OP0001", "invalid_token", "{\"name\":\"Test Operation\",\"is_add\":false,\"items\":[{\"type\":\"carrot_box\",\"amount\":25}]}", 401, None)

    test("PUT", API+"/warehouses/WH1/operations/OP0001", token, "{}", 400, None)

    test("PUT", API+"/warehouses/WH1/operations/OP0001", token, "{\"invalid_field\":\"value\"}", 400, None)

def get_operations_invalid(token):

    test("GET", API+"/warehouses/WH1/operations", "invalid_token", None, 401, None)

def get_operation_invalid(token):

    test("GET", API+"/warehouses/WH1/operations/OP0001", "invalid_token", None, 401, None)

    test("GET", API+"/warehouses/WH1/operations/OP9999", token, None, 404, None)

def add_remove_operation_invalid(token):

    test("PUT", API+"/warehouses/WH1/operations/OP0004", "invalid_token", "{\"is_add\":false,\"name\":\"alma\",\"items\":[]}", 401, None)

    test("PUT", API+"/warehouses/WH1/operations/OP0004", token, "{}", 400, None)

    test("DELETE", API+"/warehouses/WH1/operations/OP9999", token, None, 400, None)

def move_operation_invalid(token):

    test("POST", API+"/warehouses/WH1/operations", token, "{}", 400, None)

    test("POST", API+"/warehouses/WH1/operations", token, "{\"from\":\"OP9999\",\"to\":\"OP8888\"}", 409, None)

    test("POST", API+"/warehouses/WH1/operations", token, "{\"from\":\"OP0001\",\"to\":\"OP0001\"}", 409, None)


def test_operations(token):
    add_operation1(token)
    add_operation2(token)
    add_operation3(token)
    add_operation4(token)
    get_operations(token)
    get_operation(token)
    add_remove_operation(token)
    move_operation(token)
    commit_operations(token)


    add_operation_invalid(token)
    get_operations_invalid(token)
    get_operation_invalid(token)
    add_remove_operation_invalid(token)
    move_operation_invalid(token)
    pass
