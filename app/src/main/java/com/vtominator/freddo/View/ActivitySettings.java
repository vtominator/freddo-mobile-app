package com.vtominator.freddo.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vtominator.freddo.R;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener {

    private Button bChangePassword, bChangeEmail, bDeleteUser;
    private TextView tvCancel;
    private Context mContext = ActivitySettings.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        tvCancel = findViewById(R.id.tvCancel);
        bChangePassword = findViewById(R.id.bChangePassword);
        bChangeEmail = findViewById(R.id.bChangeEmail);
        bDeleteUser = findViewById(R.id.bDeleteUser);


        bChangePassword.setOnClickListener(this);
        bChangeEmail.setOnClickListener(this);
        bDeleteUser.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bChangePassword:
                startActivity(new Intent(mContext, ActivityChangePassword.class));
                break;
            case R.id.bChangeEmail:
                startActivity(new Intent(mContext, ActivityChangeEmail.class));
                break;
            case R.id.bDeleteUser:
                startActivity(new Intent(mContext, ActivityDeleteUser.class));
                break;
            case R.id.tvCancel:
                startActivity(new Intent(mContext, ActivityHome.class));
                break;
        }
    }
}
