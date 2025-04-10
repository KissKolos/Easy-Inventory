public class API {
    public Storage getStorage(String warehouse,String id) throws IOException,APIException,JSONException { ... }
    public boolean putStorage(String warehouse,String id,String name,boolean create,boolean update) throws IOException,APIException,JSONException { ... }
    public boolean deleteStorage(String warehouse,String id) throws IOException,APIException { ... }
    public boolean moveStorage(String warehouse,String id,String new_id) throws IOException,APIException,JSONException { ... }
    public StorageLimit[] getStorageLimits(String warehouse,String id,String query,int offset,int len) throws IOException,APIException,JSONException { ... }
    public boolean setStorageLimit(String warehouse,String id,String item,int amount) throws IOException,APIException,JSONException { ... }
    public StorageCapacity[] getStorageCapacity(String warehouse,String id,String query,int offset,int len) throws IOException,APIException,JSONException { ... }
    ...
}
