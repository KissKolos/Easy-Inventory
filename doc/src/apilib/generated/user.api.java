public class API {
    public UserInfo getUserinfo() throws IOException,APIException,JSONException { ... }
    public User[] getUsers(String query,int offset,int len) throws IOException,APIException,JSONException { ... }
    public User getUser(String id) throws IOException,APIException,JSONException { ... }
    public boolean putUser(String id,String name,String password,String manager_id,boolean create,boolean update) throws IOException,APIException,JSONException { ... }
    public boolean deleteUser(String id) throws IOException,APIException { ... }
    public boolean moveUser(String id,String new_id) throws IOException,APIException,JSONException { ... }
    public void changePassword(String old_password,String new_password) throws IOException,APIException,JSONException { ... }
    ...
}
