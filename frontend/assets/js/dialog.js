"use strict";

class DialogView extends View {
    ok_button=null;
    constructor(template_id) {
        super(template_id);
    }
    /**
     * @abstract
     * @returns {Promise}
     */
    ok() {}
    hasOk() {
        return false;
    }
}

/**
 * 
 * @param {Promise<DialogView>} view_promise 
 */
function showDialog(view_promise) {
    let t=new Template("popup-template");
    t.get("cancel").onclick=()=>{
        t.getNode().remove();
    };

    view_promise.then(view=>{
        t.getNode().classList.remove("loading");
        t.get("content").appendChild(view.getNode());
        
        if(view.hasOk()){
            view.ok_button=t.get("ok");
            t.get("ok").classList.remove("hidden");
            t.get("ok").onclick=()=>{
                if(!t.getNode().classList.contains("loading")) {
                    t.getNode().classList.add("loading");
                    view.ok().then(()=>{
                        t.getNode().remove();
                    }).catch(e=>{
                        showMessage(e);
                        t.getNode().classList.remove("loading");
		            });
                }
            };
        }
    }).catch(e=>{
        t.getNode().remove();
	    showMessage(e);
    });
    
    document.getElementById("popup-container").appendChild(t.getNode());
}

function showMessage(message) {
    console.log(message);
	showDialog(Promise.resolve(new MessageDialog(message)));
}

class EditDialog extends DialogView {
    item_callback;
    item;
    constructor(template_id,item_callback,item) {
        super(template_id);
        this.item_callback=item_callback;
        this.item=item;
    }
    validate() {
        this.ok_button.enabled=this.getItem()!==null;
    }
    /**
     * @abstract
     * @returns {*}
     */
    getItem() {}
    ok() {
        let modified;
        try{
            modified=this.getItem();
        }catch(e){
            return Promise.reject(e);
        }
        if(modified!==null)
            return this.item_callback(this.item,modified);
        return Promise.resolve();
    }
    hasOk() {
        return true;
    }
}

class SelectDialog extends DialogView {
    item_callback;
    select_view;
    constructor(select_view,item_callback) {
        super("empty-template");
        this.item_callback=item_callback;
        this.select_view=select_view;
    }
    initTemplate() {
        let t=this.view_template;
        t.getNode().appendChild(this.select_view.getNode());
    }
    ok() {
        return this.item_callback(this.select_view.getSelected());
    }
    hasOk() {
        return true;
    }
}

class MessageDialog extends DialogView {
    message;
    constructor(message) {
        super("message-popup");
        this.message=message;
    }
    initTemplate() {
        let t=this.view_template;
        t.get("message").textContent=this.message;
    }
}

class ConfirmDialog extends MessageDialog {
    callback;
    constructor(message,callback) {
        super(message);
        this.callback=callback;
    }
    ok() {
        return this.callback();
    }
    hasOk() {
        return true;
    }
}