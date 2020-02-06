package com.vtominator.freddo.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vtominator.freddo.Control.FavoriteAdapter;
import com.vtominator.freddo.Model.Constants;
import com.vtominator.freddo.Model.Event;
import com.vtominator.freddo.Model.SharedPrefManager;
import com.vtominator.freddo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityProfile extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ActivityProfile";
    private Context mContext = ActivityProfile.this;

    private RecyclerView mRecycleView;
    private FavoriteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    final ArrayList<Event> favoriteList = new ArrayList<>();
    //public static ArrayList<Event> favoriteList = new ArrayList<>();


    private Button bMyEvents, bAddEvent;
    private TextView tvName, tvEmail, tvUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ActivityLogin.class));
        }

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvUsername = findViewById(R.id.tvUsername);
        bMyEvents = findViewById(R.id.bMyEvents);
        bAddEvent = findViewById(R.id.bAddEvent);


        bMyEvents.setOnClickListener(this);
        bAddEvent.setOnClickListener(this);


        tvName.setText(SharedPrefManager.getInstance(this).getName());
        tvUsername.setText(SharedPrefManager.getInstance(this).getUsername());
        tvEmail.setText(SharedPrefManager.getInstance(this).getEmail());

        topToolbar();
        loadFavorites();
    }

    private void topToolbar() {
        Toolbar topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, ActivityLogin.class));
                break;
            case R.id.menuSettings:
                startActivity(new Intent(this, ActivitySettings.class));
                break;
            case R.id.menuHome:
                startActivity(new Intent(this, ActivityHome.class));
                break;
        }
        return true;
    }

    private void listToView() {
        mRecycleView = findViewById(R.id.recycleViewProfile);
        mRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FavoriteAdapter(favoriteList);


        Collections.sort(favoriteList, Event.BY_DATE);


        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new FavoriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                favoriteList.get(position);
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    private void loadFavorites() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETFAVORITES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            if (!object.getBoolean("error")) {

                                JSONArray events = object.getJSONArray("result");
                                //JSONArray events = new JSONArray(response);


                                int currentUser = SharedPrefManager.getInstance(mContext).getUserId();

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
                                    int user_id = eventObject.getInt("user_id");

                                    if (currentUser == user_id) {
                                        Event eventItem = new Event(event_id, username, eventname, location, date, time, description, festival, concert, performance, event, deal, free);
                                        favoriteList.add(eventItem);
                                    }

                                }
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
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bMyEvents:
                startActivity(new Intent(mContext, ActivityMyEvents.class));
                finish();
                break;
            case R.id.bAddEvent:
                startActivity(new Intent(mContext, ActivityAddEvent.class));
                finish();
                break;
        }
    }
}


