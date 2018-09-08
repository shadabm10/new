package gropherapp.gropher.com.gropherapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;

/**
 * Created by Developer on 9/5/18.
 */

public class RatingScreen extends AppCompatActivity {
    
    RatingBar rating1,rating;
    LinearLayout ll_rate_delivery_boy,ll_rate_app;
    ImageView img_back;
    TextView tv_submit;
    GlobalClass globalClass;
    ProgressDialog pd;
    String TAG = "rating";
    
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_screen);

        globalClass = (GlobalClass)getApplicationContext();

        pd=new ProgressDialog(RatingScreen.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");


        img_back = findViewById(R.id.toolbar_back);
        tv_submit = findViewById(R.id.tv_submit);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        rating1 = findViewById(R.id.rating1);
        rating = findViewById(R.id.rating);
        ll_rate_delivery_boy = findViewById(R.id.ll_rate_delivery_boy);
        ll_rate_app = findViewById(R.id.ll_rate_app);



        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( globalClass.isNetworkAvailable()){

                   String rate_1 = String.valueOf(rating.getRating());
                   String rate_2 = String.valueOf(rating1.getRating());
                   if(rating.getRating()>0){
                       if(rating1.getRating()>0){
                           review_url(rate_1,rate_2);
                       }else{
                           Toasty.warning(RatingScreen.this,"Please rate our app.", Toast.LENGTH_SHORT, true).show();
                       }
                   }else{
                       Toasty.warning(RatingScreen.this,"Please rate delivery boy.", Toast.LENGTH_SHORT, true).show();
                   }

                }
            }
        });
    }
    private void review_url(final String rate_1, final String rate_2) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pd.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.review, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.review);
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

                        Toasty.success(RatingScreen.this,message, Toast.LENGTH_SHORT, true).show();

                    }else{
                        Toasty.error(RatingScreen.this, message, Toast.LENGTH_SHORT, true).show();

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
                params.put("deliveryboy_id", getIntent().getStringExtra("deliveryboy_id"));
                params.put("rating",    rate_1);
                params.put("app_rating", rate_2);
                params.put("order_id", getIntent().getStringExtra("order_id"));

                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }

}
