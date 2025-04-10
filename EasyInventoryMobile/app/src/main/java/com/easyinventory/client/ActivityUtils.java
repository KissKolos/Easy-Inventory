package com.easyinventory.client;

import android.content.Intent;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityUtils {

    public static <T> T readContext(Intent i, JSONFunc<T> func) {
        String data=i.getStringExtra("value");
        if(data!=null)
            try{
                return func.apply(new JSONObject(data));
            }catch(JSONException ignored){}
        return null;
    }

    public static ActivityResultLauncher<Intent> createLauncher(ActivityResultCaller a, JSONConsumer func, boolean nullable) {
        return a.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r->{
            if(r.getResultCode()== MainActivity.RESULT_OK&&r.getData()!=null) {
                String data=r.getData().getStringExtra("result");

                try{
                    if(data!=null)
                        func.accept(new JSONObject(data));
                    else if(nullable)
                        func.accept(null);
                }catch(JSONException ignored){}
            }
        });
    }

    public interface JSONFunc<T> {
        /** @noinspection unused*/
        T apply(JSONObject o) throws JSONException;
    }

    public interface JSONConsumer {
        void accept(JSONObject o) throws JSONException;
    }

}
