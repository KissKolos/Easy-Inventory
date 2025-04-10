import re
from playwright.sync_api import Page, expect
from testlib import *
from datetime import datetime

test_user = "admin"

def test_load_database_small(page: Page):
    file=open("testdata_small.json","r")
    c=file.read()
    file.close()
    apitest("PUT",API+"/db","tester",c,204,None)

def test_login(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")

    expect(page.locator("css=.logged-in-page")).to_be_hidden()
    expect(page.locator("css=.login-page")).to_be_visible()

    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    expect(page.locator("css=.logged-in-page")).to_be_visible()
    expect(page.locator("css=.login-page")).to_be_hidden()

def test_logout(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.logout-button").click()
    expect(page.locator("css=.logged-in-page")).to_be_hidden()
    expect(page.locator("css=.login-page")).to_be_visible()

def test_items_list(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Items").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("carrot_box")

def test_item_modify(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Items").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("potato_box_teszt")
    popup.locator("css=.name").fill("Krumpli")
    popup.locator("css=.unit").click()

    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    expect(popup2).to_contain_text("pallet")
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box_teszt")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Krumpli")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Raklap")

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("carrot_box")
    popup.locator("css=.name").fill("Répa")
    popup.locator("css=.unit").click()

    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    expect(popup2).to_contain_text("small_box")
    popup2.locator("css=.list-item-content").nth(1).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()    

def test_item_create_remove(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Items").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("potato_box_teszt" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Krumpli")
    popup.locator("css=.unit").click()

    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    expect(popup2).to_contain_text("pallet")
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box_teszt")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Krumpli")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Raklap")

    page.locator("css=.list-item").filter(has_text="potato_box_teszt").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("potato_box_teszt")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Krumpli")

def test_item_archived(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Items").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("potato_box")
    page.locator("css=.list-archived").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box")

def test_units_list(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Units").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("pallet")

def test_unit_modify(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Units").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("test_box")
    popup.locator("css=.name").fill("Teszt")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("test_box")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Teszt")

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("pallet")
    popup.locator("css=.name").fill("Raklap")
    popup.locator("css=.ok").click()

def test_unit_create_remove(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Units").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("potato_box" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Krumpli")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Krumpli")

    page.locator("css=.list-item").filter(has_text="potato_box").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("potato_box")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Krumpli")

def test_unit_archived(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Units").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("potato_box_archived" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Krumpli")
    popup.locator("css=.ok").click()
    page.locator("css=.list-item").filter(has_text="potato_box").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("potato_box_archived")
    page.locator("css=.list-archived").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box_archived")

def test_users_list(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("janos1990")

def test_user_modify(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("janika10")
    popup.locator("css=.name").fill("Janika")
    popup.locator("css=.manager").click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("janika10")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("janika")

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()

    expect(popup).to_contain_text("Admin")    

    popup.locator("css=.id").fill("janos1990")
    popup.locator("css=.name").fill("János")
    popup.locator("css=.ok").click()

def test_user_create_remove_without_manager(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("test_user")
    popup.locator("css=.name").fill("Teszter Péter")
    popup.locator("css=.password").fill("Alma0!")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("test_user")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Teszter Péter")

    page.locator("css=.list-item").filter(has_text="test_user").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("test_user")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Teszter Péter")

def test_user_create_remove_with_manager(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("test_user")
    popup.locator("css=.name").fill("Teszter Péter")
    popup.locator("css=.password").fill("Alma0!")
    popup.locator("css=.manager").click()
    popup2 = page.locator("css=.popup-body").nth(1)
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("test_user")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Teszter Péter")

    page.locator("css=.list-item").filter(has_text="test_user").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("test_user")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Teszter Péter")

def test_user_system_authorizations(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.authorizations").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    expect(popup.locator("css=.authorization-grant").nth(2)).to_be_visible()
    popup.locator("css=.authorization-grant").nth(2).click()
    expect(popup.locator("css=.authorization-revoke").nth(2)).to_be_visible()
    popup.locator("css=.cancel").click()

    page.locator("css=.reload-btn").click
    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.authorizations").click()

    expect(popup.locator("css=.authorization-revoke").nth(2)).to_be_visible()
    popup.locator("css=.authorization-revoke").nth(2).click()
    expect(popup.locator("css=.authorization-revoke").nth(2)).to_be_hidden()
    popup.locator("css=.cancel").click()

def test_user_local_authorizations(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.local-authorizations").click()

    popup0 = page.locator("css=.popup-body").locator("visible=true")
    popup0.locator("css=.list-item-content").click()
    popup0.locator("css=.ok").click()

    popup = page.locator("css=.popup-body").locator("visible=true")
    expect(popup).to_be_visible()
    
    popup.locator("css=.authorization-grant").nth(1).click()
    expect(popup.locator("css=.authorization-revoke").nth(1)).to_be_visible()
    popup.locator("css=.cancel").click()

    page.locator("css=.reload-btn").click
    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.local-authorizations").click()

    popup0 = page.locator("css=.popup-body").locator("visible=true")
    popup0.locator("css=.list-item-content").click()
    popup0.locator("css=.ok").click()

    expect(popup.locator("css=.authorization-revoke").nth(1)).to_be_visible()
    popup.locator("css=.authorization-revoke").nth(1).click()
    expect(popup.locator("css=.authorization-revoke").nth(1)).to_be_hidden()
    popup.locator("css=.cancel").click()

def test_search(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()

    page.locator("css=.search-btn").click()
    expect(page.locator("css=table")).to_contain_text("Répa")

    page.locator("css=.search-storage").click()
    page.locator("css=.search-btn").click()
    expect(page.locator("css=table")).to_contain_text("Storage 1")
    expect(page.locator("css=table")).to_contain_text("Storage 2")

def test_wh_list(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Warehouses").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1")

def test_wh_modify(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Warehouses").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("WH1_t")
    popup.locator("css=.name").fill("Warehouse 1 (modified)_t")
    popup.locator("css=.address").fill("fake street2_t")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Warehouse 1 (modified)_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("fake street2_t")

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("WH1")
    popup.locator("css=.name").fill("Warehouse 1 (modified)")
    popup.locator("css=.address").fill("fake street2")
    popup.locator("css=.ok").click()

def test_wh_create_remove(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Warehouses").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("WH1_t" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Warehouse 1 (modified)_t")
    popup.locator("css=.address").fill("fake street2_t")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Warehouse 1 (modified)_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("fake street2_t")

    page.locator("css=.list-item").filter(has_text="WH1_t").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("WH1_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Warehouse 1 (modified)_t")

def test_wh_archived(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Warehouses").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("WH1_ta" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Warehouse 1 (modified)_t")
    popup.locator("css=.address").fill("fake street2_t")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Warehouse 1 (modified)_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1_ta")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("fake street2_t")

    page.locator("css=.list-item").filter(has_text="WH1_t").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("WH1_ta")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Warehouse 1 (modified)_t")

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("WH1_ta")
    page.locator("css=.list-archived").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1_ta")

    

def test_storage_list(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST1")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST2")

def test_storage_modify(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("ST_test")
    popup.locator("css=.name").fill("Test storage")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST_test")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Test storage")

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("ST2")
    popup.locator("css=.name").fill("Storage 2")
    popup.locator("css=.ok").click()

def test_storage_create_remove(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("ST_test" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Test storage")
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST_test")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Test storage")

    page.locator("css=.list-item").filter(has_text="ST_test").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("ST_test")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Test storage")

def test_storage_archived(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body") 
    popup.locator("css=.id").fill("ST_test_archived" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Test storage")
    popup.locator("css=.ok").click()

    page.locator("css=.list-item").filter(has_text="ST_test").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("ST_test_archived")
    page.locator("css=.list-archived").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST_test_archived")
    

def test_storage_limit(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    expect(popup).to_be_visible()

    popup.locator("css=.edit-btn").nth(0).click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.amount").nth(0).fill("20")
    popup2.locator("css=.ok").click()
    page.wait_for_load_state('networkidle')
    page.wait_for_timeout(100)
    expect(popup.locator("css=.stat")).to_contain_text("20")
    popup.locator("css=.cancel").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    expect(popup.locator("css=.stat").nth(0)).to_contain_text("20")

    popup.locator("css=.edit-btn").nth(0).click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.amount").nth(0).fill("100")
    popup2.locator("css=.ok").click()
    page.wait_for_load_state('networkidle')
    page.wait_for_timeout(100)
    popup.locator("css=.cancel").click()

def test_storage_capacity(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.capacity").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    expect(popup).to_contain_text("/100")
    popup.locator("css=.cancel").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.edit-btn").nth(0).click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.amount").nth(0).fill("80")
    popup2.locator("css=.ok").click()
    page.wait_for_load_state('networkidle')
    page.wait_for_timeout(100)
    popup.locator("css=.cancel").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.capacity").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    expect(popup).to_contain_text("/80")
    popup.locator("css=.cancel").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.edit-btn").nth(0).click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.amount").nth(0).fill("100")
    popup2.locator("css=.ok").click()
    page.wait_for_load_state('networkidle')
    page.wait_for_timeout(100)
    popup.locator("css=.cancel").click()
    
def test_operation_add_for_test(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    if (page.locator("css=.list-item").locator("visible=true").count() == 0):
        page.locator("css=.add-btn").locator("visible=true").click()
        popup = page.locator("css=.popup-body")
        expect(popup).to_be_visible()
        
        popup.locator("css=.id").fill("OP0001" + datetime.now().strftime('%H:%M:%S'))
        popup.locator("css=.name").fill("Test Operation")
        popup.locator("css=.ok").locator("visible=true").click()

def test_operations_list(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("OP0001")
def test_operation_view(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.view-btn").nth(1).click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    expect(popup).to_contain_text("Test Operation")
    expect(popup).to_contain_text("Insertion")

    popup.locator("css=.cancel").click()
    expect(popup).to_be_hidden()

def test_operation_decline(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()
    page.locator("css=.search-btn").click()
    amount = page.locator("css=.amount").locator("visible=true").nth(0).inner_html()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    expect(page.locator("css=.list-item").locator("visible=true").nth(0)).to_be_visible()
    opCount = page.locator("css=.list-item").locator("visible=true").count()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("Operation_test" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Test Operation")
    popup.locator("css=.add-btn").click()

    popup2 = page.locator("css=.popup-body").nth(1)
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    page.locator("css=.popup-body").nth(1).locator("css=.ok").click()
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(opCount+1)
    operation = page.locator("css=.list-item").locator("visible=true").nth(opCount)
    expect(operation).to_contain_text("Operation_test")
    operation.locator("css=.cancel-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    page.locator("css=.reload-btn").click
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(opCount)
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()
    page.locator("css=.search-btn").click()
    expect(page.locator("css=.amount").locator("visible=true").nth(0)).to_contain_text(amount)

def test_operation_remove(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()
    page.locator("css=.search-btn").click()
    amount = page.locator("css=.amount").locator("visible=true").nth(0).inner_html()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    expect(page.locator("css=.list-item").locator("visible=true").nth(0)).to_be_visible()
    opCount = page.locator("css=.list-item").locator("visible=true").count()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("Operation_test" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Test Operation")
    popup.locator("css=.add-btn").click()

    popup2 = page.locator("css=.popup-body").nth(1)
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    page.locator("css=.popup-body").nth(1).locator("css=.ok").click()
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(opCount+1)
    operation = page.locator("css=.list-item").locator("visible=true").nth(opCount)
    expect(operation).to_contain_text("Operation_test")
    operation.locator("css=.accept-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    page.locator("css=.reload-btn").locator("visible=true").click()
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(opCount)
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()
    page.locator("css=.search-btn").click()

    newAmount = str(int(amount.split(" ")[0]) - 1)
    expect(page.locator("css=.amount").locator("visible=true").nth(0)).to_contain_text(newAmount)

def test_operation_add(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()
    page.locator("css=.search-btn").click()
    amount = page.locator("css=.amount").locator("visible=true").nth(0).inner_html()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    expect(page.locator("css=.list-item").locator("visible=true").nth(0)).to_be_visible()
    opCount = page.locator("css=.list-item").locator("visible=true").count()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()

    popup.locator("css=.id").fill("Operation_test_add" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Test Operation")
    popup.locator("css=.is-add").click()
    popup.locator("css=.add-btn").click()

    popup2 = page.locator("css=.popup-body").nth(1)
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    page.locator("css=.popup-body").nth(1).locator("css=.ok").click()
    popup.locator("css=.ok").click()

    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(opCount+1)
    operation = page.locator("css=.list-item").locator("visible=true").nth(opCount)
    expect(operation).to_contain_text("Operation_test")
    operation.locator("css=.accept-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    page.locator("css=.reload-btn").locator("visible=true").click()
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(opCount)
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()
    page.locator("css=.search-btn").click()
    newAmount = str(int(amount.split(" ")[0]) + 1)
    expect(page.locator("css=.amount").locator("visible=true").nth(0)).to_contain_text(newAmount)

def test_operation_archived(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    opCount = page.locator("css=.list-item").locator("visible=true").count()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    
    popup.locator("css=.id").fill("Operation_test_archived " + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Test Operation")
    popup.locator("css=.ok").click()

    operation = page.locator("css=.list-item").locator("visible=true").nth(-1)
    operation.locator("css=.accept-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()

    expect(page.locator("css=.list-content").locator("visible=true")).not_to_contain_text("Operation_test_archived")
    page.locator("css=.list-archived").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Operation_test_archived")

def test_newuser_login(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    
    popup.locator("css=.id").fill("test")
    popup.locator("css=.name").fill("Teszter Péter")
    popup.locator("css=.password").fill("test")
    popup.locator("css=.manager").click()
    popup2 = page.locator("css=.popup-body").nth(1)
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()

    page.locator("css=.logout-button").click()

    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    expect(page.locator("css=.logged-in-page")).to_be_visible()
    expect(page.locator("css=.login-page")).to_be_hidden()
    expect(page.locator("css=.login-name")).to_contain_text("Teszter Péter")

def test_newuser_changepassword(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Profile").click()
    page.locator("css=.password-change").click()
    popup = page.locator("css=.popup-body").locator("visible=true")

    popup.locator("css=.new_password").fill("12345")
    popup.locator("css=.new_password_again").fill("12345")
    popup.locator("css=.old_password").fill("test")
    popup.locator("css=.ok").click()

    page.locator("css=.logout-button").click()
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("12345")
    page.locator("css=.login-button").click()

    expect(page.locator("css=.logged-in-page")).to_be_visible()
    expect(page.locator("css=.login-page")).to_be_hidden()
    expect(page.locator("css=.login-name")).to_contain_text("Teszter Péter")

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Profile").click()
    page.locator("css=.password-change").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.new_password").fill("test")
    popup.locator("css=.new_password_again").fill("test")
    popup.locator("css=.old_password").fill("12345")
    popup.locator("css=.ok").click()

def test_newuser_authorizations_addwh(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(2).locator("css=.local-authorizations").click()

    popup0 = page.locator("css=.popup-body").locator("visible=true")
    popup0.locator("css=.list-item-content").nth(0).click()
    popup0.locator("css=.ok").click()

    popup = page.locator("css=.popup-body").locator("visible=true")
    expect(popup).to_be_visible()
    
    popup.locator("css=.authorization-grant").nth(0).click()
    popup.locator("css=.cancel").click()

def test_newuser_authorizations_local_deny_operations(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()    
    popup.locator("css=.id").fill("OP_fail" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("OP_fail")
    popup.locator("css=.ok").locator("visible=true").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("OP_fail")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()    
    popup.locator("css=.id").fill("OP_fail" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("OP_fail")
    popup.locator("css=.is-add").click()
    popup.locator("css=.ok").locator("visible=true").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("OP_fail")
    
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()
    page.locator("css=.view-btn").nth(1).click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    expect(popup).to_contain_text("Test Operation")
    expect(popup).to_contain_text("Insertion")
    popup.locator("css=.cancel").click()
    expect(popup).to_be_hidden()

    page.locator("css=.list-item").locator("visible=true").nth(0).locator("css=.accept-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(-1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("OP3")

    page.locator("css=.list-item").locator("visible=true").nth(0).locator("css=.cancel-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(-1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("OP3")

def test_newuser_authorizations_local_deny_storages(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.edit-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("css=.name").fill("Storage_fail")
    page.locator("css=.popup-body").locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Storage_fail")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("Storage_fail")
    popup.locator("css=.name").fill("Storage_fail")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Storage_fail")

    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true")).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Storage 1")

def test_newuser_authorizations_local_deny_limits(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.capacity").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    expect(popup).to_contain_text("/100")
    popup.locator("css=.cancel").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    expect(popup).to_be_visible()
    popup.locator("css=.edit-btn").nth(0).click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.amount").nth(0).fill("20")
    popup2.locator("css=.ok").click()
    #expect(page.locator("css=.popup-body").locator("visible=true").nth(2)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(2).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Storage_fail")
    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    expect(popup.locator("css=.stat").nth(0)).not_to_contain_text("20")
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    
def test_newuser_authorizations_local_allow_init(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(2).locator("css=.local-authorizations").click()
    page.locator("css=.popup-body").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup-body").locator("visible=true").locator("css=.ok").nth(0).click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()

    page.wait_for_timeout(100)
    authorizations = page.locator("css=.popup-body").locator("css=.authorization-grant").locator("visible=true").count()
    for a in range(authorizations):
        page.locator("css=.popup-body").locator("css=.authorization-grant").locator("visible=true").nth(0).click()
    popup.locator("css=.cancel").click()
    page.locator("css=.logout-button").click()

def test_newuser_authorizations_local_allow_operations(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()    
    popup.locator("css=.id").fill("OP_success_remove" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("OP_success_remove")
    popup.locator("css=.ok").locator("visible=true").click()
    expect(page.locator("css=.list-content").locator("visible=true")).to_contain_text("OP_success_remove")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()    
    popup.locator("css=.id").fill("OP_success_add" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("OP_success_add")
    popup.locator("css=.is-add").click()
    popup.locator("css=.ok").locator("visible=true").click()
    expect(page.locator("css=.list-content").locator("visible=true")).to_contain_text("OP_success_add")


    page.locator("css=.list-item").locator("visible=true").nth(1).locator("css=.accept-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.list-content").locator("visible=true")).not_to_contain_text("OP_success_add")
    page.locator("css=.list-item").locator("visible=true").nth(1).locator("css=.cancel-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.list-content").locator("visible=true")).not_to_contain_text("OP_success_remove")

def test_newuser_authorizations_local_allow_storages(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("ST_test")
    popup.locator("css=.name").fill("Test storage")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST_test")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Test storage")
    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("ST2")
    popup.locator("css=.name").fill("Storage 2")
    popup.locator("css=.ok").click()

def test_newuser_authorizations_local_allow_limits(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    expect(popup).to_be_visible()

    popup.locator("css=.edit-btn").nth(0).click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.amount").nth(0).fill("20")
    popup2.locator("css=.ok").click()
    page.wait_for_load_state('networkidle')
    page.wait_for_timeout(100)
    expect(popup.locator("css=.stat")).to_contain_text("20")
    popup.locator("css=.cancel").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.limits").click()
    expect(popup.locator("css=.stat").nth(0)).to_contain_text("20")

    popup.locator("css=.edit-btn").nth(0).click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup2.locator("css=.amount").nth(0).fill("100")
    popup2.locator("css=.ok").click()
    page.wait_for_load_state('networkidle')
    page.wait_for_timeout(100)
    popup.locator("css=.cancel").click()

def test_newuser_authorizations_local_allow_revert(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(2).locator("css=.local-authorizations").click()
    page.locator("css=.popup-body").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup-body").locator("visible=true").locator("css=.ok").nth(0).click()

    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()

    page.wait_for_timeout(100)
    authorizations = page.locator("css=.popup-body").locator("css=.authorization-revoke").locator("visible=true").count()-1
    for a in range(authorizations):
        page.locator("css=.popup-body").locator("css=.authorization-revoke").locator("visible=true").nth(1).click()
            
    popup.locator("css=.cancel").click()
    page.locator("css=.logout-button").click()


def test_newuser_authorizations_system_deny_wh(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Warehouses").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_have_count(1)

    page.locator("css=.delete-btn").locator("visible=true").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true")).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("WH_fail")
    popup.locator("css=.name").fill("WH_fail")
    popup.locator("css=.address").fill("WH_fail")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("WH_fail")

    page.locator("css=.edit-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("css=.name").fill("WH_fail")
    page.locator("css=.popup-body").locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("WH_fail")

def test_newuser_authorizations_system_deny_item(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Items").click()

    page.locator("css=.delete-btn").locator("visible=true").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true")).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Répa")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("Item_fail")
    popup.locator("css=.name").fill("Item_fail")
    popup.locator("css=.unit").click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    expect(popup2).to_contain_text("pallet")
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Item_fail")

    page.locator("css=.edit-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("css=.name").fill("Item_fail")
    page.locator("css=.popup-body").locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Item_fail")

def test_newuser_authorizations_system_deny_unit(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Units").click()

    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true")).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Raklap")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("Unit_fail")
    popup.locator("css=.name").fill("Unit_fail")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Unit_fail")

    page.locator("css=.edit-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("css=.name").fill("Unit_fail")
    page.locator("css=.popup-body").locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Unit_fail")

def test_newuser_authorizations_system_deny_user(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_have_count(1)

    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true")).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Teszter Péter")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("User_fail")
    popup.locator("css=.name").fill("User_fail")
    popup.locator("css=.password").fill("Alma0!")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("User_fail")

    page.locator("css=.edit-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("css=.name").fill("User_fail")
    page.locator("css=.popup-body").locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("User_fail")

def test_newuser_authorizations_local_deny_storages(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.edit-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("css=.name").fill("Storage_fail")
    page.locator("css=.popup-body").locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Storage_fail")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("Storage_fail")
    popup.locator("css=.name").fill("Storage_fail")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true").nth(1)).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(1).locator("css=.cancel").click()
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Storage_fail")

    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.popup-body").locator("visible=true")).to_contain_text("Forbidden")
    page.locator("css=.popup-body").locator("visible=true").nth(0).locator("css=.cancel").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Storage 1")

def test_newuser_authorizations_system_allow_init(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(2).locator("css=.authorizations").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()

    page.wait_for_timeout(100)
    authorizations = page.locator("css=.popup-body").locator("css=.authorization-grant").locator("visible=true").count()
    for a in range(authorizations):
        page.locator("css=.popup-body").locator("css=.authorization-grant").locator("visible=true").nth(0).click()
    popup.locator("css=.cancel").click()
    page.locator("css=.logout-button").click()
    expect(page.locator("css=.popup-body").locator("css=.authorization-grant").locator("visible=true")).to_have_count(0)

def test_newuser_authorizations_system_allow_wh(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Warehouses").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("WH1_t" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Warehouse 1 (modified)_t")
    popup.locator("css=.address").fill("fake street2_t")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Warehouse 1 (modified)_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("fake street2_t")

    page.locator("css=.list-item").filter(has_text="WH1_t").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("WH1_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Warehouse 1 (modified)_t")

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("WH1_t")
    popup.locator("css=.name").fill("Warehouse 1 (modified)_t")
    popup.locator("css=.address").fill("fake street2_t")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Warehouse 1 (modified)_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("WH1_t")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("fake street2_t")
    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("WH1")
    popup.locator("css=.name").fill("Warehouse 1 (modified)")
    popup.locator("css=.address").fill("fake street2")
    popup.locator("css=.ok").click()

def test_newuser_authorizations_system_allow_item(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Items").click()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("potato_box_teszt" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Krumpli")
    popup.locator("css=.unit").click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    expect(popup2).to_contain_text("pallet")
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box_teszt")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Krumpli")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Raklap")

    page.locator("css=.list-item").filter(has_text="potato_box_teszt").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("potato_box_teszt")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Krumpli")

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("potato_box_teszt")
    popup.locator("css=.name").fill("Krumpli")
    popup.locator("css=.unit").click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    expect(popup2).to_contain_text("pallet")
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box_teszt")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Krumpli")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Raklap")
    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("carrot_box")
    popup.locator("css=.name").fill("Répa")
    popup.locator("css=.unit").click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    expect(popup2).to_contain_text("small_box")
    popup2.locator("css=.list-item-content").nth(1).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()

def test_newuser_authorizations_system_allow_unit(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Units").click()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("potato_box" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Krumpli")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("potato_box")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Krumpli")

    page.locator("css=.list-item").filter(has_text="potato_box").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("potato_box")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Krumpli")

    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("test_box")
    popup.locator("css=.name").fill("Teszt")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("test_box")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Teszt")
    page.locator("css=.list-item-actions").locator("visible=true").nth(0).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("pallet")
    popup.locator("css=.name").fill("Raklap")
    popup.locator("css=.ok").click()

def test_newuser_authorizations_system_allow_user(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("janos1990")

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("test_user")
    popup.locator("css=.name").fill("test user")
    popup.locator("css=.password").fill("Alma0!")
    popup.locator("css=.manager").click()
    popup2 = page.locator("css=.popup-body").nth(1)
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("test_user")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("test user")

    page.locator("css=.list-item-actions").locator("visible=true").nth(2).locator("css=.edit-btn").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("test_modified")
    popup.locator("css=.name").fill("test_modified")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("modified")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("modified")
    page.locator("css=.list-item-actions").locator("visible=true").nth(2).locator("css=.edit-btn").click()
    expect(popup).to_contain_text("Teszter Péter")    
    popup.locator("css=.id").fill("test_user")
    popup.locator("css=.name").fill("test user")
    popup.locator("css=.ok").click()

    page.locator("css=.list-item").filter(has_text="test_user").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("test_user")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("test user")

def test_newuser_authorizations_system_allow_storages(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill("test")
    page.locator("css=.password").fill("test")
    page.locator("css=.login-button").click()
    
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("ST_test")
    popup.locator("css=.name").fill("Test storage")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST_test")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Test storage")
    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.edit-btn").click()
    popup.locator("css=.id").fill("ST2")
    popup.locator("css=.name").fill("Storage 2")
    popup.locator("css=.ok").click()

    page.locator("css=.add-btn").locator("visible=true").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()
    popup.locator("css=.id").fill("ST_test" + datetime.now().strftime('%H:%M:%S'))
    popup.locator("css=.name").fill("Test storage")
    popup.locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("ST_test")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Test storage")

    page.locator("css=.list-item").filter(has_text="ST_test").locator("css=.delete-btn").click()
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.ok").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("ST_test")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Test storage")

def test_newuser_authorizations_system_allow_revert(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    page.locator("css=.list-item-actions").locator("visible=true").nth(-1).locator("css=.authorizations").click()
    popup = page.locator("css=.popup-body")
    expect(popup).to_be_visible()

    page.wait_for_timeout(100)
    authorizations = page.locator("css=.popup-body").locator("css=.authorization-revoke").locator("visible=true").count()
    for a in range(authorizations):
        page.locator("css=.popup-body").locator("css=.authorization-revoke").locator("visible=true").nth(0).click()
    popup.locator("css=.cancel").click()
    page.locator("css=.logout-button").click()

def test_load_database_big(page: Page):
    file=open("testdata_big.json","r")
    c=file.read()
    file.close()
    apitest("PUT",API+"/db","tester",c,204,None)

def test_statistics(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Statistics").click()

    graph = page.locator("css=.graph-inner")
    expect(graph.locator("css=path")).to_have_count(0)

    page.locator("css=.item").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.list-item-content").nth(0).click()
    popup.locator("css=.ok").click()    

    page.locator("css=.statistics-update").locator("visible=true").click()
    expect(graph.locator("css=path")).not_to_have_count(0)

def test_items_pagination(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Items").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Acél")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Beton")

    page.locator("css=.next-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Acél")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Beton")

    page.locator("css=.prev-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Acél")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Beton")

    page.locator("css=.list-query").locator("visible=true").fill("Homok")
    page.locator("css=.list-query").locator("visible=true").press("Enter")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("sand")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Acél")
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(1)

def test_units_pagination(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Units").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("cm")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("tincan")

    page.locator("css=.next-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("cm")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("tincan")

    page.locator("css=.prev-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("cm")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("tincan")

    page.locator("css=.list-query").locator("visible=true").fill("Konzervdoboz")
    page.locator("css=.list-query").locator("visible=true").press("Enter")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("tincan")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("cm")
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(1)

def test_users_pagination(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Users").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Alfonso Sloan")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Bob Proctor")

    page.locator("css=.next-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Alfonso Sloan")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Bob Proctor")

    page.locator("css=.prev-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Alfonso Sloan")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Bob Proctor")

    page.locator("css=.list-query").locator("visible=true").fill("Daniel Brennan")
    page.locator("css=.list-query").locator("visible=true").press("Enter")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("daniel_brennan")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Alfonso Sloan")
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(1)

def test_warehouses_pagination(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Warehouses").click()


    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Ajka")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Szeged")

    page.locator("css=.next-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Ajka")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Szeged")

    page.locator("css=.prev-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Ajka")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Szeged")

    page.locator("css=.list-query").locator("visible=true").fill("Szeged")
    page.locator("css=.list-query").locator("visible=true").press("Enter")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("Szeged")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("Ajka")
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(1)

def test_operation_pagination(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Operations").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()
    page.locator("css=.list-archived").locator("visible=true").click()


    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("OP_WH_ajka_0")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("OP_WH_ajka_12")

    page.locator("css=.next-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("OP_WH_ajka_0")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("OP_WH_ajka_12")

    page.locator("css=.prev-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("OP_WH_ajka_0")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("OP_WH_ajka_12")

    page.locator("css=.list-query").locator("visible=true").fill("100")
    page.locator("css=.list-query").locator("visible=true").press("Enter")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("OP_WH_ajka_100")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("OP_WH_ajka_0")
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(1)

def test_search_pagination(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Search").click()

    page.locator("css=.search-btn").click()

    expect(page.locator("css=.box").locator("visible=true")).to_contain_text("Bab")
    expect(page.locator("css=.box").locator("visible=true")).not_to_contain_text("Alma")

    page.locator("css=.next-btn").locator("visible=true").click()
    expect(page.locator("css=.box").locator("visible=true")).not_to_contain_text("Bab")
    expect(page.locator("css=.box").locator("visible=true")).to_contain_text("Alma")

    page.locator("css=.prev-btn").locator("visible=true").click()
    expect(page.locator("css=.box").locator("visible=true")).to_contain_text("Bab")
    expect(page.locator("css=.box").locator("visible=true")).not_to_contain_text("Alma")

    page.locator("css=.search").locator("visible=true").fill("Homok")
    page.locator("css=.search-btn").click()
    expect(page.locator("css=.box").locator("visible=true")).to_contain_text("Homok")
    expect(page.locator("css=.box").locator("visible=true")).not_to_contain_text("Bab")
    expect(page.locator("css=.box").locator("visible=true")).to_have_count(1)

def test_storage_pagination(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.login-button").click()
    page.locator("css=.tab-header").locator("visible=true").get_by_text("Storages").click()

    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("AJK0")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("AJK25")

    page.locator("css=.next-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("AJK0")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("AJK25")

    page.locator("css=.prev-btn").locator("visible=true").click()
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("AJK0")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("AJK25")

    page.locator("css=.list-query").locator("visible=true").fill("1/4")
    page.locator("css=.list-query").locator("visible=true").press("Enter")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).to_contain_text("AJK3")
    expect(page.locator("css=.list-content-inner").locator("visible=true")).not_to_contain_text("AJK0")
    expect(page.locator("css=.list-item").locator("visible=true")).to_have_count(1)

def test_take_screenshots_login(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.screenshot(path="images/login.png")
    page.locator("css=.login-button").click()

def test_take_screenshots_item(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.locator("css=.login-button").locator("visible=true").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Termékek").click()
    page.screenshot(path="images/item.list.png")
    page.locator("css=.add-btn").locator("visible=true").click()
    page.screenshot(path="images/item.add.png")
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.id").fill("computer")
    popup.locator("css=.name").fill("számítógép")
    popup.locator("css=.unit").click()
    popup2 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    page.screenshot(path="images/unit.select.png")
    popup2.locator("css=.list-query").fill("darab")
    popup2.locator("css=.reload-btn").click()
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    page.screenshot(path="images/item.edit.png")
    popup.locator("css=.ok").click()
    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.screenshot(path="images/item.delete.png")
    page.locator("css=.cancel").locator("visible=true").click()

def test_take_screenshots_unit(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Egységek").click()
    page.screenshot(path="images/unit.list.png")
    page.locator("css=.add-btn").locator("visible=true").click()
    page.screenshot(path="images/unit.add.png")
    popup = page.locator("css=.popup-body")
    popup.locator("css=.id").fill("inch")
    popup.locator("css=.name").fill("hüvelyk")
    page.screenshot(path="images/unit.edit.png")
    popup.locator("css=.cancel").click()
    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.screenshot(path="images/unit.delete.png")
    page.locator("css=.cancel").locator("visible=true").click()

def test_take_screenshots_user(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Felhasználók").click()
    page.screenshot(path="images/user.list.png")
    page.locator("css=.add-btn").locator("visible=true").click()
    page.screenshot(path="images/user.add.png")
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.id").fill("anna")
    popup.locator("css=.name").fill("Anna")
    popup.locator("css=.password").fill("12345")
    popup.locator("css=.manager").click()
    popup2 = page.locator("css=.popup-body").nth(1)
    page.screenshot(path="images/user.select.png")
    popup2.locator("css=.list-item-content").nth(1).click()
    popup2.locator("css=.ok").click()
    page.screenshot(path="images/user.edit.png")
    popup.locator("css=.cancel").click()
    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.screenshot(path="images/user.delete.png")
    page.locator("css=.cancel").locator("visible=true").click()

    page.locator("css=.list-item").locator("visible=true").nth(1).locator("css=.authorizations").click()
    page.locator("css=.authorization-grant").locator("visible=true").nth(0).click()
    page.locator("css=.authorization-grant").locator("visible=true").nth(3).click()
    page.screenshot(path="images/authorizations.system.png")
    page.locator("css=.cancel").click()
    page.locator("css=.list-item-actions").locator("visible=true").nth(1).locator("css=.local-authorizations").click()
    popup0 = page.locator("css=.popup-body").locator("visible=true")
    popup0.locator("css=.list-item-content").nth(0).click()
    popup0.locator("css=.ok").click()
    popup = page.locator("css=.popup-body").locator("visible=true")
    expect(popup).to_be_visible()
    popup.locator("css=.authorization-grant").nth(0).click()
    popup.locator("css=.authorization-grant").nth(1).click()
    page.screenshot(path="images/authorizations.local.png")
    popup.locator("css=.cancel").click()

def test_take_screenshots_search(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Keresés").click()
    page.locator("css=.search-btn").click()
    expect(page.locator("css=table").locator("css=.spin")).to_be_hidden()
    page.screenshot(path="images/search.png")

def test_take_screenshots_warehouse(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Telephelyek").click()
    page.screenshot(path="images/warehouse.list.png")
    page.locator("css=.add-btn").locator("visible=true").click()
    page.screenshot(path="images/warehouse.add.png")
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.id").fill("WH_sátoraljaújhely")
    popup.locator("css=.name").fill("Sátoraljaújhelyi telephely")
    popup.locator("css=.address").fill("nekeresd utca 1.")
    page.screenshot(path="images/warehouse.edit.png")
    popup.locator("css=.ok").click()
    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.screenshot(path="images/warehouse.delete.png")
    page.locator("css=.cancel").locator("visible=true").click()

def test_take_screenshots_storage(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Raktárak").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.screenshot(path="images/warehouse.select.png")
    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("Sopron")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()
    page.screenshot(path="images/storage.list.png")
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("Sátoraljaújhely")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.add-btn").locator("visible=true").click()
    page.screenshot(path="images/storage.add.png")
    popup = page.locator("css=.popup-body").locator("visible=true")
    popup.locator("css=.id").fill("SAT0")
    popup.locator("css=.name").fill("Sátoraljaújhely 0")
    page.screenshot(path="images/storage.edit.png")
    popup.locator("css=.ok").click()

    page.locator("css=.delete-btn").locator("visible=true").nth(0).click()
    page.screenshot(path="images/storage.delete.png")
    page.locator("css=.cancel").locator("visible=true").click()

    page.locator("css=.limits").locator("visible=true").nth(0).click()
    page.screenshot(path="images/storage.limit.png")
    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("Számítógép")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.edit-btn").nth(0).click()
    page.locator("css=.popup").locator("visible=true").nth(1).locator("css=.amount").fill("15")
    page.screenshot(path="images/storage.limit.edit.png")
    page.locator("css=.popup").locator("visible=true").nth(1).locator("css=.ok").click()

    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("UTP kábel")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.edit-btn").nth(0).click()
    page.locator("css=.popup").locator("visible=true").nth(1).locator("css=.amount").fill("100")
    page.locator("css=.popup").locator("visible=true").nth(1).locator("css=.ok").click()
    

def test_take_screenshots_operation(page: Page):
    page.goto("http://localhost:8000/frontend2/index.html")
    page.locator("css=.localization").locator("visible=true").select_option("Magyar")
    page.locator("css=.username").fill(test_user)
    page.locator("css=.password").fill(test_user)
    page.locator("css=.theme-button").locator("visible=true").click()
    page.locator("css=.login-button").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Műveletek").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("Sopron")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()
    page.screenshot(path="images/operation.list.png")
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("Sátoraljaújhely")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()
    page.locator("css=.add-btn").locator("visible=true").click()

    popup = page.locator("css=.popup-body")
    page.screenshot(path="images/operation.add.png")
    popup.locator("css=.id").fill("OP0001")
    popup.locator("css=.name").fill("Művelet 1")
    popup.locator("css=.is-add").click()

    for i in range(1,4):
        popup.locator("css=.add-btn").click()
        popup2 = page.locator("css=.popup-body").nth(1)
        popup2.locator("css=.list-query").fill("Számítógép")
        popup2.locator("css=.reload-btn").click()
        popup2.locator("css=.list-item-content").nth(0).click()
        popup2.locator("css=.ok").click()
        popup3 = page.locator("css=.popup-body").locator("visible=true").nth(1)
        popup3.locator("css=.serial_enable").locator("visible=true").click()
        popup3.locator("css=.manufacturer_serial").fill("SZ00" + str(i))
        popup3.locator("css=.global_serial").fill("1000" + str(i))
        if(i == 1):
            page.screenshot(path="images/operation.item.add.png")
        popup3.locator("css=.ok").click()

    popup.locator("css=.add-btn").click()
    popup2 = page.locator("css=.popup-body").nth(1)
    popup2.locator("css=.list-query").fill("UTP kábel")
    popup2.locator("css=.reload-btn").click()
    popup2.locator("css=.list-item-content").nth(0).click()
    popup2.locator("css=.ok").click()
    popup3 = page.locator("css=.popup-body").locator("visible=true").nth(1)
    popup3.locator("css=.amount").fill("30")
    popup3.locator("css=.storage").click()
    page.screenshot(path="images/storage.select.png")
    page.locator("css=.popup-body").locator("visible=true").nth(-1).locator("css=.cancel").click()
    popup3.locator("css=.ok").click()
    popup.locator("css=.ok").click()

    page.locator("css=.list-item").locator("visible=true").nth(0).locator("css=.view-btn").click()
    page.screenshot(path="images/operation.view.png")
    page.locator("css=.cancel").click()

    page.locator("css=.cancel-btn").locator("visible=true").nth(0).click()
    page.screenshot(path="images/operation.cancel.png")
    page.locator("css=.cancel").locator("visible=true").click()

    page.locator("css=.accept-btn").locator("visible=true").nth(0).click()
    page.screenshot(path="images/operation.commit.png")
    page.locator("css=.ok").locator("visible=true").click()

    page.locator("css=.tab-header").locator("visible=true").get_by_text("Raktárak").click()
    page.locator("css=.list-warehouse").locator("visible=true").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("Sátoraljaújhely")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-item-content").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.ok").click()

    page.locator("css=.capacity").locator("visible=true").nth(0).click()
    page.locator("css=.popup").locator("visible=true").locator("css=.list-query").fill("Számítógép")
    page.locator("css=.popup").locator("visible=true").locator("css=.reload-btn").click()
    expect(page.locator("css=.spinner").locator("visible=true")).to_have_count(0)
    page.screenshot(path="images/storage.capacity.png")
    
    
    

    

