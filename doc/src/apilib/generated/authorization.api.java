public class API {
    public SystemAuthorization getSystemAuthorization(String user) throws IOException,APIException,JSONException { ... }
    public LocalAuthorization getLocalAuthorization(String user,String warehouse) throws IOException,APIException,JSONException { ... }
    public boolean grantSystemAuthorization(String user,String authorization) throws IOException,APIException { ... }
    public boolean revokeSystemAuthorization(String user,String authorization) throws IOException,APIException { ... }
    public boolean grantLocalAuthorization(String user,String warehouse,String authorization) throws IOException,APIException { ... }
    public boolean revokeLocalAuthorization(String user,String warehouse,String authorization) throws IOException,APIException { ... }
    ...
}
