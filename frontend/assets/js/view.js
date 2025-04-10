"use strict";

class Template {
    template;
    constructor(template) {
        this.template=document.getElementById(template).content.cloneNode(true).children[0];
        if(Localization.CURRENT!==null)
            Localization.CURRENT.localize(this.template);
    }
    get(classname) {
        return this.template.getElementsByClassName(classname)[0];
    }
    getNode() {
        return this.template;
    }
}

class View {
    view_template=null;
    template_id;
    constructor(template_id) {
        this.template_id=template_id;
    }
    getNode() {
        if(this.view_template===null) {
            this.view_template=new Template(this.template_id);
            this.initTemplate();
        }
        return this.view_template.getNode();
    }
    initTemplate() {}
}

const PAGE_SIZE=20;

class AdapterView extends View {
    constructor(template_id) {
        super(template_id);
    }

}

class ListView extends View {
    offset=0;
    content;
    spinner;
    query;
    prev;
    next;
    archived;
    reloading=false;
    constructor() {
        super("list-template");
    }
    initTemplate() {
        let t=this.view_template;
        this.content=t.get("list-content-inner");
        this.spinner=t.get("spinner");
        t.get("reload-btn").onclick=()=>{
            this.reload();
        };
        this.prev=t.get("prev-btn");
        this.prev.onclick=()=>{
            this.offset-=PAGE_SIZE;
            if(this.offset<0)
                this.offset=0;
            this.reload();
        };
        this.next=t.get("next-btn");
        this.next.onclick=()=>{
            this.offset+=PAGE_SIZE;
            this.reload();
        };
        this.query=t.get("list-query");
        this.query.onkeyup=()=>{
            this.offset=0;
            this.reload();
        };
        this.archived=t.get("list-archived");
        this.archived.onchange=()=>{
            this.reload();
        };
        this.reload();
    }
    reload() {
        if(this.reloading)
            return;
        this.reloading=true;
        this.spinner.classList.remove("hidden");
        this.content.innerHTML="";

        this.prev.style.display=this.offset===0?"none":null;

        let q=this.query.value;
        let a=this.archived.checked;
        let o=this.offset;

        this.load(q,a,o,PAGE_SIZE).then(res=>{
            if(q===this.query.value&&a===this.archived.checked&&o===this.offset) {
                this.next.style.display=res.length<PAGE_SIZE?"none":null;
                for(let v of res){
                    this.content.appendChild(this.createItemCard(v));
                }
                this.spinner.classList.add("hidden");
                this.reloading=false;
            }else{
                this.reloading=false;
                this.reload();
            }
        });
    }
    /**
     * 
     * @returns {Promise<unknown[]>}
     */
    load(q,archived,offset,length) {}
    /**
     * 
     * @param {*} item 
     * @returns {Node}
     */
    createItemCard(v) {}
}

class ListEditView extends ListView {
    initTemplate() {
        let t=this.view_template;
        t.get("add-btn").onclick=()=>{
            showDialog(this.createEditView(null,(original,modified)=>{
                return this.save(original,modified).then(()=>{
                    this.reload();
                });
            }));
        };
        t.get("add-btn").classList.remove("hidden");
       super.initTemplate();
    }
    createItemCard(v) {
        let item=new Template("list-item-template");
        item.get("content").appendChild(this.createItemNode(v));
        return item.getNode();
    }
    /**
     * 
     * @param {*} item 
     * @returns {Node}
     */
    createItemNode(item) {}
    /**
     * 
     * @param {*} item 
     * @param {function(*,*):Promise} item_callback 
     * @returns {Promise<EditDialog>}
     */
    createEditView(item,item_callback) {}
    /**
     * 
     * @param {*} item 
     * @returns {Promise}
     */
    delete(item) {}
    /**
     * 
     * @param {*} original 
     * @param {*} modified 
     * @returns {Promise}
     */
    save(original,modified) {}

    initEditDelete(tmpl,v) {
        tmpl.get("delete-btn").onclick=()=>{
            showDialog(Promise.resolve(new ConfirmDialog(Localization.CURRENT.get("dialog.confirm_delete"),()=>{
                return this.delete(v).then(()=>{
                    this.reload();
                }).catch(e=>{
                    showMessage(e);
                });
            })));
        };
        tmpl.get("edit-btn").onclick=()=>{
            showDialog(this.createEditView(v,(original,modified)=>{
                return this.save(original,modified).then(()=>{
                    this.reload();
                })
            }));
        };
    }
}

class LocalListEditView extends ListEditView {
    selected_warehouse=null;
    initTemplate() {
        let t=this.view_template;
        t.get("list-filter").classList.remove("hidden");
        createSelectButton(t.get("list-warehouse"),()=>new WarehouseSelectView(false),s=>{
            this.selected_warehouse=s;
            this.reload();
        },null,"warehouse.null");
        super.initTemplate();
    }
}

class SelectView extends ListView {
    selected=null;
    selectedNode=null;
    
    getSelected() {
        return this.selected;
    }
    initTemplate() {
        this.view_template.getNode().classList.add("select-list");
        this.view_template.get("list-archived-outer").style.display="none";
        super.initTemplate();
    }
    createItemCard(v) {
        let item=new Template("select-item-template");
        item.get("content").appendChild(this.createItemNode(v));

        let n=item.getNode();
        n.onclick=()=>{
            if(this.selectedNode!==null)
                this.selectedNode.classList.remove("selected");
            
            this.selected=v;
            n.classList.add("selected");
            this.selectedNode=n;
        };

        return n;
    }
}
