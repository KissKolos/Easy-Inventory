public interface HTTPConnector {
    APIResponse execute(String method,String url,String token,String body,boolean readResponseBody) throws IOException;
}
