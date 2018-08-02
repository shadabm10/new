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


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "login Error: " + error.getMessage());
                Toast.makeText(LoginScreen.this, error.getMessage(), Toast.LENGTH_LONG).show();

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


            /* public void user_login_url() {

                 pd.show();

                 String url = WebserviceUrl.login;
                 AsyncHttpClient cl = new AsyncHttpClient();
                 RequestParams params = new RequestParams();


                 params.put("email", email_edt.getText().toString());
                 params.put("password", password_edt.getText().toString());
                 params.put("device_type", globalClass.device_type);
                 params.put("device_id", globalClass.getDeviceid());
                 params.put("fcm_token", fcm_token);


                 Log.d(TAG, "URL " + url);
                 Log.d(TAG, "params " + params.toString());

                 int DEFAULT_TIMEOUT = 30 * 1000;
                 cl.setMaxRetriesAndTimeout(5, DEFAULT_TIMEOUT);
                 cl.post(url, params, new JsonHttpResponseHandler() {
                     @Override
                     public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                         if (response != null) {
                             Log.d(TAG, "registration_url- " + response.toString());
                             try {

                                 // JSONObject result = response.getJSONObject("result");

                                 int status = response.getInt("status");
                                 String message = response.getString("message");


                                 Log.d(TAG, "onSuccess:status>>>> " + status);
                                 Log.d(TAG, "onSuccess:message>>>> " + message);


                                 if (status == 1) {
                                     String address_full;
                                     String address_id = "";

                                     JSONObject data = response.getJSONObject("data");
                                     Log.d(TAG, "onSuccess:data1>>>> " + data);

                                     String id = data.getString("id");
                                     String first_name = data.getString("first_name");
                                     String last_name = data.getString("last_name");
                                     String mobile = data.getString("mobile");
                                     String email = data.getString("email");
                                     String profile_pic = data.getString("profile_pic");

                                     JSONObject address_arr = data.optJSONObject("address");
                                     if( data.optJSONObject("address") == null){
                                         address_full = "";
                                     }else {

                                         address_id = address_arr.optString("id");
                                         String address_first_name = address_arr.optString("fname");
                                         String address_last_name = address_arr.optString("lname");
                                         String address = address_arr.optString("address");
                                         String country = address_arr.optString("country");
                                         String city = address_arr.optString("city");
                                         String address_mobile = address_arr.optString("mobile");
                                         double lat = address_arr.optDouble("lat");
                                         double lng = address_arr.optDouble("lng");

                                         address_full = address_first_name + " " + address_last_name + "\n" + address + ", "
                                                 + city + ", " + country + "\n" + getResources().getString(R.string.mob) + " "
                                                 + address_mobile;

                                     }

                                     //   String name = fname+" "+lname;
                                     Log.d(TAG, "address_full: "+address_full);
                                     Log.d(TAG, "address_id: "+address_id);


                                     globalClass.setId(id);
                                     globalClass.setFname(first_name);
                                     globalClass.setLname(last_name);
                                     globalClass.setPhone_number(mobile);
                                     globalClass.setEmail(email);
                                     globalClass.setLogin_status(true);
                                     globalClass.setProfil_pic(profile_pic);
                                     globalClass.setLogin_from("signup");
                                     globalClass.setShipping_full_address(address_full);
                                     globalClass.setShipping_id(address_id);


                                     prefrence.savePrefrence();


                                     Toasty.success(LoginScreen.this, getResources().getString(R.string.login_successful), Toast.LENGTH_SHORT, true).show();
                                     Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                                     startActivity(intent);
                                     finish();
                                     pd.dismiss();


                                 } else if(status == 0){
                                     if(message.equals("You are not registered.")) {
                                         Toasty.error(LoginScreen.this, getResources().getString(R.string.not_registered), Toast.LENGTH_SHORT, true).show();
                                     }else  if(message.equals("Password incorrect.")){
                                         Toasty.error(LoginScreen.this, getResources().getString(R.string.password_incorrect), Toast.LENGTH_SHORT, true).show();
                                     }

                                     pd.dismiss();

                                 }

                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }

                         }


                     }

                     @Override
                     public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                         super.onFailure(statusCode, headers, responseString, throwable);
                         Log.d(TAG, "onFailure: " + responseString);
                     }
                 });


             }
         */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }





}
