package com.vtominator.freddo.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vtominator.freddo.Control.EventAdapter;
import com.vtominator.freddo.Model.Constants;
import com.vtominator.freddo.Model.Event;
import com.vtominator.freddo.Model.SharedPrefManager;
import com.vtominator.freddo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ActivityHome extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ActivityHome";
    private Context mContext = ActivityHome.this;

    private RecyclerView mRecycleView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<Event> eventList = new ArrayList<>();
//    final EventFragment eventFragment = new EventFragment();

    private boolean ADMIN = false;
    private String PERMISSION;
    private CheckBox onlyActives;
    boolean isActive;

    private TextView tvCancel, tvSend;
    private EditText etReason;
//
//    private TabLayout tabLayout;
//    private PagerAdapter pagerAdapter;
//    private TabItem profileTab;
//    private TabItem favoriteTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRecycleView = findViewById(R.id.recycleView);

        listToView();

        SharedPrefManager.getInstance(mContext).getUserId();
        if (SharedPrefManager.getInstance(mContext).getUsername().equals("admin")) {
            ADMIN = true;
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    int position = viewHolder.getAdapterPosition();
                    Event currentEvent = eventList.get(position);
                    //deleteEvent(viewHolder, currentEvent);
                    popupWindow(currentEvent);

                }
            }).attachToRecyclerView(mRecycleView);
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    int position = viewHolder.getAdapterPosition();
                    Event currentEvent = eventList.get(position);
                    acceptEvent(viewHolder, currentEvent);
                }
            }).attachToRecyclerView(mRecycleView);

            onlyActives = findViewById(R.id.onlyActives);
            onlyActives.setVisibility(View.VISIBLE);
            onlyActives.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isActive) {
                    if (isActive) loadInactiveEvents();
                    else loadEvents();
                }

            });

        } else {
            ADMIN = false;
        }


        topToolbar(); // Felső menüsor létrehozása
        spinnerAdapter(); // Rendezés módja spinnerként

        loadEvents(); // Az adatbázisból lekért elemek listába fejtése


    }

    private void declineEvent(View view, Event currentEvent, PopupWindow popupWindow) {
        final Context mContext = view.getContext();
        final Event tempEvent = currentEvent;
        popupWindow.dismiss();

        final String eventname = tempEvent.getEventname();
        final int event_id = tempEvent.getEvent_id();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DECLINEEVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {

                        mAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                    }
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("eventname", eventname);
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void acceptEvent(final RecyclerView.ViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.itemView.getContext();
        final Event tempEvent = currentEvent;

        final String eventname = tempEvent.getEventname();
        final int event_id = tempEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ACTIVATEEVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {

                        mAdapter.notifyDataSetChanged();
                        deleteReason(currentEvent);

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        mAdapter.notifyDataSetChanged();
                    }
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("eventname", eventname);
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }


    private void topToolbar() {
        Toolbar topToolbar = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, ActivityLogin.class));
                break;

            case R.id.menuProfile:
                startActivity(new Intent(this, ActivityProfile.class));
                break;
        }
        return true;
    }

    private void spinnerAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String text = parent.getItemAtPosition(pos).toString();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
        isActive = sharedPreferences.getBoolean("isActive", false);

        listToView();
        if (text.equals("Név szerint")) {
            Collections.sort(eventList, Event.BY_NAME);

            mRecycleView.setLayoutManager(mLayoutManager);
            mRecycleView.setAdapter(mAdapter);

        } else if (text.equals("Idő szerint növekvő")) {
            Collections.sort(eventList, Event.BY_DATE);

            mRecycleView.setLayoutManager(mLayoutManager);
            mRecycleView.setAdapter(mAdapter);

        } else if (text.equals("Idő szerint csökkenő")) {
            Collections.sort(eventList, Event.BY_DATE_DESC);

            mRecycleView.setLayoutManager(mLayoutManager);
            mRecycleView.setAdapter(mAdapter);

        } else if (text.equals("Kedvelések alapján növekvő")) {
            Collections.sort(eventList, Event.BY_LIKE);

            mRecycleView.setLayoutManager(mLayoutManager);
            mRecycleView.setAdapter(mAdapter);

        } else if (text.equals("Kedvelések alapján csökkenő")) {
            Collections.sort(eventList, Event.BY_LIKE_DESC);

            mRecycleView.setLayoutManager(mLayoutManager);
            mRecycleView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void listToView() {
        mRecycleView = findViewById(R.id.recycleView);

        mRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new EventAdapter(eventList);

        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);

//        mAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                eventList.get(position);
//
//                Bundle bundle = new Bundle();
//                bundle.putInt("position", position);
//
//
//                eventFragment.setArguments(bundle);
//
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .addToBackStack(null).detach(eventFragment)
//                        .attach(eventFragment)
//                        .replace(R.id.homeContainer, eventFragment)
//                        .commit();
//
//                mAdapter.notifyItemChanged(position);
//            }
//
//            @Override
//            public void onFavoriteClick(int position) {
//
//                //favoriteList.add(new Event(eventList.get(position).getEvent_id(),eventList.get(position).getUsername(),eventList.get(position).getEventname(),eventList.get(position).getLocation(),eventList.get(position).getDate(),eventList.get(position).getTime(),eventList.get(position).getDescription(),eventList.get(position).getFestival(),eventList.get(position).getConcert(),eventList.get(position).getPerformance(),eventList.get(position).getEvent(),eventList.get(position).getDeal(),eventList.get(position).getFree()));
//                // Toast.makeText(mContext,"klikk",Toast.LENGTH_SHORT).show();
//                //favoriteAdapter.notifyItemChanged(position);
//                // mAdapter.notifyItemChanged(position);
//            }
//        });

    }

    private void loadEvents() {

        eventList.clear();

        if (ADMIN) {
            PERMISSION = Constants.URL_GETALLEVENTS;
        } else {
            PERMISSION = Constants.URL_GETACTIVEEVENTS;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, PERMISSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
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

                                    Event eventItem = new Event(event_id, username, eventname, location, date, time, description, festival, concert, performance, event, deal, free);
                                    eventList.add(eventItem);

                                }
                            } else {
                                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

    private void loadInactiveEvents() {

        eventList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETINACTIVEEVENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
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

                                    Event eventItem = new Event(event_id, username, eventname, location, date, time, description, festival, concert, performance, event, deal, free);
                                    eventList.add(eventItem);

                                }
                            } else {
                                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

    private void popupWindow(final Event currentEvent) {


        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.decline_popup_window, null);

        etReason = (EditText) popupView.findViewById(R.id.etReason);
        tvCancel = (TextView) popupView.findViewById(R.id.tvCancel);
        tvSend = (TextView) popupView.findViewById(R.id.tvSend);


        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mAdapter.notifyDataSetChanged();
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addReason(currentEvent, etReason.getText().toString());
                if (etReason.getText().length() != 0) {
                    declineEvent(v, currentEvent, popupWindow);
                }

                mAdapter.notifyDataSetChanged();
            }
        });


    }

    private void addReason(Event currentEvent, String reason) {
        final int event_id = currentEvent.getEvent_id();
        final String mReason = reason;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADDREASON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", Integer.toString(event_id));
                params.put("reason", mReason);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void deleteReason(Event currentEvent) {
        final int event_id = currentEvent.getEvent_id();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEREASON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }


}
