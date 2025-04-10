public class API {
    public Unit[] getUnits(String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException { ... }
    public Unit getUnit(String id) throws IOException,APIException,JSONException { ... }
    public boolean putUnit(String id,String name,boolean create,boolean update) throws IOException,APIException,JSONException { ... }
    public boolean deleteUnit(String id) throws IOException,APIException { ... }
    public boolean moveUnit(String id,String new_id) throws IOException,APIException,JSONException { ... }
    ...
}
