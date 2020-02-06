package com.vtominator.freddo.Model;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Comparator;
import java.util.Date;


public class Event {
    private static final String TAG = "Event";


    private int event_id;
    private String username;
    private String eventname;
    private String location;
    private String date;
    private String time;
    private String description;
    private int festival;
    private int concert;
    private int performance;
    private int event;
    private int deal;
    private int free;

    private int like;
    private String active;
    private String status;
    private String reason;
    private boolean favorite;


    public Event(int event_id, String username, String eventname, String location, String date, String time, String description, int festival, int concert, int performance, int event, int deal, int free) {
        this.event_id = event_id;
        this.username = username;
        this.eventname = eventname;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.festival = festival;
        this.concert = concert;
        this.performance = performance;
        this.event = event;
        this.deal = deal;
        this.free = free;
    }


    public static long getTimeLeft(Event currentEvent) {
        long diff;

        String pattern = "yyyy-MM-dd HH:mm:ss";
        String date = currentEvent.getDate()+" "+currentEvent.getTime();


        Date today = new Date();

        try {
            DateFormat df = new SimpleDateFormat(pattern);
            Date eventDay = df.parse(date);

            Log.d(TAG, "getTimeLeft: "+eventDay);
            diff = eventDay.getTime() - today.getTime();
            return diff;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static final Comparator<Event> BY_DATE = new Comparator<Event>() {

        @Override
        public int compare(Event event, Event o2) {
            if (getTimeLeft(event) < getTimeLeft(o2)) return -1;
            else if (getTimeLeft(event) > getTimeLeft(o2)) return 1;
            return 0;
        }
    };
    public static final Comparator<Event> BY_DATE_DESC = new Comparator<Event>() {

        @Override
        public int compare(Event event, Event o2) {
            if (getTimeLeft(event) < getTimeLeft(o2)) return 1;
            else if (getTimeLeft(event) > getTimeLeft(o2)) return -1;
            return 0;
        }
    };

    public static final Comparator<Event> BY_NAME = new Comparator<Event>() {

        @Override
        public int compare(Event event, Event o2) {
            return event.getEventname().compareTo(o2.getEventname());
        }
    };

    public static final Comparator<Event> BY_LIKE = new Comparator<Event>() {

        @Override
        public int compare(Event event, Event o2) {
            if (event.getLike() < o2.getLike()) return -1;
            else if (event.getLike() > o2.getLike()) return 1;
            return 0;
        }
    };

    public static final Comparator<Event> BY_LIKE_DESC = new Comparator<Event>() {
        @Override
        public int compare(Event event, Event o2) {
            if (event.getLike() < o2.getLike()) return 1;
            else if (event.getLike() > o2.getLike()) return -1;
            return 0;
        }
    };

    public int getEvent_id() {
        return event_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEventname() {
        return eventname;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public int getFestival() {
        return festival;
    }

    public int getConcert() {
        return concert;
    }

    public int getPerformance() {
        return performance;
    }

    public int getEvent() {
        return event;
    }

    public int getDeal() {
        return deal;
    }

    public int getFree() {
        return free;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getLike() {
        return like;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getActive() {
        return active;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public boolean isFavorite(){return this.favorite;}

    public void setFavorite(boolean favorite){this.favorite = favorite;}

}
