package gropherapp.gropher.com.gropherapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gropherapp.gropher.com.gropherapp.DrawerActivity;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.activity.LoginScreen;
import gropherapp.gropher.com.gropherapp.adapter.AdapterRecentOrder;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.Shared_Preference;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class FragmentMyOrder extends Fragment {

    String TAG = "recent";
    GlobalClass globalClass;
    ProgressDialog pd;
    Shared_Preference prefrence;
    ListView lv_recent_order;
    ArrayList<HashMap<String,String>> arr_recent_order;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_recent_order, container, false);

        globalClass = (GlobalClass) getActivity().getApplicationContext();
        prefrence = new Shared_Preference(getActivity());
        prefrence.loadPrefrence();


        arr_recent_order = new ArrayList<>();
        lv_recent_order = view.findViewById(R.id.lv_recent_order);

        if(globalClass.isNetworkAvailable()){
            recent_order_url();
        }
        return view;
    }

    private void recent_order_url() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.recent_orders, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.recent_orders);
                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {

                    arr_recent_order.clear();
                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");


                    Log.d("TAG", "status :\t" + status);
                    Log.d("TAG", "message :\t" + message);

                 //   JsonObject jsonObject = jobj.getAsJsonObject("info");

                    JsonArray jsonArray =jobj.getAsJsonArray("info");
                    for(int i=0; i<jsonArray.size();i++) {

                        JsonObject jObject = (JsonObject) jsonArray.get(i);

                        String id = jObject.get("id").toString().replaceAll("\"", "");
                        String name = jObject.get("name").toString().replaceAll("\"", "");
                        String image = jObject.get("image").toString().replaceAll("\"", "");
                        String shop_name = jObject.get("shop_name").toString().replaceAll("\"", "");
                        String instruction = jObject.get("instruction").toString().replaceAll("\"", "");
                        String address = jObject.get("address").toString().replaceAll("\"", "");
                        String order_placed_on = jObject.get("order_placed_on").toString().replaceAll("\"", "");


                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", id);
                        map.put("name", name);
                        map.put("image", image);
                        map.put("shop_name", shop_name);
                        map.put("instruction", instruction);
                        map.put("address", address);
                        map.put("order_placed_on", order_placed_on);

                        arr_recent_order.add(map);

                    }

                    AdapterRecentOrder adapterRecentOrder = new AdapterRecentOrder(getActivity(),arr_recent_order,pd);
                    lv_recent_order.setAdapter(adapterRecentOrder);
                    adapterRecentOrder.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "login Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("id", globalClass.getId());

                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }
}
