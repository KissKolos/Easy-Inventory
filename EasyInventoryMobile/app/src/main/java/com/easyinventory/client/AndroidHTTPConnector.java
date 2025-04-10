package com.easyinventory.client;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import easyinventoryapi.APIResponse;
import easyinventoryapi.HTTPConnector;
import okhttp3.*;

public class AndroidHTTPConnector implements HTTPConnector {

    private final OkHttpClient client;

    public AndroidHTTPConnector(boolean hasTimeout) {
        if(hasTimeout)
            client=new OkHttpClient.Builder()
                    .pingInterval(3, TimeUnit.SECONDS)
                    .connectTimeout(5,TimeUnit.SECONDS)
                    .minWebSocketMessageToCompress(Long.MAX_VALUE)
                    .cache(null)
                    .build();
        else
            client=new OkHttpClient.Builder()
                    .pingInterval(0, TimeUnit.SECONDS)
                    .connectTimeout(0,TimeUnit.SECONDS)
                    .readTimeout(0,TimeUnit.SECONDS)
                    .writeTimeout(0,TimeUnit.SECONDS)
                    .cache(null)
                    .build();
    }

    public AndroidHTTPConnector() {
        this(true);
    }

    @Override
    public APIResponse execute(String method, String url, @Nullable String token, @Nullable String body, boolean readResponseBody) throws IOException {
        RequestBody r_body=null;
        if(body!=null)
            r_body=RequestBody.create(body, MediaType.get("application/json"));

        Request.Builder request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .header("Accept-Encoding", "identity")
                    .method(method, r_body);
        }catch(IllegalArgumentException e) {
            throw new IOException(e);
        }

        if(token!=null)
            request=request.header("Authorization","Bearer "+token);

        try (Response response = client.newCall(request.build()).execute()) {
            if (readResponseBody) {
                ResponseBody re_body = response.body();
                return new APIResponse(response.code(), re_body == null ? "" : re_body.string());
            } else
                return new APIResponse(response.code(), "");
        }
    }
}
