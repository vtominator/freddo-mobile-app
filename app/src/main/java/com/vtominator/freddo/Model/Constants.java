package com.vtominator.freddo.Model;

public class Constants {
    private static final String ROOT_URL = "http://192.168.1.128/freddo/v1/";

    public static final String URL_REGISTER = ROOT_URL + "registerUser.php";
    public static final String URL_DELETEUSER = ROOT_URL + "deleteUser.php";
    public static final String URL_LOGIN = ROOT_URL + "userLogin.php";
    public static final String URL_CHANGEPASSWORD = ROOT_URL + "changePassword.php";
    public static final String URL_CHANGEEMAIL = ROOT_URL + "changeEmail.php";

    public static final String URL_SETEVENTS = ROOT_URL + "addEvent.php";
    public static final String URL_GETALLEVENTS = ROOT_URL + "getAllEvents.php";
    public static final String URL_GETACTIVEEVENTS = ROOT_URL + "getActiveEvents.php";
    public static final String URL_GETINACTIVEEVENTS = ROOT_URL + "getInactiveEvents.php";

    public static final String URL_ACTIVATEEVENT = ROOT_URL + "activateEvent.php";
    public static final String URL_DECLINEEVENT = ROOT_URL + "declineEvent.php";
    public static final String URL_ISACTIVE = ROOT_URL + "isActive.php";
    public static final String URL_GETSTATUS = ROOT_URL + "getStatus.php";
    public static final String URL_ADDREASON = ROOT_URL + "addReason.php";
    public static final String URL_GETREASON = ROOT_URL + "getReason.php";
    public static final String URL_DELETEREASON = ROOT_URL + "deleteReason.php";


    public static final String URL_SETFAVORITE = ROOT_URL + "addFavorite.php";
    public static final String URL_GETFAVORITES = ROOT_URL + "getFavorites.php";
    public static final String URL_GETLIKES = ROOT_URL + "getLikes.php";
    public static final String URL_DELETEFAVORITE = ROOT_URL + "deleteFavorite.php";
    public static final String URL_GETMYEVENTS = ROOT_URL + "getMyEvents.php";
    public static final String URL_DELETEEVENT = ROOT_URL + "deleteEvent.php";


}
