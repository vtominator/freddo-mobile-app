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


public class ActivityChangeEmail extends AppCompatActivity implements View.OnClickListener {

    private Context mContext = ActivityChangeEmail.this;
    private EditText etOldEmail, etNewEmail, etNewEmailAgain, etPassword;
    private Button bChangeEmail;
    private TextView tvCancel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        etOldEmail = findViewById(R.id.etOldEmail);
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewEmailAgain = findViewById(R.id.etNewEmailAgain);
        etPassword = findViewById(R.id.etPassword);

        tvCancel = findViewById(R.id.tvCancel);
        bChangeEmail = findViewById(R.id.bChangeEmail);

        bChangeEmail.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Kérem várjon...");

    }

    private void changeEmail() {
        final String username = String.valueOf(SharedPrefManager.getInstance(this).getUsername());
        final String old_email = etOldEmail.getText().toString().trim();
        final String new_email = etNewEmail.getText().toString().trim();
        final String new_email_again = etNewEmailAgain.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String db_password = String.valueOf(SharedPrefManager.getInstance(this).getPassword());
        final String db_email = String.valueOf(SharedPrefManager.getInstance(this).getEmail());

        if (new_email.equals(old_email)) {
            Toast.makeText(mContext, "Az új email cím megegyezik a jelenlegi email címmel!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!new_email.equals(new_email_again)) {
            Toast.makeText(mContext, "Az új email címek nem egyeznek meg!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!old_email.equals(db_email)) {
            Toast.makeText(mContext, "Helytelen email cím!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!md5(password).equals(db_password)) {
            Toast.makeText(mContext, "Helytelen jelszó!", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Email megváltoztatása...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CHANGEEMAIL,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if (jsonObject.getBoolean("error") == false) {
                                SharedPrefManager.getInstance(mContext).emailChange(
                                        jsonObject.getString("email")
                                );
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
                params.put("old_email", old_email);
                params.put("new_email", new_email);
                params.put("password", password);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger md5Data = new BigInteger(1, md.digest(input.getBytes()));
            return String.format("%032X", md5Data).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bChangeEmail:
                changeEmail();
                break;
            case R.id.tvCancel:
                startActivity(new Intent(mContext, ActivitySettings.class));
                break;
        }
    }


}


