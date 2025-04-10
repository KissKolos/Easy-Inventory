public class API {
    public Warehouse[] getWarehouses(String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException { ... }
    public Warehouse getWarehouse(String id) throws IOException,APIException,JSONException { ... }
    public boolean putWarehouse(String id,String name,String address,boolean create,boolean update)
    public boolean deleteWarehouse(String id) throws IOException,APIException { ... }
    public boolean moveWarehouse(String id,String new_id) throws IOException,APIException,JSONException { ... }
    ...
}
