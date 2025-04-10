/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.APIResponse;
import easyinventoryapi.HTTPConnector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

/**
 *
 * @author 3041TAN-08
 */
public class SimpleHTTPConnector implements HTTPConnector {

    @Override
    public APIResponse execute(String method, String url, String token, String body, boolean readResponseBody) throws IOException {
        APIResponse response;
        HttpURLConnection connection = null;
        System.out.println(method+" "+url);
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Connection","close");
            connection.setUseCaches(false);
            //connection.setReadTimeout(10000);
            //connection.setConnectTimeout(10000);
            
            if(token!=null)
                connection.setRequestProperty("Authorization", "Bearer "+token);
            
            if(body!=null){
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"))) {
                    writer.write(body);
                }
            }
            
            
            int code=connection.getResponseCode();
            System.out.println(code);
            
            String resp="";
            if(code==200&&readResponseBody){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                resp = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            System.out.println(resp);
            
            response=new APIResponse(code,resp);
        }catch (UncheckedIOException e) {
            throw new IOException(e);
        } catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }
    
}
