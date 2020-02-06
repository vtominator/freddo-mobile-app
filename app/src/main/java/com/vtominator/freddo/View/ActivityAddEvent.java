package com.vtominator.freddo.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivityAddEvent extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ActivityAddEvent";
    private Context mContext = ActivityAddEvent.this;

    private Button bDate, bTime, bAdd;
    private TextView tvEventLocationTitle, tvEventDateTitle, tvEventTimeTitle, tvEventTypeTitle, tvEventDescriptionTitle, tvEventDate, tvEventTime, tvCharCount, tvCancel;
    private EditText etEventName, etEventLocation, etDescription;
    private ProgressDialog progressDialog;
    private LinearLayout linearDate, linearTime;
    private GridLayout gridBoxes;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private int festival = 0, concert = 0, performance = 0, event = 0, deal = 0, free = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


        tvEventLocationTitle = findViewById(R.id.tvEventLocationTitle);
        tvEventDateTitle = findViewById(R.id.tvEventDateTitle);
        linearDate = findViewById(R.id.linearDate);
        tvEventTimeTitle = findViewById(R.id.tvEventTimeTitle);
        linearTime = findViewById(R.id.linearTime);
        tvEventTypeTitle = findViewById(R.id.tvEventTypeTitle);
        gridBoxes = findViewById(R.id.gridBoxes);
        tvEventDescriptionTitle = findViewById(R.id.tvEventDescriptionTitle);

        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventTime = findViewById(R.id.tvEventTime);
        tvCharCount = findViewById(R.id.tvCharCount);
        tvCancel = findViewById(R.id.tvCancel);

        etEventName = findViewById(R.id.etEventName);
        etEventLocation = findViewById(R.id.etEventLocation);
        etDescription = findViewById(R.id.etDescription);

        bDate = findViewById(R.id.bDate);
        bTime = findViewById(R.id.bTime);
        bAdd = findViewById(R.id.bAdd);

        bDate.setOnClickListener(this);
        bTime.setOnClickListener(this);
        bAdd.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);


        tvEventLocationTitle.setVisibility(View.GONE);
        etEventLocation.setVisibility(View.GONE);
        tvEventDateTitle.setVisibility(View.GONE);
        linearDate.setVisibility(View.GONE);
        tvEventTimeTitle.setVisibility(View.GONE);
        linearTime.setVisibility(View.GONE);
        tvEventTypeTitle.setVisibility(View.GONE);
        gridBoxes.setVisibility(View.GONE);
        tvEventDescriptionTitle.setVisibility(View.GONE);
        tvCharCount.setVisibility(View.GONE);
        etDescription.setVisibility(View.GONE);
        bAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        bAdd.setClickable(false);

        charCount();
        invisibleFields();
    }


    private void addEvent() {
        final String username = SharedPrefManager.getInstance(this).getUsername();
        final String eventName = etEventName.getText().toString().trim();
        final String eventLocation = etEventLocation.getText().toString().trim();
        final String eventDate = tvEventDate.getText().toString().trim();
        final String eventTime = tvEventTime.getText().toString().trim();
        final String eventDescription = etDescription.getText().toString().trim();


        progressDialog.setMessage("Esemény hozzáadása...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETEVENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {
                        startActivity(new Intent(mContext, ActivityHome.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("eventname", eventName);
                params.put("location", eventLocation);
                params.put("date", eventDate);
                params.put("time", eventTime);
                params.put("description", eventDescription);
                params.put("festival", Integer.toString(festival));
                params.put("concert", Integer.toString(concert));
                params.put("performance", Integer.toString(performance));
                params.put("event", Integer.toString(event));
                params.put("deal", Integer.toString(deal));
                params.put("free", Integer.toString(free));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    } //Esemény hozzáadása az adatbázishoz

    private void invisibleFields() {
        etEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    tvEventLocationTitle.setVisibility(View.VISIBLE);
                    etEventLocation.setVisibility(View.VISIBLE);

                    if (etEventLocation.length() != 0) {
                        tvEventDateTitle.setVisibility(View.VISIBLE);
                        linearDate.setVisibility(View.VISIBLE);
                        tvEventTimeTitle.setVisibility(View.VISIBLE);
                        linearTime.setVisibility(View.VISIBLE);

                        if (tvEventDate.length() != 0 && tvEventTime.length() != 0) {
                            tvEventTypeTitle.setVisibility(View.VISIBLE);
                            gridBoxes.setVisibility(View.VISIBLE);
                            tvEventDescriptionTitle.setVisibility(View.VISIBLE);
                            tvCharCount.setVisibility(View.VISIBLE);
                            etDescription.setVisibility(View.VISIBLE);

                            if (etDescription.length() != 0) {
                                bAdd.getBackground().setColorFilter(null);
                                bAdd.setClickable(true);
                            }
                        }
                    }

                    etEventLocation.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() != 0) {
                                tvEventDateTitle.setVisibility(View.VISIBLE);
                                tvEventTimeTitle.setVisibility(View.VISIBLE);
                                linearDate.setVisibility(View.VISIBLE);
                                linearTime.setVisibility(View.VISIBLE);

                                if (tvEventDate.length() != 0 && tvEventTime.length() != 0) {
                                    tvEventTypeTitle.setVisibility(View.VISIBLE);
                                    gridBoxes.setVisibility(View.VISIBLE);
                                    tvEventDescriptionTitle.setVisibility(View.VISIBLE);
                                    tvCharCount.setVisibility(View.VISIBLE);
                                    etDescription.setVisibility(View.VISIBLE);

                                    if (etDescription.length() != 0) {
                                        bAdd.getBackground().setColorFilter(null);
                                        bAdd.setClickable(true);
                                    }
                                }

                                tvEventDate.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (s.length() != 0) {
                                            tvEventTime.addTextChangedListener(new TextWatcher() {

                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if (s.length() != 0) {
                                                        tvEventTypeTitle.setVisibility(View.VISIBLE);
                                                        gridBoxes.setVisibility(View.VISIBLE);
                                                        tvEventDescriptionTitle.setVisibility(View.VISIBLE);
                                                        tvCharCount.setVisibility(View.VISIBLE);
                                                        etDescription.setVisibility(View.VISIBLE);


                                                        etDescription.addTextChangedListener(new TextWatcher() {
                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                                if (s.length() != 0) {
                                                                    bAdd.getBackground().setColorFilter(null);
                                                                    bAdd.setClickable(true);
                                                                } else {
                                                                    bAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                                                    bAdd.setClickable(false);
                                                                }
                                                            }

                                                            @Override
                                                            public void afterTextChanged(Editable s) {

                                                            }
                                                        });
                                                    } else {
                                                        tvEventTypeTitle.setVisibility(View.GONE);
                                                        gridBoxes.setVisibility(View.GONE);
                                                        tvEventDescriptionTitle.setVisibility(View.GONE);
                                                        tvCharCount.setVisibility(View.GONE);
                                                        etDescription.setVisibility(View.GONE);
                                                        bAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                                        bAdd.setClickable(false);
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {

                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                tvEventTime.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (s.length() != 0) {
                                            tvEventDate.addTextChangedListener(new TextWatcher() {

                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if (s.length() != 0) {
                                                        tvEventTypeTitle.setVisibility(View.VISIBLE);
                                                        gridBoxes.setVisibility(View.VISIBLE);
                                                        tvEventDescriptionTitle.setVisibility(View.VISIBLE);
                                                        tvCharCount.setVisibility(View.VISIBLE);
                                                        etDescription.setVisibility(View.VISIBLE);

                                                        etDescription.addTextChangedListener(new TextWatcher() {
                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                                if (s.length() != 0) {
                                                                    bAdd.getBackground().setColorFilter(null);
                                                                    bAdd.setClickable(true);
                                                                } else {
                                                                    bAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                                                    bAdd.setClickable(false);
                                                                }
                                                            }

                                                            @Override
                                                            public void afterTextChanged(Editable s) {

                                                            }
                                                        });
                                                    } else {
                                                        tvEventTypeTitle.setVisibility(View.GONE);
                                                        gridBoxes.setVisibility(View.GONE);
                                                        tvEventDescriptionTitle.setVisibility(View.GONE);
                                                        tvCharCount.setVisibility(View.GONE);
                                                        etDescription.setVisibility(View.GONE);
                                                        bAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                                        bAdd.setClickable(false);
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {

                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                            } else {
                                tvEventDateTitle.setVisibility(View.GONE);
                                tvEventTimeTitle.setVisibility(View.GONE);
                                tvEventTypeTitle.setVisibility(View.GONE);
                                linearDate.setVisibility(View.GONE);
                                linearTime.setVisibility(View.GONE);
                                gridBoxes.setVisibility(View.GONE);
                                tvEventDescriptionTitle.setVisibility(View.GONE);
                                tvCharCount.setVisibility(View.GONE);
                                etDescription.setVisibility(View.GONE);
                                bAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                bAdd.setClickable(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } else {
                    tvEventLocationTitle.setVisibility(View.GONE);
                    etEventLocation.setVisibility(View.GONE);
                    tvEventDateTitle.setVisibility(View.GONE);
                    tvEventTimeTitle.setVisibility(View.GONE);
                    linearDate.setVisibility(View.GONE);
                    linearTime.setVisibility(View.GONE);
                    tvEventTypeTitle.setVisibility(View.GONE);
                    gridBoxes.setVisibility(View.GONE);
                    tvEventDescriptionTitle.setVisibility(View.GONE);
                    tvCharCount.setVisibility(View.GONE);
                    etDescription.setVisibility(View.GONE);
                    bAdd.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    bAdd.setClickable(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    } //Regisztráció során csak akkor jelennek meg a kitöltendő mezők, ha a korábbiak már ki lettek töltve

    private void selectTime() {
        calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tvEventTime.setText(hourOfDay + ":" + String.format("%02d", minute));
            }
        }, hour, minute, false);

        timePickerDialog.show();
    } //Időválasztó ablak

    private void selectDate() {
        calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                mMonth += 1;
                // tvEventDate.setText(mYear+". "+monthMap.get(mMonth)+" "+mDay+".");
                tvEventDate.setText(mYear + "-" + String.format("%02d", mMonth) + "-" + String.format("%02d", mDay));
            }
        }, year, month, day);

        datePickerDialog.show();
    } //Dátumválasztó ablak

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.cbFestival:
                if (checked)
                    festival = 1;
                break;
            case R.id.cbConcert:
                if (checked)
                    concert = 1;
                break;
            case R.id.cbPerformance:
                if (checked)
                    performance = 1;
                break;
            case R.id.cbEvent:
                if (checked)
                    event = 1;
                break;
            case R.id.cbDeal:
                if (checked)
                    deal = 1;
                break;
            case R.id.cbFree:
                if (checked)
                    free = 1;
                break;
        }
    } //CheckBox-mezők igazságértékének vizsgálata

    private void charCount() {
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharCount.setText(250 - s.toString().length() + "/250 karakter");
            }
        });
    } //A leírás megadásakor megjelenő "hátralévő karakterek" száma

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bDate:
                selectDate();
                break;
            case R.id.bTime:
                selectTime();
                break;
            case R.id.bAdd:
                addEvent();
                break;
            case R.id.tvCancel:
                startActivity(new Intent(mContext, ActivityProfile.class));
                break;
        }
    }


}
