"use strict";

class StatsView extends View {
	source;
	minxl;
	maxxl;
	minyl;
	maxyl;
	inner;
	constructor(source) {
		super("stats-template");
		this.source=source;
	}
	showStats(s,from,to) {
		this.inner.innerHTML="";
		
        let date=new Date();
        date.setTime(from);        
        
		this.minxl.textContent=date.toDateString();

        date.setTime(to);
		this.maxxl.textContent=date.toDateString();

		let maxv=0;
		let has_negative=false;
		console.log(s.data);

		for(let v of s.data) {
			has_negative|=v<0;
			maxv=Math.max(maxv,Math.abs(v));
		}

		if(has_negative)
			this.minyl.textContent=-maxv;
		else
			this.minyl.textContent="0";
		
		this.maxyl.textContent=maxv;

		let w=1.0/(s.data.length-1);

		let d="M 100 "+(has_negative?50:100);
		let d2="";
		if(maxv>0)
			for(let i in s.data) {
				let v=s.data[i];

				d+=" L "+(100-i*w*100)+" "+((1-v/maxv)*(has_negative?50:100));
				if(d2==="")
					d2="M ";
				else
					d2+=" L ";
				d2+=(100-i*w*100)+" "+((1-v/maxv)*(has_negative?50:100));
			}
		d+=" L 0 "+(has_negative?50:100);
		d+=" z";
		let path=document.createElementNS('http://www.w3.org/2000/svg',"path");
		path.setAttribute("d",d);
		path.setAttribute("fill","url(#Gradient)");
		this.inner.appendChild(path);

		path=document.createElementNS('http://www.w3.org/2000/svg',"path");
		path.setAttribute("d",d2);
		path.setAttribute("fill","none");
		path.setAttribute("stroke","blue");
		path.setAttribute("stroke-width","0.5");
		this.inner.appendChild(path);
	}
	initTemplate() {
		let t=this.view_template;

		this.minxl=t.get("min-x");
		this.maxxl=t.get("max-x");
		this.minyl=t.get("min-y");
		this.maxyl=t.get("max-y");
		this.inner=t.get("graph-content");

		let from_date=t.get("from");
		let to_date=t.get("to");

		let date=new Date();
		to_date.valueAsDate=date;
		date.setDate(date.getDate() - 1);
		from_date.valueAsDate=date;

		from_date.onchange=()=>{
			to_date.min=from_date.value;
		};
		to_date.onchange=()=>{
			from_date.max=to_date.value;
		};
		to_date.min=from_date.value;
		from_date.max=to_date.value;

		t.get("statistics-config").appendChild(this.source.getNode());
		t.get("statistics-update").onclick=()=>{
			let from=from_date.valueAsDate.getTime();
			let to=to_date.valueAsDate.getTime();

			this.source.getStats(from,to).then(s=>{
				this.showStats(s,from,to);
			}).catch(e=>{
				showMessage(e);
			});
		};
	}
}

class StatSource extends View {
	getStats(from,to) {}
}

class ItemStat extends StatSource {
	selected_item;
	option;
	warehouse_search;
    storage_search;
    operation_search;
	constructor() {
		super("stats-items-template")
	}
	initTemplate() {
		let t=this.view_template;

		this.option=t.get("option");
		this.warehouse_search=t.get("warehouse-search");
        this.storage_search=t.get("storage-search");
        this.operation_search=t.get("operation-search");

        createSelectButton(t.get("item"),()=>new ItemSelectView(),s=>this.selected_item=s==null?null:s.id,null,"item.null");
	}
	getStats(from,to) {
		return request("GET","/statistics/items?"+new URLSearchParams({
			from:from/1000,
			to:to/1000,
			item:this.selected_item,
			warehouse:this.warehouse_search.value,
			storage:this.storage_search.value,
			operation:this.operation_search.value
		}),undefined,"statistics.items.fail").then(d=>{
            switch(this.option.value) {
                case "start_amount":
                    return {data:d.start_amount};
                case "added":
                    return {data:d.add_amount};
                case "removed":
                    return {data:d.remove_amount};
                default:
                    for(let i in d.add_amount) {
                        d.add_amount[i]+=d.remove_amount[i];
                    }
                    return {data:d.add_amount};
            }
		});
	}
}

class OperationStat extends StatSource {
	warehouse_search;
    operation_search;
	constructor() {
		super("stats-operations-template")
	}
	initTemplate() {
		let t=this.view_template;

		this.warehouse_search=t.get("warehouse-search");
        this.operation_search=t.get("operation-search");
	}
	getStats(from,to) {
		return request("GET","/statistics/operations?"+new URLSearchParams({
			from:from/1000,
			to:to/1000,
			warehouse:this.warehouse_search.value,
			operation:this.operation_search.value
		}),undefined,"statistics.items.fail").then(d=>{
			return {data:d.times};
		});
	}
}
