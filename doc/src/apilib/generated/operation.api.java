public class API {
    public Operation[] getOperations(String warehouse,String query,int offset,int len,boolean archived) throws IOException,APIException,JSONException { ... }
    public boolean putOperation(String warehouse,String id,String name,boolean is_add,OperationItem[] items) throws IOException,APIException,JSONException { ... }
    public boolean cancelOperation(String warehouse,String id) throws IOException,APIException { ... }
    public boolean finishOperation(String warehouse,String id) throws IOException,APIException { ... }
    ...
}
