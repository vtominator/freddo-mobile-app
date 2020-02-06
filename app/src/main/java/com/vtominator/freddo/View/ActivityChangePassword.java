package com.vtominator.freddo.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vtominator.freddo.Model.Constants;
import com.vtominator.freddo.Model.SharedPrefManager;
import com.vtominator.freddo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class ActivityChangePassword extends AppCompatActivity implements View.OnClickListener {

    private Context mContext = ActivityChangePassword.this;
    private EditText etOldPassword, etNewPassword, etNewPasswordAgain;
    private Button bChangePassword;
    private TextView tvCancel;
    private ProgressDialog progressDialog;
    private static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewPasswordAgain = findViewById(R.id.etNewPasswordAgain);

        tvCancel = findViewById(R.id.tvCancel);
        bChangePassword = findViewById(R.id.bChangePassword);

        bChangePassword.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Kérem várjon...");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bChangePassword:
                changePassword();
                break;
            case R.id.tvCancel:
                startActivity(new Intent(mContext, ActivitySettings.class));
                break;
        }
    }

    private void changePassword() {
        final String username = String.valueOf(SharedPrefManager.getInstance(this).getUsername());
        final String old_password = etOldPassword.getText().toString().trim();
        final String new_password = etNewPassword.getText().toString().trim();
        final String new_password_again = etNewPasswordAgain.getText().toString().trim();
        final String password = String.valueOf(SharedPrefManager.getInstance(this).getPassword());


        if (new_password.equals(old_password)) {
            Toast.makeText(mContext, "Az új jelszó megegyezik a jelenlegi jelszóval!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!new_password.equals(new_password_again)) {
            Toast.makeText(mContext, "Az új jelszavak nem egyeznek meg!", Toast.LENGTH_LONG).show();
            return;
        }
        if ((!password.equals(md5(old_password))) && (flag == false)) {
            Toast.makeText(mContext, "Helytelen régi jelszó!", Toast.LENGTH_LONG).show();
            return;
        }
        if ((!md5(password).equals(md5(old_password))) && (flag == true)) {
            Toast.makeText(mContext, "Helytelen régi jelszó!", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Jelszó megváltoztatása...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHANGEPASSWORD,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if (jsonObject.getBoolean("error") == false) {
                                SharedPrefManager.getInstance(mContext).passwordChange(
                                        jsonObject.getString("password")
                                );
                                flag = true;
                                finish();
                                startActivity(new Intent(mContext, ActivitySettings.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", username);
                params.put("old_password", old_password);
                params.put("new_password", new_password);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger md5Data = new BigInteger(1, md.digest(input.getBytes()));
            return String.format("%032X", md5Data).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}


