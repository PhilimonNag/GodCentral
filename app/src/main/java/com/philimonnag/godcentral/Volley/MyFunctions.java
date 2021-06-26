package com.philimonnag.godcentral.Volley;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

public class MyFunctions {
    public  static  final String GET_BIBLE_VERSE ="https://labs.bible.org/api/?passage=votd&type=json";
    Context context;


    public MyFunctions(Context context) {
        this.context = context;
    }

    public MyFunctions() {

    }
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(JSONArray response);
    }
    public void getBibleVerse(VolleyResponseListener volleyResponseListener) {
        RequestQueue requestQueue;
        String url=GET_BIBLE_VERSE;
        Cache cache = new DiskBasedCache(context.getCacheDir(),1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               volleyResponseListener.onResponse(response);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyResponseListener.onError(error.toString());
                //Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);



    }
}
