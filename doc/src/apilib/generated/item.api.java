public class API {
    public Item[] getItems(String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException { ... }
    public Item getItem(String id) throws IOException,APIException,JSONException { ... }
    public boolean putItem(String id,String name,String unit_id,boolean create,boolean update) throws IOException,APIException,JSONException { ... }
    public boolean deleteItem(String id) throws IOException,APIException { ... }
    public boolean moveItem(String id,String new_id) throws IOException,APIException,JSONException { ... }
    ...
}
