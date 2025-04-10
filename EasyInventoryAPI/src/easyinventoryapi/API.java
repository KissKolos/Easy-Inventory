package easyinventoryapi;

import java.io.IOException;
import java.net.URLEncoder;
import org.json.*;

//https://github.com/stleary/JSON-java

public class API {

    protected final HTTPConnector connector;
    protected final String url;
    protected final String token;
    protected final Thread renew_thread;

    public static API connect(HTTPConnector connector,String url,String username,String password) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("POST",url+"/users/"+encode(username)+"/auth",null,new JSONObject()
            .put("password",password)
            .toString(),true);
        if(resp.code!=200)
            throw new APIException(resp.code);
        JSONObject r=new JSONObject(resp.body);
        String token=r.getString("token");

        return new API(connector,url,token);
    }

    public API(HTTPConnector connector,String url,String token) {
        this.connector=connector;
        this.url=url;
        this.token=token;
        this.renew_thread=new Thread(()->{
            try{
                while(true) {
                    try{this.renewToken();}catch(IOException|APIException e){}
                    Thread.sleep(60000);
                }
            }catch(InterruptedException e) {}
        });
        this.renew_thread.start();
    }

    private static String encode(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
    
    private JSONObject getObject(String path,int code) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("GET",url+path,token,null,true);
        if(resp.code!=code)
            throw new APIException(resp.code);
        if("[]".equals(resp.body))
            return new JSONObject();
        return new JSONObject(resp.body);
    }
    
    private JSONObject optObject(String path,int code) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("GET",url+path,token,null,true);
        if(resp.code==404)
            return null;
        if(resp.code!=code)
            throw new APIException(resp.code);
        if("[]".equals(resp.body))
            return new JSONObject();
        return new JSONObject(resp.body);
    }
    
    private boolean deleteObject(String path) throws IOException,APIException {
        APIResponse resp=connector.execute("DELETE",url+path, token,null,false);
        if(resp.code!=204&&resp.code!=404)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    private boolean deleteObjectWithBody(String path,String body) throws IOException,APIException {
        APIResponse resp=connector.execute("DELETE",url+path, token,body,false);
        if(resp.code!=204&&resp.code!=404)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    private JSONArray getArray(String path,int code) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("GET",url+path,token,null,true);
        if(resp.code!=code)
            throw new APIException(resp.code);
        return new JSONArray(resp.body);
    }

    private JSONArray getPaginated(String path,String query,int offset,int len) throws IOException,APIException,JSONException {
        String q="";
        if(query!=null)
            q="q="+encode(query)+"&";
        return getArray(path+"?"+q+"offset="+offset+"&len="+len,200);
    }

    private JSONArray getPaginated(String path,String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException {
        String q="";
        if(query!=null)
            q="q="+encode(query)+"&";
        return getArray(path+"?"+q+"offset="+offset+"&len="+len+"&archived="+archived,200);
    }
    
    private boolean moveObject(String path,String id,String new_id) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("POST",url+path, token, new JSONObject()
            .put("from",id)
            .put("to",new_id)
            .toString(),false);
        if(resp.code!=204&&resp.code!=409)
            throw new APIException(resp.code);
        return resp.code==204;
    }

    public UserInfo getUserinfo() throws IOException,APIException,JSONException {
        return new UserInfo(getObject("/userinfo",200));
    }
    
    public User[] getUsers(String query,int offset,int len) throws IOException,APIException,JSONException {
        JSONArray r=getPaginated("/users",query,offset,len);
        User[] users=new User[r.length()];
        for(int i=0;i<r.length();i++)
            users[i]=new User(r.getJSONObject(i));
        return users;
    }
    
    public User getUser(String id) throws IOException,APIException,JSONException {
        JSONObject r=optObject("/users/"+encode(id),200);
        if(r==null)
            return null;
        else
            return new User(r);
    }

    public boolean putUser(String id,String name,String password,String manager_id,boolean create,boolean update) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("PUT",url+"/users/"+encode(id)+"?create="+create+"&update="+update, token, new JSONObject()
            .put("name",name)
            .putOpt("password", password==null||password.isEmpty()?null:password)
            .put("manager",manager_id)
            .toString(),false);
        if(resp.code!=201&&resp.code!=204)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean deleteUser(String id) throws IOException,APIException {
        return this.deleteObject("/users/"+encode(id));
    }
    
    public boolean moveUser(String id,String new_id) throws IOException,APIException,JSONException {
        return moveObject("/users",id,new_id);
    }
    
    public void logout() throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("DELETE",url+"/token", token,null,false);
        if(resp.code!=204)
            throw new APIException(resp.code);
        this.renew_thread.interrupt();
    }
    
    public void renewToken() throws IOException,APIException {
        APIResponse resp=connector.execute("POST",url+"/token", token,null,false);
        if(resp.code!=200)
            throw new APIException(resp.code);
    }
    
    public void changePassword(String old_password,String new_password) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("PUT",url+"/userinfo", token, new JSONObject()
            .put("old_password",old_password)
            .put("new_password",new_password)
            .toString(),false);
        if(resp.code!=204)
            throw new APIException(resp.code);
    }
    
    public SystemAuthorization getSystemAuthorization(String user) throws IOException,APIException,JSONException {
        return new SystemAuthorization(getArray("/users/"+encode(user)+"/authorizations/system",200));
    }
    
    public LocalAuthorization getLocalAuthorization(String user,String warehouse) throws IOException,APIException,JSONException {
        return new LocalAuthorization(getArray("/users/"+encode(user)+"/authorizations/local/"+encode(warehouse),200));
    }
    
    public boolean grantSystemAuthorization(String user,String authorization) throws IOException,APIException {
        APIResponse resp=connector.execute("PUT",url+"/users/"+encode(user)+"/authorizations/system/"+encode(authorization), token,"",false);
        if(resp.code!=204&&resp.code!=404)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean revokeSystemAuthorization(String user,String authorization) throws IOException,APIException {
        APIResponse resp=connector.execute("DELETE",url+"/users/"+encode(user)+"/authorizations/system/"+encode(authorization), token,null,false);
        if(resp.code!=204&&resp.code!=404)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean grantLocalAuthorization(String user,String warehouse,String authorization) throws IOException,APIException {
        APIResponse resp=connector.execute("PUT",url+"/users/"+encode(user)+"/authorizations/local/"+encode(warehouse)+"/"+encode(authorization), token,"",false);
        if(resp.code!=204&&resp.code!=404)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean revokeLocalAuthorization(String user,String warehouse,String authorization) throws IOException,APIException {
        APIResponse resp=connector.execute("DELETE",url+"/users/"+encode(user)+"/authorizations/local/"+encode(warehouse)+"/"+encode(authorization), token,null,false);
        if(resp.code!=204&&resp.code!=404)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public Warehouse[] getWarehouses(String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException {
        JSONArray r=getPaginated("/warehouses",query,offset,len,archived);

        Warehouse[] warehouses=new Warehouse[r.length()];
        for(int i=0;i<r.length();i++)
            warehouses[i]=new Warehouse(r.getJSONObject(i));
        return warehouses;
    }
    
    public Warehouse getWarehouse(String id) throws IOException,APIException,JSONException {
       JSONObject r=optObject("/warehouses/"+encode(id),200);
        if(r==null)
            return null;
        else
            return new Warehouse(r);
    }
    
    public boolean putWarehouse(String id,String name,String address,boolean create,boolean update)
            throws IOException,APIException,JSONException {
        JSONObject body=new JSONObject().put("name", name);
        
        if(address!=null) {
            body.put("address", address);
        }
        
        APIResponse resp=connector.execute("PUT",url+"/warehouses/"+encode(id)+"?create="+create+"&update="+update, token, body.toString(),false);
        if(resp.code!=201&&resp.code!=204)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean deleteWarehouse(String id) throws IOException,APIException {
        return this.deleteObject("/warehouses/"+encode(id));
    }
    
    public boolean moveWarehouse(String id,String new_id) throws IOException,APIException,JSONException {
        return moveObject("/warehouses",id,new_id);
    }
    
    
    public Item[] getItems(String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException {
       JSONArray r=getPaginated("/items",query,offset,len,archived);

        Item[] items=new Item[r.length()];
        for(int i=0;i<r.length();i++)
            items[i]=new Item(r.getJSONObject(i));
        return items;
    }
    
    public Item getItem(String id) throws IOException,APIException,JSONException {
       JSONObject r=optObject("/items/"+encode(id),200);
        if(r==null)
            return null;
        else
            return new Item(r);
    }
    
    public boolean putItem(String id,String name,String unit_id,boolean create,boolean update) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("PUT",url+"/items/"+encode(id)+"?create="+create+"&update="+update, token, new JSONObject()
            .put("name",name)
            .put("unit",unit_id)
            .toString(),false);
        if(resp.code!=201&&resp.code!=204)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean deleteItem(String id) throws IOException,APIException {
        return this.deleteObject("/items/"+encode(id));
    }
    
    public boolean moveItem(String id,String new_id) throws IOException,APIException,JSONException {
        return moveObject("/items",id,new_id);
    }
    
    public Unit[] getUnits(String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException {
       JSONArray r=getPaginated("/units",query,offset,len,archived);

        Unit[] units=new Unit[r.length()];
        for(int i=0;i<r.length();i++)
            units[i]=new Unit(r.getJSONObject(i));
        return units;
    }
    
    public Unit getUnit(String id) throws IOException,APIException,JSONException {
        JSONObject r=optObject("/units/"+encode(id),200);
        if(r==null)
            return null;
        else
            return new Unit(r);
    }
    
    public boolean putUnit(String id,String name,boolean create,boolean update) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("PUT",url+"/units/"+encode(id)+"?create="+create+"&update="+update, token, new JSONObject()
            .put("name",name)
            .toString(),false);
        if(resp.code!=201&&resp.code!=204)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean deleteUnit(String id) throws IOException,APIException {
        return deleteObject("/units/"+encode(id));
    }
    
    public boolean moveUnit(String id,String new_id) throws IOException,APIException,JSONException {
        return moveObject("/units",id,new_id);
    }
    
    
    public Storage[] getStorages(String warehouse,String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException {
        JSONArray r=getPaginated("/warehouses/"+encode(warehouse)+"/storages",query,offset,len,archived);

        Storage[] storages=new Storage[r.length()];
        for(int i=0;i<r.length();i++)
            storages[i]=new Storage(r.getJSONObject(i));
        return storages;
    }
    
    public Storage getStorage(String warehouse,String id) throws IOException,APIException,JSONException {
        JSONObject r=optObject("/warehouses/"+encode(warehouse)+"/storages/"+id,200);
        if(r==null)
            return null;
        else
            return new Storage(r);
    }
    
    public boolean putStorage(String warehouse,String id,String name,boolean create,boolean update) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("PUT",url+"/warehouses/"+encode(warehouse)+"/storages/"+encode(id)+"?create="+create+"&update="+update, token, new JSONObject()
            .put("name",name)
            .toString(),false);
        if(resp.code!=201&&resp.code!=204)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean deleteStorage(String warehouse,String id) throws IOException,APIException {
        return deleteObject("/warehouses/"+encode(warehouse)+"/storages/"+encode(id));
    }
    
    public boolean moveStorage(String warehouse,String id,String new_id) throws IOException,APIException,JSONException {
        return moveObject("/warehouses/"+encode(warehouse)+"/storages",id,new_id);
    }
    
    public StorageLimit[] getStorageLimits(String warehouse,String id,String query,int offset,int len) throws IOException,APIException,JSONException {
        JSONArray a=getPaginated("/warehouses/"+encode(warehouse)+"/storages/"+encode(id)+"/limits",query,offset,len);
        StorageLimit[] ret=new StorageLimit[a.length()];
        for(int i=0;i<ret.length;i++)
            ret[i]=new StorageLimit(a.getJSONObject(i));
        return ret;
    }
    
    public boolean setStorageLimit(String warehouse,String id,String item,int amount) throws IOException,APIException,JSONException {
        APIResponse resp=connector.execute("PUT",url+"/warehouses/"+encode(warehouse)+"/storages/"+encode(id)+"/limits/"+encode(item), token, new JSONObject()
            .put("amount",amount)
            .toString(),false);
        if(resp.code!=204&&resp.code!=404)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public StorageCapacity[] getStorageCapacity(String warehouse,String id,String query,int offset,int len) throws IOException,APIException,JSONException {
        JSONArray a=getPaginated("/warehouses/"+encode(warehouse)+"/storages/"+encode(id)+"/capacity",query,offset,len);
        StorageCapacity[] ret=new StorageCapacity[a.length()];
        for(int i=0;i<ret.length;i++)
            ret[i]=new StorageCapacity(a.getJSONObject(i));
        return ret;
    }
    
    public Operation[] getOperations(String warehouse,String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException {
        JSONArray r=getPaginated("/warehouses/"+encode(warehouse)+"/operations",query,offset,len,archived);

        Operation[] operations=new Operation[r.length()];
        for(int i=0;i<r.length();i++)
            operations[i]=new Operation(r.getJSONObject(i));
        return operations;
    }
    
    public boolean putOperation(String warehouse,String id,String name,boolean is_add,OperationItem[] items) throws IOException,APIException,JSONException {
        JSONArray it=new JSONArray();
        for(int i=0;i<items.length;i++) {
            JSONObject o=new JSONObject()
                .put("type", items[i].item.id)
                .put("amount", items[i].amount)
                .putOpt("lot", items[i].lot)
                .putOpt("manufacturer_serial", items[i].manufacturer_serial)
                .putOpt("storage", items[i].storage==null?null:items[i].storage.id);
            
            if(items[i].global_serial!=0)
                o.put("global_serial", items[i].global_serial);
            
            it.put(i,o);
        }
        
        APIResponse resp=connector.execute("PUT",url+"/warehouses/"+encode(warehouse)+"/operations/"+encode(id), token, new JSONObject()
            .put("name",name)
            .put("is_add",is_add)
            .put("items", it)
            .toString(),false);
        if(resp.code!=201&&resp.code!=204)
            throw new APIException(resp.code);
        return resp.code==204;
    }
    
    public boolean cancelOperation(String warehouse,String id) throws IOException,APIException {
        return deleteObjectWithBody("/warehouses/"+encode(warehouse)+"/operations/"+encode(id),"{\"cancel\":true}");
    }
    
    public boolean finishOperation(String warehouse,String id) throws IOException,APIException {
        return deleteObjectWithBody("/warehouses/"+encode(warehouse)+"/operations/"+encode(id),"{\"cancel\":false}");
    }
    
    public boolean moveOperation(String warehouse,String id,String new_id) throws IOException,APIException,JSONException {
        return moveObject("/warehouses/"+encode(warehouse)+"/operations",id,new_id);
    }
    
    public ItemStack[] getCurrentItems(String warehouse,String storage,String query,
            boolean qwarehouse,boolean qstorage,boolean lot,boolean serial,int offset,int len) throws IOException,APIException,JSONException {
        
        String path="/search";
        if(warehouse!=null) {
            path="/warehouses/"+encode(warehouse)+"/search";
            if(storage!=null) {
                path="/warehouses/"+encode(warehouse)+"/storages/"+encode(storage)+"/search";
            }
        }
        
        JSONArray r=getArray(path+"?q="+
                URLEncoder.encode(query, "UTF-8")+
                (qwarehouse?"&warehouse=true":"")+
                (qstorage?"&storage=true":"")+
                (lot?"&lot=true":"")+
                (serial?"&serial=true":"")+
                "&offset="+offset+
                "&len="+len
                ,200);
        
        ItemStack[] res=new ItemStack[r.length()];
        for(int i=0;i<res.length;i++)
            res[i]=new ItemStack(r.getJSONObject(i));
        return res;
    }
    
    public void putDB(String save,String test_token) throws IOException,APIException {
        putDB(connector,url,save,test_token);
    }
    
    public static void putDB(HTTPConnector connector,String url,String save,String test_token) throws IOException,APIException {
        APIResponse resp=connector.execute("PUT",url+"/db", test_token, save,false);
        if(resp.code!=204)
            throw new APIException(resp.code);
    }
}
