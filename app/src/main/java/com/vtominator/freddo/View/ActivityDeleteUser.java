package com.vtominator.freddo.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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


public class ActivityDeleteUser extends AppCompatActivity implements View.OnClickListener {

    private Context mContext = ActivityDeleteUser.this;

    private EditText etPassword;
    private Button bDeleteUser;
    private TextView tvCancel;
    private CheckBox cbWarning;
    private int warning = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        etPassword = findViewById(R.id.etPassword);

        tvCancel = findViewById(R.id.tvCancel);
        bDeleteUser = findViewById(R.id.bDeleteUser);

        cbWarning = findViewById(R.id.cbWarning);

        bDeleteUser.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        cbWarning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbWarning.isChecked()) {
                    warning = 1;
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Kérem várjon...");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bDeleteUser:
                deleteUser();
                break;
            case R.id.tvCancel:
                startActivity(new Intent(mContext, ActivitySettings.class));
                break;
        }
    }


    private void deleteUser() {
        final String user_id = String.valueOf(SharedPrefManager.getInstance(this).getUserId());
        final String username = String.valueOf(SharedPrefManager.getInstance(this).getUsername());
        final String password = etPassword.getText().toString().trim();
        final String db_password = String.valueOf(SharedPrefManager.getInstance(this).getPassword());

        if (!md5(password).equals(db_password)) {
            Toast.makeText(mContext, "A megadott jelszó helytelen!", Toast.LENGTH_LONG).show();
            return;
        }
        if (warning == 0) {
            Toast.makeText(mContext, "Erősítse meg a törlési szándékát!", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Felhasználó törlése...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEUSER,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if (jsonObject.getBoolean("error") == false) {
                                SharedPrefManager.getInstance(mContext).logout();
                                finish();
                                startActivity(new Intent(mContext, ActivityLogin.class));
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

                params.put("user_id", user_id);

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


