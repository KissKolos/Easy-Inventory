"use strict";

class Localization {
    static LANG_CODES=['en_US','hu_HU'];
    static CURRENT=null;
    static LOCALIZATIONS={};

    code;
    data;

    constructor(code,data) {
        this.code=code;
        this.data=data;
    }

    static loadAll() {
        let promises=[];
        for(let code of this.LANG_CODES) {
            promises.push(fetch('assets/lang/'+code+'.json').then(r=>{
                return r.json();
            }).then(r=>{
                Localization.LOCALIZATIONS[code]=new Localization(code,r);
            }));
        }
        return Promise.all(promises);
    }
    get(code) {
	    code=code.trim();
        if(this.data[code]===undefined)
            console.log("lang code missing: "+code);
        return this.data[code]===undefined?code:this.data[code];
    }
    localize(node) {
        for(let n of node.getElementsByClassName("localize")) {
            n.textContent=this.get(n.textContent);
            if(n.placeholder!==undefined)
                n.placeholder=this.get(n.placeholder);
        }
    }
}
