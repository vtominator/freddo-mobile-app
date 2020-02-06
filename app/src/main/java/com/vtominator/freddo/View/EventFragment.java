package com.vtominator.freddo.View;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vtominator.freddo.R;
import com.vtominator.freddo.Control.ViewPagerAdapter;


public class EventFragment extends Fragment {

    private TextView tvfrgEventName, tvfrgEventDescription, tvfrgLocation, tvfrgEventDate, tvCancel;
    private static final String TAG = "EventFragment";


    private String[] imageUrls = new String[]{
            "https://cdn.pixabay.com/photo/2016/11/11/23/34/cat-1817970_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/12/21/12/26/glowworm-3031704_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/12/24/09/09/road-3036620_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/11/07/00/07/fantasy-2925250_960_720.jpg",
            "https://cdn.pixabay.com/photo/2017/10/10/15/28/butterfly-2837589_960_720.jpg"
    };

    public EventFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        tvfrgEventName = (TextView) view.findViewById(R.id.tvfrgEventName);
        tvfrgEventDescription = (TextView) view.findViewById(R.id.tvfrgEventDescription);
        tvfrgLocation = (TextView) view.findViewById(R.id.tvfrgLocation);
        tvfrgEventDate = (TextView) view.findViewById(R.id.tvfrgEventDate);



        tvCancel = (TextView) view.findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Fragment fragment:getActivity().getSupportFragmentManager().getFragments()) {
                    getActivity().getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                }
            }
        });


        Bundle bundle = getArguments();
        int position = bundle.getInt("position", -1);

        if (position != -1) {
            String eventName = ActivityHome.eventList.get(position).getEventname();
            String eventDescription = ActivityHome.eventList.get(position).getDescription();
            String eventDate = ActivityHome.eventList.get(position).getDate();
            String eventLocation = ActivityHome.eventList.get(position).getLocation();

            tvfrgEventName.setText(eventName);
            tvfrgEventDescription.setText(eventDescription);
            tvfrgEventDate.setText(eventDate);
            tvfrgLocation.setText(eventLocation);
        }

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vpfrgPictures);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getContext(), imageUrls);
        viewPager.setAdapter(adapter);

        return view;
    }

}
