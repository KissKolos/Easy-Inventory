"use strict";

class OperationItemDialog extends EditDialog {
    warehouse;
    selected_storage=null;
    selected_item;
    constructor(item_callback,warehouse,selected_item) {
        super("operation-item-popup",item_callback,null);
        this.warehouse=warehouse;
        this.selected_item=selected_item;
    }
    initTemplate() {
        let t=this.view_template;

        createSelectButton(t.get("storage"),()=>new StorageSelectView(this.warehouse.id),s=>this.selected_storage=s===null?null:s.id,null,"storage.any");

        let manufacturer_serial=t.get("manufacturer_serial");
        let global_serial=t.get("global_serial");
        let serial_enable=t.get("serial_enable");
        let amount=t.get("amount");

        global_serial.disabled=true;
        manufacturer_serial.disabled=true;
        serial_enable.onchange=()=>{
            global_serial.disabled=!serial_enable.checked;
            manufacturer_serial.disabled=!serial_enable.checked;
            amount.disabled=serial_enable.checked;

            if(serial_enable.checked) {
                amount.value="1";
            }else{
                global_serial.value="";
                manufacturer_serial.value="";
            }
        };

        t.get("amount-unit").textContent=this.selected_item.unit.name;
    }
    getItem() {
        let t=this.view_template;
        let lot=t.get("lot").value;
        let manufacturer_serial=t.get("manufacturer_serial").value;
        let serial_enable=t.get("serial_enable").checked;
        let global_serial=parseInt(t.get("global_serial").value);
        let amount=parseInt(t.get("amount").value);

        if(isNaN(global_serial))
            global_serial=null;
        if(lot==="")
            lot=null;
        if(manufacturer_serial==="")
            manufacturer_serial=null;

        if(serial_enable&&global_serial===null)
            global_serial=Math.floor(Math.random()*100000);

        return {storage:this.selected_storage,type:this.selected_item,lot:lot,manufacturer_serial:manufacturer_serial,global_serial:global_serial,amount:amount};
    }
}

class OperationDialog extends EditDialog {
    warehouse;
    items=[];
    constructor(item_callback,item,warehouse) {
        super("operation-popup",item_callback,item);
        this.warehouse=warehouse;
    }
    addItem(item) {
        this.items.push(item);

        let row=new Template("operation-item-row");
        row.get("storage").textContent=item.storage;
        row.get("type").textContent=item.type.name;
        row.get("lot").textContent=item.lot;
        row.get("manufacturer_serial").textContent=item.manufacturer_serial;
        row.get("global_serial").textContent=item.global_serial;
        row.get("amount").textContent=item.amount+" "+item.type.unit.name;
        row.get("remove-btn").onclick=()=>{
            this.items=this.items.filter(v=>item!==v);
            row.getNode().remove();
        };
        this.view_template.get("content").appendChild(row.getNode());
    }
    initTemplate() {
        let t=this.view_template;
        t.get("add-btn").onclick=()=>{
            showDialog(Promise.resolve(new SelectDialog(new ItemSelectView(),(s)=>{
                console.log(s);
                showDialog(Promise.resolve(new OperationItemDialog((_,item)=>{
                    console.log(item);
                    this.addItem(item);
                    return Promise.resolve();
                },this.warehouse,s)));
                return Promise.resolve();
            })));
        };
    }
    getItem() {
        let t=this.view_template;
        let id=t.get("id").value;
        let name=t.get("name").value;
        let is_add=t.get("is-add").checked;

        if(id==="")
            id=randomId();

        if(name.length==0)
            name=id;

        return {id:id,name:name,is_add:is_add,items:this.items};
    }
}

class OperationViewDialog extends DialogView {
    item;
    constructor(item) {
        super("operation-view-popup");
        this.item=item;
    }
    initTemplate() {
        let t=this.view_template;
        let content=t.get("content");
        for(let item of this.item.items) {
            let row=new Template("operation-view-item-row");
            row.get("storage").textContent=item.storage.name;
            row.get("type").textContent=item.item.name;
            row.get("lot").textContent=item.lot;
            row.get("manufacturer_serial").textContent=item.manufacturer_serial;
            row.get("global_serial").textContent=item.global_serial;
            row.get("amount").textContent=item.amount;
            content.appendChild(row.getNode());
        }
        t.get("id").textContent=this.item.id;
        t.get("name").textContent=this.item.name;
        t.get("is-add").checked=this.item.is_add;
    }
}

class OperationView extends LocalListEditView {
    load(q,archived,offset,length) {
        if(this.selected_warehouse===null)
            return Promise.resolve([]);
        return request("GET","/warehouses/"+this.selected_warehouse.id+"/operations"+pageParams(q,archived,offset,length),undefined,"operation.fail.list");
    }
    createItemNode(item) {
        let t=new Template("operation-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
        t.get("is-add").textContent=item.is_add?Localization.CURRENT.get("operation.type.insertion"):Localization.CURRENT.get("operation.type.removal");
        t.get("view-btn").onclick=()=>{
            showDialog(Promise.resolve(new OperationViewDialog(item)));
        };
        t.get("accept-btn").onclick=()=>{
            showDialog(Promise.resolve(new ConfirmDialog(Localization.CURRENT.get("dialog.confirm_delete"),()=>{
                return this.deleteOp(item,false).then(()=>{
                    this.reload();
                });
            })));
        };
        t.get("cancel-btn").onclick=()=>{
            showDialog(Promise.resolve(new ConfirmDialog(Localization.CURRENT.get("dialog.confirm_delete"),()=>{
                return this.deleteOp(item,true).then(()=>{
                    this.reload();
                });
            })));
        };
        if(item.commited!==null){
            t.get("list-item-actions").classList.add("hidden");
            t.get("archived").classList.remove("hidden");
            t.get("archived").textContent=new Date(item.commited*1000).toLocaleString();
        }
        return t.getNode();
    }
    createEditView(item,item_callback) {
        if(this.selected_warehouse===null)
            return Promise.resolve(new MessageDialog(Localization.CURRENT.get("warehouse.not_selected")));
        return Promise.resolve(new OperationDialog(item_callback,item,this.selected_warehouse));
    }
    deleteOp(item,cancel) {
        return request("DELETE","/warehouses/"+this.selected_warehouse.id+"/operations/"+item.id,{cancel:cancel},"operation.fail."+(cancel?"cancel":"commit"));
    }
    save(original,modified) {
        for(let m of modified.items)
            m.type=m.type.id;
        return request("PUT","/warehouses/"+this.selected_warehouse.id+"/operations/"+modified.id,{
            name:modified.name,
            is_add:modified.is_add,
            items:modified.items
        },"operation.fail.create");
    }
}
