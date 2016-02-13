package com.example.vraja03.newsearch.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by VRAJA03 on 2/13/2016.
 */
public class NetworkConnection {

    public static boolean checkInternetConn(Context ctx){

        ConnectivityManager conMgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() == null
                || (!conMgr.getActiveNetworkInfo().isAvailable())
                || (!conMgr.getActiveNetworkInfo().isConnected())) {
            return false; //if there is no internet connection
        }
        return true;
    }
}
