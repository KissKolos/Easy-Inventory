"use strict";

class StorageDialog extends EditDialog {
    constructor(item_callback,item) {
        super("storage-popup",item_callback,item);
    }
    initTemplate() {
        if(this.item!==null) {
            let t=this.view_template;
            t.get("id").value=this.item.id;
            t.get("name").value=this.item.name;
        }
    }
    getItem() {
        let t=this.view_template;
        let id=t.get("id").value;
        let name=t.get("name").value;

        if(id==="")
            id=randomId();

        if(name.length==0)
            name=id;

        return {id:id,name:name};
    }
}

class ViewDialog extends DialogView {
    inner_view;
    constructor(inner_view) {
        super("empty-template");
        this.inner_view=inner_view;
    }
    hasOk() {
        return false;
    }
    initTemplate() {
        this.view_template.get("content").appendChild(this.inner_view.getNode());
    }
}

class StorageView extends LocalListEditView {
    load(q,archived,offset,length) {
        if(this.selected_warehouse===null)
            return Promise.resolve([]);
        return request("GET","/warehouses/"+this.selected_warehouse.id+"/storages"+pageParams(q,archived,offset,length),undefined,"storage.fail.list");
    }
    createItemNode(item) {
        let t=new Template("storage-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
        t.get("limits").onclick=()=>showDialog(Promise.resolve(new ViewDialog(new StorageLimitView(item))));
        t.get("capacity").onclick=()=>showDialog(Promise.resolve(new ViewDialog(new StorageCapacityView(item))));
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
        if(this.selected_warehouse===null)
            return Promise.resolve(new MessageDialog(Localization.CURRENT.get("warehouse.not_selected")));
        return Promise.resolve(new StorageDialog(item_callback,item));
    }
    delete(item) {
        return request("DELETE","/warehouses/"+this.selected_warehouse.id+"/storages/"+item.id,undefined,"storage.fail.delete");
    }
    save(original,modified) {
        if(original===null||original.id===modified.id)
            return request("PUT","/warehouses/"+this.selected_warehouse.id+"/storages/"+modified.id+putParams(original===null),{
                name:modified.name
            },"storage.fail."+(original===null?"create":"modify"));
        else
            return request("POST","/warehouses/"+this.selected_warehouse.id+"/storages",{
                from:original.id,
                to:modified.id
            },"storage.fail.move").then(this.save(modified,modified));
    }
}

class StorageSelectView extends SelectView {
    warehouse;
    constructor(warehouse) {
        super();
        this.warehouse=warehouse;
    }
    load(q,archived,offset,length) {
        return request("GET","/warehouses/"+this.warehouse+"/storages"+pageParams(q,archived,offset,length),undefined,"storage.fail.list").then(r=>{
            r.splice(0,0,null);
            return r;
        });
    }
    createItemNode(item) {
        let t=new Template("storage-select-template");
        if(item===null) {
            t.get("id").textContent="";
            t.get("name").textContent=Localization.CURRENT.get("storage.any");
        }else{
            t.get("id").textContent=item.id;
            t.get("name").textContent=item.name;
        }
        return t.getNode();
    }
}

class StorageLimitEditDialog extends EditDialog {
    constructor(item_callback,item) {
        super("storage-limit-popup",item_callback,item);
    }
    initTemplate() {
        if(this.item!==null) {
            let t=this.view_template;
            t.get("amount").value=this.item;
        }
    }
    getItem() {
        return parseInt(this.view_template.get("amount").value);
    }
}

class StorageLimitView extends ListView {
    storage;
    constructor(storage) {
        super();
        this.storage=storage;
    }
    load(q,archived,offset,length) {
        return request("GET","/warehouses/"+this.storage.warehouse.id+"/storages/"+this.storage.id+"/limits"+pageParams(q,archived,offset,length),undefined,"storage.fail.listlimit");
    }
    initTemplate() {
        this.view_template.getNode().classList.add("limit-list");
        this.view_template.get("list-archived-outer").style.display="none";
        super.initTemplate();
    }
    createItemCard(v) {
        let item=new Template("storage-limit-row");
        item.get("name").textContent=v.item.name;

        item.get("stat").textContent=v.amount;

        item.get("edit-btn").onclick=()=>showDialog(Promise.resolve(new StorageLimitEditDialog((o,amount)=>{
            if(o===amount)
                return Promise.resolve();
            return request("PUT","/warehouses/"+this.storage.warehouse.id+"/storages/"+this.storage.id+"/limits/"+v.item.id,{
                amount:amount
            },"storage.fail.limit").then(()=>this.reload());
        },v.amount)));
        return item.getNode();
    }
}

class StorageCapacityView extends ListView {
    storage;
    constructor(storage) {
        super();
        this.storage=storage;
    }
    load(q,archived,offset,length) {
        return request("GET","/warehouses/"+this.storage.warehouse.id+"/storages/"+this.storage.id+"/capacity"+pageParams(q,archived,offset,length),undefined,"storage.fail.listcapacity");
    }
    initTemplate() {
        this.view_template.getNode().classList.add("capacity-list");
        this.view_template.get("list-archived-outer").style.display="none";
        super.initTemplate();
    }
    createItemCard(v) {
        let item=new Template("storage-capacity-row");
        item.get("name").textContent=v.item.name;

        let st=item.get("stat");
        if(v.limit===0) {
            st.textContent=v.stored_amount+"/"+v.limit;
            if(v.stored_amount>0)
                st.style.color="rgb(255,0,0)";
        }else{
            let ratio=v.stored_amount/v.limit;
            st.textContent=v.stored_amount+"/"+v.limit+" ("+Math.floor(ratio*100)+"%)";
            st.style.color="rgb("+(Math.min(ratio,1)*255)+","+((1-Math.min(ratio,1))*255)+",0)";
        }
        return item.getNode();
    }
}