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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.activity.PlaceOrder;
import gropherapp.gropher.com.gropherapp.activity.RatingScreen;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class FragmentStatistic extends Fragment {
    TextView tv_place_order;
    GlobalClass globalClass;
    ProgressDialog pd;
    String TAG = "statics";
    TextView tv_total_order_placed,tv_order_delivered,tv_rating_given;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);


        globalClass = (GlobalClass) getActivity().getApplicationContext();

        pd=new ProgressDialog(getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");

        tv_place_order = view.findViewById(R.id.tv_place_order);
        tv_total_order_placed = view.findViewById(R.id.tv_total_order_placed);
        tv_order_delivered = view.findViewById(R.id.tv_order_delivered);
        tv_rating_given = view.findViewById(R.id.tv_rating_given);

        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PlaceOrder.class);
                startActivity(intent);
            }
        });

        if(globalClass.isNetworkAvailable()){
            get_statistics_url();
        }

        return view;
    }

    private void get_statistics_url() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pd.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.get_statistics, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.get_statistics);
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


                        JsonObject statistics_list = jobj.getAsJsonObject("statistics_list");

                        String total_orders_placed = statistics_list.get("total_orders_placed").toString().replaceAll("\"", "");
                        String total_orders_completed = statistics_list.get("total_orders_completed").toString().replaceAll("\"", "");
                        String total_review_list = statistics_list.get("total_review_list").toString().replaceAll("\"", "");

                        tv_total_order_placed.setText(total_orders_placed);
                        tv_order_delivered.setText(total_orders_completed);
                        tv_rating_given.setText(total_review_list);

                        //Toasty.success(getActivity(),message, Toast.LENGTH_SHORT, true).show();

                    }else{
                        Toasty.error(getActivity(), message, Toast.LENGTH_SHORT, true).show();

                    }
                    pd.dismiss();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "forgetpass Error: " + error.getMessage());
                //  Toast.makeText(RatingScreen.this, error.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
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
