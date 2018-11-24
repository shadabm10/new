package gropherapp.gropher.com.gropherapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class Reset_Password extends AppCompatActivity {
    EditText edt_otp, edt_new_pass, edt_confirm_pass;
    TextView txtview_reset_pass;
    RelativeLayout rl_submit_btn;
    ImageView login_img;
    String TAG = "reset";
    String id;
    GlobalClass globalClass;


    ProgressDialog pd;
    // Global_class global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        globalClass = (GlobalClass)getApplicationContext();

        pd=new ProgressDialog(Reset_Password.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getResources().getString(R.string.loading));

        edt_otp = findViewById(R.id.edt_otp);
        edt_new_pass = findViewById(R.id.edt_new_pass);
        edt_confirm_pass = findViewById(R.id.edt_confirm_pass);

        login_img = findViewById(R.id.login_img);

        id=getIntent().getStringExtra("id");





        login_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               /* Intent intent = new Intent(Reset_Password.this,Login_Screen.class);
                startActivity(intent);
                finish();*/
               if(globalClass.isNetworkAvailable()) {

                   if (!edt_otp.getText().toString().trim().isEmpty()) {
                       if (!edt_new_pass.getText().toString().trim().isEmpty()) {
                           if (!edt_confirm_pass.getText().toString().trim().isEmpty()) {
                               if (edt_new_pass.getText().toString().trim().equals(edt_confirm_pass.getText().toString().trim())) {
                                   if (edt_new_pass.getText().toString().length() >= 6) {
                                       resetpass_url();
                                   } else {
                                       Toasty.error(Reset_Password.this, getResources().getString(R.string.minimum_length), Toast.LENGTH_SHORT, true).show();
                                   }
                               } else {
                                   Toasty.error(Reset_Password.this, getResources().getString(R.string.password_mismatch), Toast.LENGTH_SHORT, true).show();
                               }
                           } else {
                               Toasty.warning(Reset_Password.this, getResources().getString(R.string.renter_password), Toast.LENGTH_SHORT, true).show();
                           }
                       } else {
                           Toasty.warning(Reset_Password.this, getResources().getString(R.string.enter_new_password), Toast.LENGTH_SHORT, true).show();
                       }
                   } else {
                       Toasty.warning(Reset_Password.this, getResources().getString(R.string.enter_code), Toast.LENGTH_SHORT, true).show();
                   }
               }else{ Toasty.info(Reset_Password.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG, true).show();}
            }
        });


    }

    private void resetpass_url() {
        pd.show();
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.resetpass, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.resetpass);
                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {


                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");


                    Log.d("TAG", "status :\t" + status);
                    Log.d("TAG", "message :\t" + message);

                    if (status.equals("1")) {



                        Toasty.success(Reset_Password.this, getResources().getString(R.string.password_changed), Toast.LENGTH_SHORT, true).show();
                        Intent intent = new Intent(Reset_Password.this, LoginScreen.class);
                        startActivity(intent);
                        finish();
                        //pd.dismiss();

                    }else{

                            Toasty.error(Reset_Password.this, message, Toast.LENGTH_LONG, true).show();


                      //  pd.dismiss();

                    }
                    pd.dismiss();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "resetpass Error: " + error.getMessage());
                Toast.makeText(Reset_Password.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("otp_code",edt_otp.getText().toString());
                params.put("new_password",edt_new_pass.getText().toString());
                params.put("id",id);


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
        super.onBackPressed();


        finish();
    }
}
