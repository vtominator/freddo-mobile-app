package com.vtominator.freddo.View;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vtominator.freddo.Model.Event;
import com.vtominator.freddo.Control.MyEventsAdapter;
import com.vtominator.freddo.R;
import com.vtominator.freddo.Model.Constants;
import com.vtominator.freddo.Model.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityMyEvents extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ActivityMyEvents";
    private Context mContext = ActivityMyEvents.this;

    private RecyclerView mRecycleView;
    private MyEventsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView tvNo, tvYes, tvCancel;

    final ArrayList<Event> myEventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);


        tvCancel = findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);

        loadMyEvents();

    }

    private void loadMyEvents() {
        final String currentUser = SharedPrefManager.getInstance(mContext).getUsername();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETMYEVENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")){
                                JSONArray events = jsonObject.getJSONArray("events");


                            for (int i = 0; i < events.length(); i++) {
                                JSONObject eventObject = events.getJSONObject(i);

                                int event_id = eventObject.getInt("event_id");
                                String username = eventObject.getString("username");
                                String eventname = eventObject.getString("eventname");
                                String location = eventObject.getString("location");
                                String date = eventObject.getString("date");
                                String time = eventObject.getString("time");
                                String description = eventObject.getString("description");
                                int festival = eventObject.getInt("festival");
                                int concert = eventObject.getInt("concert");
                                int performance = eventObject.getInt("performance");
                                int event = eventObject.getInt("event");
                                int deal = eventObject.getInt("deal");
                                int free = eventObject.getInt("free");


                                if (currentUser.equals(username)) {
                                    Event eventItem = new Event(event_id, username, eventname, location, date, time, description, festival, concert, performance, event, deal, free);
                                    myEventsList.add(eventItem);

                                }
                            }

                            } else {
                                Toast.makeText(
                                        mContext,
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            listToView(); // A lista beletöltése a viewba

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", currentUser);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void listToView() {
        mRecycleView = findViewById(R.id.recycleViewMyEvents);
        mRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MyEventsAdapter(myEventsList);

        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MyEventsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                myEventsList.get(position);
                mAdapter.notifyItemChanged(position);
            }

            public void onDeleteClick(final int position) {
                final Event currentEvent = myEventsList.get(position);
                popupWindow(currentEvent);
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    private void popupWindow(final Event currentEvent){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.delete_popup_window, null);

        tvNo =  popupView.findViewById(R.id.tvNo);
        tvYes =  popupView.findViewById(R.id.tvYes);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(v, currentEvent, popupWindow);
            }
        });
    }

    private void deleteEvent(final View v, Event currentEvent, PopupWindow popupWindow) {

        popupWindow.dismiss();

        final String username = SharedPrefManager.getInstance(v.getContext()).getUsername();
        final int event_id = currentEvent.getEvent_id();

        final Event tempEvent = currentEvent;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEEVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {

                        ActivityHome.eventList.remove(tempEvent);
                        Intent refresh = new Intent(mContext, ActivityMyEvents.class);
                        refresh.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        mContext.startActivity(refresh);
                        finish();

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvCancel:
                startActivity(new Intent(mContext, ActivityProfile.class));
                break;
        }
    }
}
