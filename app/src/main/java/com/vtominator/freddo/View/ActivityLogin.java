package com.vtominator.freddo.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vtominator.freddo.Control.RequestHandler;
import com.vtominator.freddo.Model.Constants;
import com.vtominator.freddo.Model.SharedPrefManager;
import com.vtominator.freddo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private Button bLogin;
    private EditText etUsername, etPassword;
    private TextView tvRegister;
    private ProgressDialog progressDialog;
    private Context mContext = ActivityLogin.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CheckBox stayLoggedIn = findViewById(R.id.cbStayLoggedIn);
        boolean isChecked = false;

        stayLoggedIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isChecked", isChecked);
                editor.commit();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
        isChecked = sharedPreferences.getBoolean("isChecked", false);


        if (isChecked) {
            if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                finish();
                startActivity(new Intent(mContext, ActivityHome.class));
                return;
            }
        } else {
            SharedPrefManager.getInstance(this).logout();
        }


        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.bLogin);
        tvRegister = findViewById(R.id.tvRegister);

        bLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Kérem várjon...");
    }

    private void userLogin() {
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(mContext).userLogin(
                                        obj.getInt("user_id"),
                                        obj.getString("name"),
                                        obj.getString("username"),
                                        obj.getString("email"),
                                        obj.getString("password")
                                );
                                startActivity(new Intent(mContext, ActivityHome.class));
                                finish();
                                Toast.makeText(mContext, "Sikeres bejelentkezés", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogin:
                userLogin();
                break;
            case R.id.tvRegister:
                if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
                    startActivity(new Intent(this, ActivityRegistration.class));
                    break;
                }
        }
    }
}


