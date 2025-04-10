"use strict";

class TabbedView extends View {
    tabheader;
    tabcontent;
    selected=null;
    selectedHeader=null;
    tabs=[];
    constructor() {
        super("tabview-template");
    }
    initTemplate() {
        this.tabheader=this.view_template.get("tab-header");
        this.tabcontent=this.view_template.get("tab-content");

        let first=true;
        for(let tab of this.tabs) {
            let node=tab[0].getNode();
            let title_code=tab[1];

            this.tabcontent.appendChild(node);
    
            let t=new Template("tabview-header-template");
            t.get("text").textContent=Localization.CURRENT.get(title_code);
            t.get("text").onclick=()=>{
                this.selected.classList.add("hidden");
                this.selectedHeader.classList.remove("selected");
                node.classList.remove("hidden");
                this.selected=node;
                this.selectedHeader=t.getNode();
                this.selectedHeader.classList.add("selected");
            };
            this.tabheader.appendChild(t.getNode());

            if(first) {
                this.selected=node;
                this.selectedHeader=t.getNode();
                this.selectedHeader.classList.add("selected");
                first=false;
            }else{
                node.classList.add("hidden");
            }
        }
    }
    addTab(node,title_code) {
        this.tabs.push([node,title_code]);
    }
}