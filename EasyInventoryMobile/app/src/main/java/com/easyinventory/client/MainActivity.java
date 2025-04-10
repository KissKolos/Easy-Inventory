package com.easyinventory.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.easyinventory.client.ui.UIUtils;

import easyinventoryapi.UserInfo;

public class MainActivity extends AppCompatActivity {

    public static easyinventoryapi.API api;
    public static String user_id,user_name;

    public static boolean isStopped() {
        return Worker.GLOBAL.getState()== Thread.State.NEW||api==null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.setProperty("http.keepAlive", "false");

        if(Worker.GLOBAL.getState()== Thread.State.NEW)
            Worker.GLOBAL.start();
        else
            System.out.println(Worker.GLOBAL.getState());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText username=findViewById(R.id.editUserName);
        EditText server=findViewById(R.id.editServer);
        EditText password=findViewById(R.id.editPassword);

        this.requestPermissions(new String[]{"android.permission.INTERNET"},0);

        Button b=findViewById(R.id.loginButton);
        b.setOnClickListener(_e->{
            b.setEnabled(false);
            String username_v=username.getText().toString();
            String server_v=server.getText().toString();
            String password_v=password.getText().toString();

            UIUtils.waitForTask(this,this.getWindow().getDecorView().getRootView(),()->
                UIUtils.formatException(()->{
                    api=easyinventoryapi.API.connect(new AndroidHTTPConnector(),server_v,username_v,password_v);
                    UserInfo info=api.getUserinfo();
                    user_name=info.username;
                    user_id= info.user;
                    return true;
                },getResources(),"login_fail")
            ,r->{
                b.setEnabled(true);
                this.startActivity(new Intent(this,LoggedInActivity.class));
            },()-> b.setEnabled(true));
        });
    }
}