"use strict";

function setup_ui() {
    let login=new LoginView(()=>{
        let tabbed=new TabbedView();
        tabbed.addTab(new ItemView(),"tab.items");
        tabbed.addTab(new UnitView(),"tab.units");
        tabbed.addTab(new UserView(),"tab.users");
        tabbed.addTab(new SearchView(),"tab.search");
        tabbed.addTab(new OperationView(),"tab.operations");
        tabbed.addTab(new WarehouseView(),"tab.warehouses");
        tabbed.addTab(new StorageView(),"tab.storages");
        tabbed.addTab(new ProfileView(),"tab.profile");

        let stats=new TabbedView();
        stats.addTab(new StatsView(new ItemStat()),"statistics.items");
        stats.addTab(new StatsView(new OperationStat()),"statistics.operations");

        tabbed.addTab(stats,"tab.statistics");
        return tabbed.getNode();
    },loc=>{
        Localization.CURRENT=Localization.LOCALIZATIONS[loc];
        setup_ui();
    });
    document.getElementById("content").innerHTML="";
    document.getElementById("content").appendChild(login.getNode());
}

function main() {
    Localization.loadAll().then(()=>{
        Localization.CURRENT=Localization.LOCALIZATIONS['en_US'];
        setup_ui();
    })
}


main();
