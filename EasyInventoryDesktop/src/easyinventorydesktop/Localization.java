/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.APIException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 3041TAN-06
 */
public class Localization {
    
    public static Localization CURRENT;
    private final JSONObject codes;
    public static final String[] LANG_CODES=new String[]{"hu_HU","en_US"};
    
    public Localization(String name) throws IOException,JSONException {
        InputStream res=getClass().getClassLoader().getResourceAsStream("resources/lang/"+name+".json");
        String resp;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(res, "UTF-8"))) {
            resp = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        codes=new JSONObject(resp);
    }
    
    public String getLocalized(String code) {
        if(!codes.has(code))
            System.out.println("missing: "+code);
        return codes.optString(code, code);
    }
    
    public <T> T formatException(String failcode,UIUtils.APITask<T> t) throws FormattedException {
        try{
            return t.run();
        }catch (IOException|JSONException ex) {
            ex.printStackTrace();
            throw new FormattedException(getLocalized(failcode));
        } catch (APIException ex) {
            ex.printStackTrace();
            throw new FormattedException(codes.optString(failcode+"."+ex.code,codes.getString(failcode)));
        }
    }
    
    /*public String getAPIIOExceptionMessage(IOException e) {
        return e.getMessage();
    }
    
    public String getAPIExceptionMessage(APIException e) {
        return e.getMessage();
    }
    
    public String getJSONExceptionMessage(JSONException e) {
        return e.getMessage();
    }*/

    @Override
    public String toString() {
        return this.getLocalized("localization.name");
    }
    
}
