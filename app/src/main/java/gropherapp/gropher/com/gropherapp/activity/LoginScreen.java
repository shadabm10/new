package gropherapp.gropher.com.gropherapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.DrawerActivity;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.Shared_Preference;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class LoginScreen extends AppCompatActivity{

    String TAG = "login";
    GlobalClass globalClass;
    ProgressDialog pd;
    Shared_Preference prefrence;
    TextView  signup_tv,tv_f_password;
    EditText email_edt, password_edt;
    String device_id;
    String fcm_token;
    ImageView login_img;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        globalClass = (GlobalClass) getApplicationContext();
        prefrence = new Shared_Preference(LoginScreen.this);
        prefrence.loadPrefrence();

/*

        if(globalClass.getLogin_status().equals(true)){
            Intent intent = new Intent(LoginScreen.this, DrawerActivity.class);
            startActivity(intent);
            finish();
        }else {

        }
*/

        pd = new ProgressDialog(LoginScreen.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");

        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "device_id: " + device_id);
        globalClass.setDeviceid(device_id);

       // fcm_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "fcm_token: " + fcm_token);




        email_edt = findViewById(R.id.edt_email);
        password_edt = findViewById(R.id.edt_password);
        login_img = findViewById(R.id.login_img);
        signup_tv = findViewById(R.id.tv_signup);
        tv_f_password = findViewById(R.id.tv_f_password);




        login_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent intent = new Intent(LoginScreen.this, DrawerActivity.class);
                startActivity(intent);*/

                if (globalClass.isNetworkAvailable()) {
                    if (!email_edt.getText().toString().isEmpty()) {
                        if (isValidEmail(email_edt.getText().toString())) {
                            if (!password_edt.getText().toString().isEmpty()) {

                                String email = email_edt.getText().toString();
                                String password = password_edt.getText().toString();


                                login_url(email, password, device_id, WebserviceUrl.fcm_token, globalClass.device_type);
                            } else {
                                Toasty.warning(LoginScreen.this, "Please enter password.", Toast.LENGTH_SHORT, true).show();
                            }
                        } else {
                            Toasty.warning(LoginScreen.this, "Please enter valid email.", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        Toasty.warning(LoginScreen.this, "Please enter email.", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.info(LoginScreen.this, "please check your internet connection", Toast.LENGTH_SHORT, true).show();
                }

            }
        });

        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this, Signup.class);
                startActivity(intent);
            }
        });

        tv_f_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this, Forget_Password.class);
                startActivity(intent);
            }
        });



    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void login_url(final String email, final String password, final String device_id,
                           final String fcm_token, final String device_type) {
        // Tag used to cancel the request
        pd.show();
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.login, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.login);
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

                        JsonObject jsonObject = jobj.getAsJsonObject("info");

                        String uid = jsonObject.get("uid").toString().replaceAll("\"", "");
                        String name = jsonObject.get("name").toString().replaceAll("\"", "");
                        String mobile = jsonObject.get("mobile").toString().replaceAll("\"", "");
                        String email = jsonObject.get("email").toString().replaceAll("\"", "");
                        String image = jsonObject.get("image").toString().replaceAll("\"", "");


                        globalClass.setId(uid);
                        globalClass.setName(name);
                        globalClass.setPhone_number(mobile);
                        globalClass.setEmail(email);
                        globalClass.setProfil_pic(image);
                        globalClass.setLogin_status(true);

                        prefrence.savePrefrence();

                        Intent intent = new Intent(LoginScreen.this, DrawerActivity.class);
                        startActivity(intent);
                        finish();

                        Toasty.success(LoginScreen.this,message, Toast.LENGTH_LONG).show();

                    }else {

                        Toasty.error(LoginScreen.this,message, Toast.LENGTH_LONG).show();
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
                //Toast.makeText(LoginScreen.this, error.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("email", email);
                params.put("password", password);
                params.put("device_id", device_id);
                params.put("fcm_reg_token", fcm_token);
                params.put("device_type", device_type);

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
