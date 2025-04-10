from testlib import *

def item_statistics(token):
    test("GET", API+"/statistics/items?item=carrot_box",token,None,200,'''
    {"start_amount":[166,166],"remove_amount":[0,0],"add_amount":[0,0]}
    ''')

def operation_statistics(token):
    test("GET", API+"/statistics/operations?item=carrot_box",token,None,200,'''
    {"start_amount":[166,166],"remove_amount":[],"add_amount":[]}
    ''')

def test_statistics(token):
    item_statistics(token)
    #operation_statistics(token)
    pass    
