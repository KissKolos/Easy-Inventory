package com.easyinventory.client.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.easyinventory.client.R;
import com.easyinventory.client.Worker;

import org.json.JSONException;

import java.io.IOException;
import java.util.function.Consumer;

import easyinventoryapi.APIException;

@SuppressLint("InflateParams")
public class UIUtils {

    @SuppressLint("ClickableViewAccessibility")
    public static <T> void waitForTask(Activity a, @NonNull View v, FormattedTask<T> task, Consumer<T> success, Runnable fail) {
        LayoutInflater inf=(LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup=inf.inflate(R.layout.loading_popup,null);

        PopupWindow w=new PopupWindow(popup, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
        v.post(()->{
            w.showAtLocation(v, Gravity.CENTER,0,0);
            w.setTouchInterceptor((_v,_e)->true); //suppress clicks
        });

        Worker.GLOBAL.addTask(()->{
            try{
                T res=task.run();
                v.post(()-> {
                    w.dismiss();
                    success.accept(res);
                });
            }catch(FormattedException e) {
                v.post(()->{
                    w.dismiss();
                    showError(a.getWindow().getDecorView(), e.getMessage());
                    e.printStackTrace();
                    fail.run();
                });
            }
        });
    }

    public static <T> void waitForTaskBackground(Activity a,FormattedTask<T> task, Consumer<T> success,Runnable fail) {
        Worker.GLOBAL.addTask(()->{
            try{
                T res=task.run();
                a.runOnUiThread(()-> {
                    success.accept(res);
                });
            }catch(FormattedException e) {
                a.runOnUiThread(()->{
                    showError(a.getWindow().getDecorView(), e.getMessage());
                    e.printStackTrace();
                    fail.run();
                });
            }
        });
    }

    public static void setBackground(Activity activity,View v,int attr) {
        TypedValue val=new TypedValue();
        activity.getTheme().resolveAttribute(attr,val,true);
        v.setBackgroundResource(val.resourceId);
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void showError(View v, String msg) {
        LayoutInflater inf=(LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup=inf.inflate(R.layout.error_popup,null);

        PopupWindow w=new PopupWindow(popup, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,true);

        ((TextView)popup.findViewById(R.id.errorMessage)).setText(msg, TextView.BufferType.NORMAL);

        v.post(()->{
            w.showAtLocation(v, Gravity.CENTER,0,0);
            w.setTouchInterceptor((_v,_e)->{
                w.dismiss();
                return true;
            });
        });
    }

    public static void showConfirmDialog(@NonNull View v, int prompt, int accept, int cancel, Runnable accept_callback, Runnable cancel_callback) {
        LayoutInflater inf=(LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup=inf.inflate(R.layout.confirm_popup,null);

        PopupWindow w=new PopupWindow(popup, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,true);

        ((TextView)popup.findViewById(R.id.prompt)).setText(prompt, TextView.BufferType.NORMAL);
        ((TextView)popup.findViewById(R.id.accept_button)).setText(accept, TextView.BufferType.NORMAL);
        ((TextView)popup.findViewById(R.id.cancel_button)).setText(cancel, TextView.BufferType.NORMAL);
        popup.findViewById(R.id.accept_button).setOnClickListener(_e->{
            accept_callback.run();
            w.dismiss();
        });
        popup.findViewById(R.id.cancel_button).setOnClickListener(_e->{
            cancel_callback.run();
            w.dismiss();
        });

        v.post(()->{
            w.showAtLocation(v, Gravity.CENTER,0,0);
        });
    }

    public static String loadString(Resources r, String code) {
        try {
            return r.getString(R.string.class.getDeclaredField(code).getInt(null));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public static <T> T formatException(APITask<T> t,Resources r,String failcode) throws FormattedException {
        try{
            return t.run();
        }catch(APIException e) {
            String msg=loadString(r,failcode+"_"+e.code);
            if(msg==null)
                msg=loadString(r,failcode);
            throw new FormattedException(msg,e);
        }catch(JSONException|IOException e) {
            throw new FormattedException(e.getMessage(),e);
        }
    }

    public static String randomId() {
        return ""+(int)(Math.random() * 10000000);
    }

    public interface APITask<T> {
        T run() throws APIException, JSONException, IOException;

    }

    public interface FormattedTask<T> {
        T run() throws FormattedException;
    }

}
