package gropherapp.gropher.com.gropherapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.adapter.AdapterRecentOrder;
import gropherapp.gropher.com.gropherapp.fragment.FragmentWallet;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;

/**
 * Created by Developer on 8/28/18.
 */

public class PayMoreMoney extends AppCompatActivity {

    TextView tv_product_name,tv_product_qty,tv_new_amount,tv_total_amount,
            tv_required_amount,tv_dont_pay,tv_pay;
    ImageView imageView2;
    GlobalClass globalClass;
    ProgressDialog pd;
    String TAG = "pay_more";

    Fragment fragment = null;
    RelativeLayout rl_container;

    ImageLoader loader;
    DisplayImageOptions defaultOptions;
    ImageView toolbar_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_more_money);

        initialisation();
        function();
        view_requested_money();
    }


    private void initialisation() {

        globalClass = (GlobalClass) getApplicationContext();
        pd = new ProgressDialog(PayMoreMoney.this);

        tv_product_name = findViewById(R.id.tv_product_name);
        tv_product_qty = findViewById(R.id.tv_product_qty);
        tv_new_amount = findViewById(R.id.tv_new_amount);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        tv_required_amount = findViewById(R.id.tv_required_amount);
        imageView2 = findViewById(R.id.imageView2);
        tv_dont_pay = findViewById(R.id.tv_dont_pay);
        tv_pay = findViewById(R.id.tv_pay);
        rl_container = findViewById(R.id.rl_container);
        toolbar_back = findViewById(R.id.toolbar_back);
    }

    private void function() {

        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");


        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                //  .showImageOnLoading(R.mipmap.loading_black128px)
                //  .showImageForEmptyUri(R.mipmap.no_image)
                //  .showImageOnFail(R.mipmap.no_image)
                //  .showImageOnFail(R.mipmap.img_failed)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(PayMoreMoney.this.getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
        loader = ImageLoader.getInstance();

        tv_product_name.setText(getIntent().getStringExtra("p_name"));


        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_dont_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept_more_money_request("N");
            }
        });

        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                accept_more_money_request("Y");
            }
        });


    }

    private void view_requested_money() {
        pd.show();
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.view_requested_money, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {


                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");

                    switch (status) {
                        case "1":

                            JsonObject job_info = jobj.getAsJsonObject("job_info");
                            String product_quantity = job_info.get("product_quantity").toString().replaceAll("\"", "");
                            String new_product_amount = job_info.get("new_product_amount").toString().replaceAll("\"", "");
                            String new_total_amount = job_info.get("new_total_amount").toString().replaceAll("\"", "");
                            String required_amount = job_info.get("required_amount").toString().replaceAll("\"", "");
                            String image = job_info.get("image").toString().replaceAll("\"", "");



                            tv_product_qty.setText(product_quantity);
                            tv_new_amount.setText("$"+new_product_amount);
                            tv_total_amount.setText("$"+new_total_amount);
                            tv_required_amount.setText("$"+required_amount);

                            if(image.isEmpty() || image.equals("")){
                                imageView2.setImageResource(R.mipmap.no_image);
                            }else{

                                loader.displayImage(image, imageView2 , defaultOptions);
                            }


                            break;


                        default:

                            Toasty.warning(PayMoreMoney.this, message, Toast.LENGTH_SHORT, true).show();
                            break;
                    }



                    pd.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "login Error: " + error.getMessage());
                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        })

        {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


             //   params.put("id", globalClass.getId());
                params.put("job_id", getIntent().getStringExtra("id"));


                Log.d(TAG, "url hit: " + WebserviceUrl.view_requested_money);
                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }

    private void accept_more_money_request(final String agree) {
        pd.show();
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.accept_more_money_request, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {


                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");

                    switch (status) {
                        case "1":

                            Toasty.success(PayMoreMoney.this, message, Toast.LENGTH_SHORT, true).show();
                            finish();
                            break;

                        case "2":

                            Toasty.error(PayMoreMoney.this, message, Toast.LENGTH_SHORT, true).show();
                            break;


                        case "3":

                            Toasty.info(PayMoreMoney.this, "Please Add Amount To Your Wallet", Toast.LENGTH_SHORT, true).show();
                            rl_container.setVisibility(View.VISIBLE);
                            fragment = new FragmentWallet();
                            FragmentManager fragmentManager2 = getSupportFragmentManager();
                            fragmentManager2.beginTransaction().replace(R.id.flContent, fragment).commit();

                            break;
                        default:

                            Toasty.warning(PayMoreMoney.this, message, Toast.LENGTH_SHORT, true).show();
                            break;
                    }



                    pd.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "login Error: " + error.getMessage());
                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        })

        {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("id", globalClass.getId());
                params.put("job_id", getIntent().getStringExtra("id"));
                params.put("agree", agree);

                Log.d(TAG, "url hit: " + WebserviceUrl.accept_more_money_request);
                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }

    @Override
    public void onBackPressed() {

        if(rl_container.getVisibility()==View.VISIBLE){
            rl_container.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

}
