package gropherapp.gropher.com.gropherapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import gropherapp.gropher.com.gropherapp.DrawerActivity;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class Forget_Password extends AppCompatActivity {

    String TAG = "f_pass";
    ImageView login_img;

    ProgressDialog pd;
    EditText edt_email;
    GlobalClass globalClass;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.forgot_password);
        globalClass = (GlobalClass)getApplicationContext();

        pd=new ProgressDialog(Forget_Password.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");


        edt_email  = findViewById(R.id.edt_email);
        login_img = findViewById(R.id.login_img);




        login_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(Forget_Password.this, Reset_Password.class);
                startActivity(intent);
                finish();*/
                    if(globalClass.isNetworkAvailable()) {
                        if (!edt_email.getText().toString().trim().isEmpty()) {
                            if (isValidEmail(edt_email.getText().toString())) {

                                forgetpass_url( edt_email.getText().toString());
                            } else {
                                Toasty.error(Forget_Password.this, getResources().getString(R.string.valid_email), Toast.LENGTH_LONG, true).show();
                            }
                        } else {
                            Toasty.warning(Forget_Password.this, getResources().getString(R.string.enter_email), Toast.LENGTH_LONG, true).show();
                        }
                    }else{ Toasty.info(Forget_Password.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG, true).show();}
                }

        });
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    private void forgetpass_url(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.forgetpass, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.forgetpass);
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

                        String id = jobj.get("id").toString().replaceAll("\"", "");
                        String otp_code = jobj.get("otp_code").toString().replaceAll("\"", "");


                        Toasty.success(Forget_Password.this, getResources().getString(R.string.otp_send), Toast.LENGTH_SHORT, true).show();
                        Intent intent = new Intent(Forget_Password.this, Reset_Password.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                        finish();
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
                Toast.makeText(Forget_Password.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("email", email);

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
