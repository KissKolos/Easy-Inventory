"use strict";

class WarehouseDialog extends EditDialog {
    constructor(item_callback,item) {
        super("warehouse-popup",item_callback,item);
    }
    initTemplate() {
        if(this.item!==null) {
            let t=this.view_template;
            t.get("id").value=this.item.id;
            t.get("name").value=this.item.name;
            t.get("address").value=this.item.address;
        }
    }
    getItem() {
        let t=this.view_template;
        let id=t.get("id").value;
        let name=t.get("name").value;
        let address=t.get("address").value;

        if(id==="")
            id=randomId();

        if(name.length==0)
            name=id;

        return {id:id,name:name,address:address};
    }
}

class WarehouseView extends ListEditView {
    load(q,archived,offset,length) {
        return request("GET","/warehouses"+pageParams(q,archived,offset,length),undefined,"warehouse.fail.list");
    }
    createItemNode(item) {
        let t=new Template("warehouse-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
        t.get("address").textContent=item.address;
        if(item.deleted===null) {
            this.initEditDelete(t,item);
        }else{
            t.get("list-item-actions").classList.add("hidden");
            t.get("archived").classList.remove("hidden");
            t.get("archived").textContent=new Date(item.deleted*1000).toLocaleString();
        }
        return t.getNode();
    }
    createEditView(item,item_callback) {
        return Promise.resolve(new WarehouseDialog(item_callback,item));
    }
    delete(item) {
        return request("DELETE","/warehouses/"+item.id,undefined,"warehouse.fail.delete");
    }
    save(original,modified) {
        if(original===null||original.id===modified.id)
            return request("PUT","/warehouses/"+modified.id+putParams(original===null),{
                name:modified.name,
                address:modified.address
            },"warehouse.fail."+(original===null?"create":"modify"));
        else
            return request("POST","/warehouses",{
                from:original.id,
                to:modified.id
            },"warehouse.fail.move").then(this.save(modified,modified));
    }
}

class WarehouseSelectView extends SelectView {
    can_select_null;
    constructor(can_select_null) {
        super();
        this.can_select_null=can_select_null;
    }
    load(q,archived,offset,length) {
        return request("GET","/warehouses"+pageParams(q,archived,offset,length),undefined,"warehouse.fail.list").then(r=>{
            if(this.can_select_null)
                r.splice(0,0,null);
            return r;
        });
    }
    createItemNode(item) {
        let t=new Template("warehouse-select-template");
        if(item===null) {
            t.get("id").textContent="";
            t.get("name").textContent=Localization.CURRENT.get("warehouse.any");
        }else{
            t.get("id").textContent=item.id;
            t.get("name").textContent=item.name;
        }
        return t.getNode();
    }
}
