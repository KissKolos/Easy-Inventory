"use strict";

class UnitDialog extends EditDialog {
    constructor(item_callback,item) {
        super("unit-popup",item_callback,item);
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

        if(name.length===0)
            name=id;

        return {id:id,name:name};
    }
}

class UnitView extends ListEditView {
    load(q,archived,offset,length) {
        return request("GET","/units"+pageParams(q,archived,offset,length),undefined,"unit.fail.list");
    }
    createItemNode(item) {
        let t=new Template("unit-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
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
        return Promise.resolve(new UnitDialog(item_callback,item));
    }
    delete(item) {
        return request("DELETE","/units/"+item.id,undefined,"unit.fail.delete");
    }
    save(original,modified) {
        if(original===null||original.id===modified.id)
            return request("PUT","/units/"+modified.id+putParams(original===null),{
                name:modified.name
            },"unit.fail."+(original===null?"create":"modify"));
        else
            return request("POST","/units",{
                from:original.id,
                to:modified.id
            },"unit.fail.move").then(this.save(modified,modified));
    }
}

class UnitSelectView extends SelectView {
    constructor(select_callback) {
        super(select_callback);
    }
    load(q,archived,offset,length) {
        return request("GET","/units"+pageParams(q,archived,offset,length),undefined,"unit.fail.list");
    }
    createItemNode(item) {
        let t=new Template("unit-select-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
        return t.getNode();
    }

}
