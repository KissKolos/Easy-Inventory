package easyinventoryapi;


public class APIResponse {
    public final int code;
    public final String body;
    
    public APIResponse(int code,String body) {
        this.code=code;
        this.body=body;
    }
}