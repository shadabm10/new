package gropherapp.gropher.com.gropherapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public  class OtpScreen extends AppCompatActivity {

    String TAG = "otp";
    GlobalClass globalClass;
    ProgressDialog pd;
    TextView tv_place_order;
    String order_id;
    EditText edt_otp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen);

        globalClass = (GlobalClass) getApplicationContext();
        pd = new ProgressDialog(OtpScreen.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");

        ImageView img_back = findViewById(R.id.toolbar_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        order_id = getIntent().getStringExtra("order_id");

        edt_otp = findViewById(R.id.edt_otp);
        tv_place_order = findViewById(R.id.tv_place_order);

        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = edt_otp.getText().toString();
                otp_url(otp);


            }
        });
    }

    private void otp_url(final String otp) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
     //   pd.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.confirm_otp, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.confirm_otp);
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

                        Intent intent = new Intent(OtpScreen.this,TrackOrderScreen.class);
                        startActivity(intent);

                      //  pd.dismiss();

                    }else {

                        Toast.makeText(OtpScreen.this,"Something went wrong", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "confirm_otp Error: " + error.getMessage());
                Toast.makeText(OtpScreen.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",globalClass.getId());
                params.put("order_id",order_id);
                params.put("otp",otp);

                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }
}
