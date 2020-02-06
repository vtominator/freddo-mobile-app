package com.vtominator.freddo.Control;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.vtominator.freddo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.EventViewHolder> {
    public static List<Event> myEventsList;
    private static final String TAG = "MyEventsAdapter";

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgDelete;
        private TextView tvEventName, tvEventDescription, tvLocation, tvEventDate, tvEventTime;
        private TextView tvStatus, tvReason;

        private TextView cbFestivalTag, cbConcertTag, cbPerformanceTag, cbEventTag, cbDealTag, cbFreeTag;

        public EventViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvReason = itemView.findViewById(R.id.tvReason);

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

    public MyEventsAdapter(List<Event> myEventsList) {
        this.myEventsList = myEventsList;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_list_my_events, viewGroup, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view, mListener);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder viewHolder, int i) {
        final Event currentEvent = myEventsList.get(i);
        Context mContext = viewHolder.tvEventName.getContext();

        viewHolder.tvEventName.setText(currentEvent.getEventname());
        viewHolder.tvEventDescription.setText(currentEvent.getDescription());
        viewHolder.tvLocation.setText(currentEvent.getLocation());
        viewHolder.tvEventDate.setText(currentEvent.getDate());
        viewHolder.tvEventTime.setText(currentEvent.getTime().substring(0, 5));

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


        getStatus(viewHolder, currentEvent);
        getReason(viewHolder, currentEvent);


    }

    @Override
    public int getItemCount() {
        return myEventsList.size();
    }


    private void getStatus(final MyEventsAdapter.EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvEventName.getContext();
        final String eventname = currentEvent.getEventname();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETSTATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject likeObject = new JSONObject(response);
                    String status = likeObject.getString("message");
                    currentEvent.setStatus(status);

                    if (!likeObject.getBoolean("error")) {

                        viewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.green));
                        viewHolder.tvStatus.setText(currentEvent.getStatus());

                    } else {
                        viewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                        viewHolder.tvStatus.setText(currentEvent.getStatus());
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

    private void getReason(final MyEventsAdapter.EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvEventName.getContext();

        final int event_id = currentEvent.getEvent_id();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETREASON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject likeObject = new JSONObject(response);


                    if (!likeObject.getBoolean("error")) {
                        String reason = likeObject.getString("reason");
                        currentEvent.setReason(reason);
                        viewHolder.tvReason.setText(currentEvent.getReason());

                    } else {
                        viewHolder.tvReason.setText(currentEvent.getReason());
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

}