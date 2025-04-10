"use strict";

class ItemDialog extends EditDialog {
    selected_unit=null;
    constructor(item_callback,item) {
        super("item-popup",item_callback,item);
    }
    initTemplate() {
        let t=this.view_template;
        if(this.item!==null) {
            t.get("id").value=this.item.id;
            t.get("name").value=this.item.name;
            this.selected_unit=this.item.unit;
        }
        
        createSelectButton(t.get("unit"),()=>new UnitSelectView(),s=>this.selected_unit=s,this.selected_unit,"unit.null");
    }
    getItem() {
        let t=this.view_template;
        let id=t.get("id").value;
        let name=t.get("name").value;
        let unit=this.selected_unit;

        if(id==="")
            id=randomId();

        if(name.length==0)
            name=id;

        if(unit===null)
            throw Localization.CURRENT.get("unit.null");
        return {id:id,name:name,unit:unit};
    }
}

class ItemView extends ListEditView {
    load(q,archived,offset,length) {
        return request("GET","/items"+pageParams(q,archived,offset,length),undefined,"item.fail.list");
    }
    createItemNode(item) {
        let t=new Template("item-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
        t.get("unit").textContent=item.unit.name;
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
        return Promise.resolve(new ItemDialog(item_callback,item));
    }
    delete(item) {
        return request("DELETE","/items/"+item.id,undefined,"item.fail.delete");
    }
    save(original,modified) {
        if(original===null||original.id===modified.id)
            return request("PUT","/items/"+modified.id+putParams(original===null),{
                name:modified.name,
                unit:modified.unit.id
            },"item.fail."+(original===null?"create":"modify"));
        else
            return request("POST","/items",{
                from:original.id,
                to:modified.id
            },"item.fail.move").then(this.save(modified,modified));
    }
}

class ItemSelectView extends SelectView {
    constructor() {
        super();
    }
    load(q,archived,offset,length) {
        return request("GET","/items"+pageParams(q,archived,offset,length),undefined,"item.fail.list");
    }
    createItemNode(item) {
        let t=new Template("item-select-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
        return t.getNode();
    }
}
