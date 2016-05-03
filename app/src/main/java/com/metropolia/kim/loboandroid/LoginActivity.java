package com.metropolia.kim.loboandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kimmo on 02/05/2016.
 */
public class LoginActivity extends AppCompatActivity {
    private Intent intent;
    private EditText login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (EditText) findViewById(R.id.editLogin);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        intent = new Intent(this, MainActivity.class);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = login.getText().toString();
                if (name == ""){
                    Toast toast = Toast.makeText(getApplicationContext(),"Please insert your username",Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    LoginTask lt = new LoginTask(LoginActivity.this);
                    String[] params = {name};
                    lt.execute(params);

                }
                //startActivity(intent);
            }
        });
    }
}
