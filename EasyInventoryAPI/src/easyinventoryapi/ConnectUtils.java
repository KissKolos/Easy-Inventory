package easyinventoryapi;

import java.net.*;
import java.io.*;
import java.util.stream.*;

//https://github.com/stleary/JSON-java

public class ConnectUtils {
    /*public static APIResponse executeGet(String url,String token) throws IOException {
        APIResponse response;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("Connection","close");
            connection.setUseCaches(false);
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            
            int code=connection.getResponseCode();
            System.out.println("len: "+connection.getContentLength());
            System.out.println("enc: "+connection.getContentEncoding());
            System.out.println("type: "+connection.getContentType());
            String resp="";
            if(code==200){
                InputStream in=connection.getInputStream();
                InputStreamReader inp=new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(inp);
                resp = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            

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

    public static APIResponse executePUT(String url,String token,String body) throws IOException {
        APIResponse response;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", ""+ body.length());
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("Connection","close");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
        
            connection.setUseCaches(false);
            connection.setDoOutput(true);
        
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"))) {
                writer.write(body);
            }

            
            int code=connection.getResponseCode();
            System.out.println("len: "+connection.getContentLength());
            System.out.println("enc: "+connection.getContentEncoding());
            System.out.println("type: "+connection.getContentType());
            
            String resp="";
            if(code==200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                resp = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            response=new APIResponse(code,resp);
        } catch (UncheckedIOException e) {
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
    
    public static APIResponse executePOST(String url,String token,String body) throws IOException {
        APIResponse response;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", ""+ body.length());
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("Connection","close");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
        
            connection.setUseCaches(false);
            connection.setDoOutput(true);
        
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"))) {
                writer.write(body);
            }

            int code=connection.getResponseCode();
            
            String resp="";
            if(code==200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                resp = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            response=new APIResponse(code,resp);
        }catch (UncheckedIOException e) {
            throw new IOException(e);
        }  catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }
    
    public static APIResponse executeDELETE(String url,String token) throws IOException {
        APIResponse response;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("Connection","close");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
        
            connection.setUseCaches(false);
            connection.setDoOutput(false);

            response= new APIResponse(connection.getResponseCode(),null);
        } catch (UncheckedIOException e) {
            throw new IOException(e);
        }catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }
    
    public static APIResponse executeDELETEWithBody(String url,String token,String body) throws IOException {
        APIResponse response;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("Content-Length", ""+ body.length());
            connection.setRequestProperty("Connection","close");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
        
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"))) {
                writer.write(body);
            }

            response= new APIResponse(connection.getResponseCode(),null);
        }catch (UncheckedIOException e) {
            throw new IOException(e);
        }  catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }*/
}
