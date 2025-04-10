"use strict";

class SearchView extends View {
    selected_warehouse=null;
    selected_storage=null;
    offset=0;
    prev;
    next;
    results;
    loading;
    constructor() {
        super("search-template");
    }
    initTemplate() {
        let t=this.view_template;
        this.results=t.get("results");
        let st_select=t.get("storage");
        this.loading=t.get("loading");

        this.prev=t.get("prev-btn");
        this.prev.onclick=()=>{
            this.offset-=PAGE_SIZE;
            if(this.offset<0)
                this.offset=0;
            this.doSearch();
        };
        this.prev.style.display="none";
        this.next=t.get("next-btn");
        this.next.onclick=()=>{
            this.offset+=PAGE_SIZE;
            this.doSearch();
        };
        this.next.style.display="none";

        st_select.style.display="none";
        createSelectButton(t.get("warehouse"),()=>new WarehouseSelectView(true),s=>{
            this.selected_warehouse=s==null?null:s.id;
            if(s!==null)
                createSelectButton(st_select,()=>new StorageSelectView(this.selected_warehouse),s=>{
                    this.selected_storage=s==null?null:s.id;
                },null,"storage.any");
            st_select.style.display=s==null?"none":"block";
        },null,"warehouse.any");

        t.get("search-btn").onclick=()=>this.doSearch();
    }
    doSearch() {
        this.results.innerHTML="";
        this.loading.classList.remove("hidden");

        let path="/search";

        if(this.selected_warehouse!==null) {
            path="/warehouses/"+this.selected_warehouse+"/search";
            if(this.selected_storage!==null)
                path="/warehouses/"+this.selected_warehouse+"/storages/"+this.selected_storage+"/search";
        }

        let t=this.view_template;
        let warehouse=t.get("search-warehouse").checked;
        let storage=t.get("search-storage").checked;
        let lot=t.get("search-lot").checked;
        let serial=t.get("search-serial").checked;

        if(warehouse)
            t.get("warehouse-header").classList.remove("hidden");
        else
            t.get("warehouse-header").classList.add("hidden");

        if(storage)
            t.get("storage-header").classList.remove("hidden");
        else
            t.get("storage-header").classList.add("hidden");

        if(lot)
            t.get("lot-header").classList.remove("hidden");
        else
            t.get("lot-header").classList.add("hidden");

        if(serial){
            t.get("gserial-header").classList.remove("hidden");
            t.get("mserial-header").classList.remove("hidden");
        }else{
            t.get("gserial-header").classList.add("hidden");
            t.get("mserial-header").classList.add("hidden");
        }

        this.prev.style.display=this.offset===0?"none":null;

        request("GET",path+"?"+new URLSearchParams({
            q:t.get("search").value,
            warehouse:warehouse,
            storage:storage,
            lot:lot,
            serial:serial,
            offset:this.offset,
            len:PAGE_SIZE
        }))
        .catch(()=>{})
        .then(res=>{
            this.next.style.display=res.length<PAGE_SIZE?"none":null;
            for(let stack of res) {
                let row=new Template("search-result-template");
                if(!warehouse)
                    row.get("warehouse-col").classList.add("hidden");
                if(!storage)
                    row.get("storage-col").classList.add("hidden");
                if(!lot)
                    row.get("lot-col").classList.add("hidden");
                if(!serial){
                    row.get("gserial-col").classList.add("hidden");
                    row.get("mserial-col").classList.add("hidden");
                }
                row.get("warehouse").textContent=stack.warehouse===null?null:stack.warehouse.name;
                row.get("storage").textContent=stack.storage===null?null:stack.storage.name;
                row.get("type").textContent=stack.item.name;
                row.get("amount").textContent=stack.amount+" "+stack.item.unit.name;
                row.get("available-amount").textContent=stack.available_amount+" "+stack.item.unit.name;
                row.get("lot").textContent=stack.lot;
                row.get("global-serial").textContent=stack.global_serial;
                row.get("manufacturer-serial").textContent=stack.manufacturer_serial;
                this.results.appendChild(row.getNode());
            }
            this.loading.classList.add("hidden");
        });
    }
}
