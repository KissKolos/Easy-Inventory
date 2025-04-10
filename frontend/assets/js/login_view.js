"use strict";

class LoginView extends View {
    content_factory;
    localization_callback;
    loginname;
    content;
    constructor(content_factory,localization_callback) {
        super("login-template");
        this.content_factory=content_factory;
        this.localization_callback=localization_callback;
    }
    loadContent() {
        request("GET","/userinfo").then(r=>{
            let t=this.view_template;
            t.getNode().classList.remove("failed");

            this.loginname.textContent=r.username;
            this.content.appendChild(this.content_factory());

            t.getNode().classList.add("logged-in");
            t.getNode().classList.remove("loading");
        }).catch(e=>{
            console.log(e);
            let t=this.view_template;
            t.getNode().classList.add("failed");
            t.getNode().classList.remove("loading");
            set_token(null);
        });
    }
    initThemeButtons(t) {
        const theme=document.getElementById("theme");
        const dark1=t.get("dark-theme1");
        const dark2=t.get("dark-theme");
        const light1=t.get("light-theme1");
        const light2=t.get("light-theme");
        
        let lf=()=>{
            theme.setAttribute("href","assets/css/colors_dark.css");
            light1.style.display="none";
            light2.style.display="none";
            dark1.style.display=null;
            dark2.style.display=null;
        };

        let df=()=>{
            theme.setAttribute("href","assets/css/colors_light.css");
            dark1.style.display="none";
            dark2.style.display="none";
            light1.style.display=null;
            light2.style.display=null;
        };

        light1.onclick=lf;
        light2.onclick=lf;
        dark1.onclick=df;
        dark2.onclick=df;
        light1.style.display="none";
        light2.style.display="none";
    }
    initTemplate() {
        let t=this.view_template;
        let username=t.get("username");
        let password=t.get("password");
        this.content=t.get("login-content");
        this.loginname=t.get("login-name");

        this.initThemeButtons(t);

        t.get("login-button").onclick=()=>{
            let v=username.value;
            t.getNode().classList.add("loading");
            request("POST","/users/"+v+"/auth",{password:password.value}).then(r=>{
                set_token(r.token);
                this.loadContent();
            }).catch(()=>{
                let t=this.view_template;
                t.getNode().classList.add("failed");
                t.getNode().classList.remove("loading");
            });
        };
        t.get("logout-button").onclick=()=>{
            request("DELETE","/token",undefined,"logout.fail").catch(e=>console.log(e));
            set_token(null);
            t.getNode().classList.remove("logged-in");
            this.content.innerHTML="";
        };
        let l=t.get("localization");
        for(let code of Localization.LANG_CODES) {
            let opt=document.createElement("option");
            opt.value=code;
            opt.textContent=Localization.LOCALIZATIONS[code].get("localization.name");
            l.appendChild(opt);
        }
        l.value=Localization.CURRENT.code;
        l.onchange=()=>{
            this.localization_callback(l.value);
        };

        if(get_token()!==null){
            this.loadContent();
        }
        console.log(get_token());
    }
}
