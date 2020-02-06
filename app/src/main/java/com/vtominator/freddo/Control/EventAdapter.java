package com.vtominator.freddo.Control;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
import com.vtominator.freddo.Model.Event;
import com.vtominator.freddo.Model.SharedPrefManager;
import com.vtominator.freddo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Filterable {
    private static final String TAG = "EventAdapter";

    private List<Event> eventList;
    private List<Event> eventListFull;
    private boolean ADMIN = false;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);

        void onFavoriteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgFavorite;
        private TextView tvEventName, tvLocation, tvEventDate, tvEventTime, tvEventDescription, tvLikes, tvActive;
        private TextView cbFestivalTag, cbConcertTag, cbPerformanceTag, cbEventTag, cbDealTag, cbFreeTag;


        public EventViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

//           // Kártya klikkelés
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null){
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION){
//                            listener.onItemClick(position);
//                        }
//                    }
//                }
//            });

            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvActive = itemView.findViewById(R.id.tvActive);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);


            cbFestivalTag = itemView.findViewById(R.id.cbFestivalTag);
            cbConcertTag = itemView.findViewById(R.id.cbConcertTag);
            cbPerformanceTag = itemView.findViewById(R.id.cbPerformanceTag);
            cbEventTag = itemView.findViewById(R.id.cbEventTag);
            cbDealTag = itemView.findViewById(R.id.cbDealTag);
            cbFreeTag = itemView.findViewById(R.id.cbFreeTag);

            cbFestivalTag.setVisibility(View.GONE);
            cbConcertTag.setVisibility(View.GONE);
            cbPerformanceTag.setVisibility(View.GONE);
            cbEventTag.setVisibility(View.GONE);
            cbDealTag.setVisibility(View.GONE);
            cbFreeTag.setVisibility(View.GONE);


        }
    }

    public EventAdapter(List<Event> eventList) {
        eventListFull = new ArrayList<>(eventList);
        this.eventList = eventList;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_home_list_event, viewGroup, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view, mListener);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder viewHolder, int i) {
        final Event currentEvent = eventList.get(i);
        Context mContext = viewHolder.tvEventName.getContext();

        SharedPrefManager.getInstance(mContext).getUserId();
        if (SharedPrefManager.getInstance(mContext).getUsername().equals("admin")) {
            ADMIN = true;
            getActives(viewHolder, currentEvent);
        }
        getFavorites(viewHolder, currentEvent);
        getLikes(viewHolder, currentEvent);

        viewHolder.tvEventName.setText(currentEvent.getEventname());
        viewHolder.tvLocation.setText(currentEvent.getLocation());
        viewHolder.tvEventDate.setText(currentEvent.getDate());
        viewHolder.tvEventTime.setText(currentEvent.getTime().substring(0, 5));
        viewHolder.tvEventDescription.setText(currentEvent.getDescription());


        if (currentEvent.getFestival() == 1) {
            viewHolder.cbFestivalTag.setVisibility(View.VISIBLE);
        }
        if (currentEvent.getConcert() == 1) {
            viewHolder.cbConcertTag.setVisibility(View.VISIBLE);
        }
        if (currentEvent.getPerformance() == 1) {
            viewHolder.cbPerformanceTag.setVisibility(View.VISIBLE);
        }
        if (currentEvent.getEvent() == 1) {
            viewHolder.cbEventTag.setVisibility(View.VISIBLE);
        }
        if (currentEvent.getDeal() == 1) {
            viewHolder.cbDealTag.setVisibility(View.VISIBLE);
        }
        if (currentEvent.getFree() == 1) {
            viewHolder.cbFreeTag.setVisibility(View.VISIBLE);
        }

        final Event tempEvent = currentEvent;

        viewHolder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentEvent.isFavorite()) addFavorite(v, tempEvent);
                else deleteFavorite(v, tempEvent);
            }
        });

    }

    private void addFavorite(final View v, final Event currentEvent) {
        final int user_id = SharedPrefManager.getInstance(v.getContext()).getUserId();
        final int event_id = currentEvent.getEvent_id();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETFAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(v.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setFavorite(true);
                        notifyDataSetChanged();


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
                params.put("user_id", Integer.toString(user_id));
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
        requestQueue.add(stringRequest);
    }

    private void deleteFavorite(final View v, final Event currentEvent) {
        final int user_id = SharedPrefManager.getInstance(v.getContext()).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEFAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(v.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setFavorite(false);
                        notifyDataSetChanged();

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
                params.put("user_id", Integer.toString(user_id));
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
        requestQueue.add(stringRequest);
    }


    private void getFavorites(final EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvLikes.getContext();

        final int currentUser = SharedPrefManager.getInstance(mContext).getUserId();
        final int currentId = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETFAVORITES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean("error")) {
                        JSONArray favorites = object.getJSONArray("result");

                        for (int i = 0; i < favorites.length(); i++) {
                            JSONObject favObj = favorites.getJSONObject(i);

                            int user_id = favObj.getInt("user_id");
                            int event_id = favObj.getInt("event_id");


                            if (currentUser == user_id) {
                                if (currentId == event_id) {
                                    viewHolder.imgFavorite.setBackgroundResource(R.drawable.favorite);
                                    currentEvent.setFavorite(true);
                                    return;
                                }
                            }
                            viewHolder.imgFavorite.setBackgroundResource(R.drawable.nofavorite);
                        }
                    } else {
                        viewHolder.imgFavorite.setBackgroundResource(R.drawable.nofavorite);
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
        });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void getLikes(final EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvLikes.getContext();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETLIKES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject likeObject = new JSONObject(response);

                    if (!likeObject.getBoolean("error")) {
                        JSONArray jsonArray = likeObject.getJSONArray("likes");
                        JSONObject o = jsonArray.getJSONObject(0);

                        int like = o.getInt("likes");

                        currentEvent.setLike(like);
                        viewHolder.tvLikes.setText(Integer.toString(currentEvent.getLike()) + " embernek tetszik");

                    } else {
                        viewHolder.tvLikes.setText(Integer.toString(0) + " embernek tetszik");
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
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void getActives(final EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvLikes.getContext();
        final String eventname = currentEvent.getEventname();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ISACTIVE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject likeObject = new JSONObject(response);
                    String active = likeObject.getString("message");
                    currentEvent.setActive(active);

                    if (!likeObject.getBoolean("error")) {

                        viewHolder.tvActive.setTextColor(mContext.getResources().getColor(R.color.green));
                        viewHolder.tvActive.setText(currentEvent.getActive());

                    } else {
                        viewHolder.tvActive.setTextColor(mContext.getResources().getColor(R.color.red));
                        viewHolder.tvActive.setText(currentEvent.getActive());
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


    @Override
    public int getItemCount() {
        return eventList.size();
    }


    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    public Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventListFull);

            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();

                // Szűrés eventre név / leírás / szervező / hely alapján
                for (Event event : eventListFull) {
                    if (event.getEventname().toLowerCase().contains(filterPattern) || event.getDescription().toLowerCase().contains(filterPattern) ||
                            event.getUsername().toLowerCase().contains(filterPattern) || event.getLocation().toLowerCase().contains(filterPattern)) {

                        filteredList.add(event);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            eventList.clear();
            eventList.addAll((List) results.values);

            notifyDataSetChanged();
        }
    };

}