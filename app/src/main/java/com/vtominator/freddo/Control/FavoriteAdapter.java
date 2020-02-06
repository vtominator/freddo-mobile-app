package com.vtominator.freddo.Control;


import android.content.Intent;
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
import com.vtominator.freddo.Model.SharedPrefManager;
import com.vtominator.freddo.R;
import com.vtominator.freddo.View.ActivityProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.EventViewHolder> {
    public static List<Event> favoriteList;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgDelete;
        private TextView tvEventName, tvTimeLeft;

        public EventViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);


            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            imgDelete = itemView.findViewById(R.id.imgDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public FavoriteAdapter(List<Event> favoriteList) {
        this.favoriteList = favoriteList;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_profile_favorite_events, viewGroup, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view, mListener);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder viewHolder, int i) {
        final Event currentEvent = favoriteList.get(i);

        long diff = Event.getTimeLeft(currentEvent);
        int days = (int) (diff / (24 * 60 * 60 * 1000)) + 1;

        if (days == 1) {
            viewHolder.tvTimeLeft.setText(String.valueOf("Ma"));
        } else if (days <= 0) {
            viewHolder.tvTimeLeft.setText(String.valueOf("kakker"));
        } else if (days < 2) {
            viewHolder.tvTimeLeft.setText(String.valueOf("Holnap"));
        } else {
            viewHolder.tvTimeLeft.setText(String.valueOf(days + " nap múlva"));
        }


        viewHolder.tvEventName.setText(currentEvent.getEventname());


        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFavorite(v, currentEvent);
            }
        });
    }

    private void deleteFavorite(final View v, Event currentEvent) {
        final int user_id = SharedPrefManager.getInstance(v.getContext()).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEFAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(v.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {
                        notifyDataSetChanged();
                        Intent refresh = new Intent(v.getContext(), ActivityProfile.class);
                        refresh.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        v.getContext().startActivity(refresh);
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

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }


}