package gropherapp.gropher.com.gropherapp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.adapter.AdapterHistory;
import gropherapp.gropher.com.gropherapp.adapter.AdapterRecentOrder;
import gropherapp.gropher.com.gropherapp.adapter.AdapterReview;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.Shared_Preference;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class FragmentReview extends Fragment {

    String TAG = "review";
    GlobalClass globalClass;
    ProgressDialog pd;
    Shared_Preference prefrence;

    ArrayList<HashMap<String,String>> arr_recent_order;

    RecyclerView rv_review;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_review, container, false);

        globalClass = (GlobalClass) getActivity().getApplicationContext();

        pd=new ProgressDialog(getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getResources().getString(R.string.loading));

        arr_recent_order = new ArrayList<>();


        rv_review = view.findViewById(R.id.rv_review);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rv_review.setLayoutManager(mLayoutManager);

        order_history_url();

        return view;
    }
    private void order_history_url() {
        // Tag used to cancel the request


        String tag_string_req = "req_login";

        pd.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.review_list, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                //   Log.d(TAG, "url_hit: " + WebserviceUrl.order_history);
                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {


                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");


                    Log.d("TAG", "status :\t" + status);
                    Log.d("TAG", "message :\t" + message);

                    if(status.equals("1")) {

                        arr_recent_order.clear();

                        JsonArray jsonArray =jobj.getAsJsonArray("order_list");
                        for(int i=0; i<jsonArray.size();i++) {

                            JsonObject jObject = (JsonObject) jsonArray.get(i);

                            String id = jObject.get("id").toString().replaceAll("\"", "");
                            String customer_id = jObject.get("customer_id").toString().replaceAll("\"", "");
                            String deliveryboy_id = jObject.get("deliveryboy_id").toString().replaceAll("\"", "");
                            String product_name = jObject.get("product_name").toString().replaceAll("\"", "");
                            String order_placed_on = jObject.get("order_placed_on").toString().replaceAll("\"", "");
                            String review = jObject.get("review").toString().replaceAll("\"", "");
                            String rating = jObject.get("rating").toString().replaceAll("\"", "");
                            String app_rating = jObject.get("app_rating").toString().replaceAll("\"", "");
                            String fname = jObject.get("fname").toString().replaceAll("\"", "");
                            String lname = jObject.get("lname").toString().replaceAll("\"", "");


                            HashMap<String, String> map_ser = new HashMap<String, String>();
                            map_ser.put("id", id);
                            map_ser.put("customer_id", customer_id);
                            map_ser.put("deliveryboy_id", deliveryboy_id);
                            map_ser.put("product_name", product_name);
                            map_ser.put("review", review);
                            map_ser.put("rating", rating);
                            map_ser.put("app_rating", app_rating);
                            map_ser.put("order_placed_on", order_placed_on);
                            map_ser.put("fname", fname);
                            map_ser.put("lname", lname);

                            arr_recent_order.add(map_ser);


                        }

                        Log.d(TAG, "onResponse: p_arr:  "+arr_recent_order);
                        AdapterReview adapterReview = new AdapterReview(getActivity(), arr_recent_order);
                        rv_review.setAdapter(adapterReview);
                        adapterReview.notifyDataSetChanged();

                        pd.dismiss();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "order_history Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",globalClass.getId());

                Log.d(TAG, "getParams: " + params);
                Log.d(TAG, "url_hit: " + WebserviceUrl.review_list);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }
}
