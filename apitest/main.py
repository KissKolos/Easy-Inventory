#
##!/bin/python3

from testlib import *
from unit_test import *
from item_test import *
from warehouse_test import *
from storage_test import *
from user_test import *
from operation_test import *
from storagelimit_test import *
from authorization_test import *
from statistics_test import *
from authentication_test import *

TEST_TOKEN="tester"

def get_content(f):
    file=open(f,"r")
    c=file.read()
    file.close()
    return c

test("PUT",API+"/db",TEST_TOKEN,get_content("testdata.json"),204,None)

ADMIN=test_login(API+"/users/admin/auth",'{"password":"admin"}')


test_units(ADMIN)
test_items(ADMIN)
test_storages(ADMIN)
test_warehouses(ADMIN)
test_users(ADMIN)
test_operations(ADMIN)
test_storagelimits(ADMIN)
test_authorizations(ADMIN)
test_statistics(ADMIN)
test_authentication(ADMIN)

print_stat()
