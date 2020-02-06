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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ActivityRegistration extends AppCompatActivity implements View.OnClickListener {

    private Button bSignUp;
    private EditText etName, etUsername, etEmail, etEmailAgain, etPassword, etPasswordAgain;
    private TextView tvCancel;
    private ProgressDialog progressDialog;
    private Context mContext = ActivityRegistration.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(mContext, ActivityHome.class));
            return;
        }

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etEmailAgain = findViewById(R.id.etEmailAgain);
        etPassword = findViewById(R.id.etPassword);
        etPasswordAgain = findViewById(R.id.etPasswordAgain);

        bSignUp = findViewById(R.id.bSignUp);
        tvCancel = findViewById(R.id.tvCancel);

        bSignUp.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    private void registerUser() {
        final String name = etName.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String email_again = etEmailAgain.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String password_again = etPasswordAgain.getText().toString().trim();

        final String DBusername = SharedPrefManager.getInstance(this).getUsername();
        final String DBemail = SharedPrefManager.getInstance(this).getEmail();


        Random random = new Random();
        final int hash = random.nextInt(999) + 1;
        final int defaultZero = 0;

        progressDialog.setMessage("Felhasználó regisztrálása...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            final JSONObject jsonObject = new JSONObject(response);


                            // A megadott jelszó nem elég biztonságos
                            String pattern = "^\\S*(?=\\S{6,})(?=\\S*[a-z])(?=\\S*[A-Z])(?=\\S*[\\d])\\S*$";
                            if (!etPassword.getText().toString().trim().matches(pattern)) {
                                etPassword.setError("A jelszó legyen legalább 6 karakter hosszú, tartalmazzon kis- és nagybetűket, valamint legalább egy speciális karaktert!");

                            }

                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();


                            if (jsonObject.getBoolean("error") == false) {
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
                params.put("name", name);
                params.put("username", username);
                params.put("email", email);
                params.put("email_again", email_again);
                params.put("password", password);
                params.put("password_again", password_again);
                params.put("hash", Integer.toString(hash));
                params.put("active", Integer.toString(defaultZero));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bSignUp:
                registerUser();
                startActivity(new Intent(mContext, ActivityLogin.class));
                finish();
                break;
            case R.id.tvCancel:
                startActivity(new Intent(mContext, ActivityLogin.class));
                finish();
                break;
        }

    }

}
