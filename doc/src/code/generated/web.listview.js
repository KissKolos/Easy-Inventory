class ListView extends View {
    offset=0;
    content;
    spinner;
    query;
    prev;
    next;
    archived;
    reloading=false;
    constructor() { ... }
    initTemplate() { ... }
    reload() { ... }
    load(q,archived,offset,length) {}
    createItemCard(v) {}
}
