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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.DrawerActivity;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.Shared_Preference;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class Signup extends AppCompatActivity {

    EditText edt_name,edt_phone,edt_email,edt_password;

    GlobalClass globalClass;
    Shared_Preference prefrence;
    ProgressDialog pd;
    String TAG = "Signup";
    ImageView img_proceed, img_eye_2;
    String device_id;
    String fcm_token;
    TextView tv_termsncondition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        globalClass = (GlobalClass)getApplicationContext();
        prefrence = new Shared_Preference(Signup.this);
        pd=new ProgressDialog(Signup.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");



        device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "device_id: "+device_id);
        globalClass.setDeviceid(device_id);

        //fcm_token = FirebaseInstanceId.getInstance().getToken();
        //Log.d(TAG, "fcm_token: "+fcm_token);

        edt_name = findViewById(R.id.edt_name);
        edt_phone = findViewById(R.id.edt_phone);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);

        img_proceed = findViewById(R.id.img_proceed);
        tv_termsncondition = findViewById(R.id.tv_termsncondition);




        img_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          /*      Intent intent = new Intent(Signup.this, HomeScreen.class);
                startActivity(intent);
                finish();*/



                if(globalClass.isNetworkAvailable()){
                    if(!edt_name.getText().toString().isEmpty()) {
                        if (!edt_phone.getText().toString().isEmpty()) {
                            if (!edt_email.getText().toString().isEmpty()) {
                                if (isValidEmail(edt_email.getText().toString())) {
                                    if (!edt_password.getText().toString().isEmpty()) {


                                        String name = edt_name.getText().toString();
                                        String phone = edt_phone.getText().toString();
                                        String email = edt_email.getText().toString();
                                        String password = edt_password.getText().toString();

                                        registration_url(name,phone,email,password,device_id,WebserviceUrl.fcm_token,globalClass.device_type);

                                    } else {
                                        Toasty.warning(Signup.this, "Please enter password.", Toast.LENGTH_SHORT, true).show();}
                                } else {
                                    Toasty.warning(Signup.this, "Please enter valid email.", Toast.LENGTH_SHORT, true).show();}
                            } else {
                                Toasty.warning(Signup.this, "Please enter email.", Toast.LENGTH_SHORT, true).show();}
                        } else {
                            Toasty.warning(Signup.this, "Please enter phone number.", Toast.LENGTH_SHORT, true).show();}
                    }else{
                        Toasty.warning(Signup.this, "Please enter name", Toast.LENGTH_SHORT, true).show();}
                }else {
                        Toasty.info(Signup.this, "please check your internet connection", Toast.LENGTH_SHORT, true).show();
                }

        }
        });

        tv_termsncondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this,TermsNCondition.class);
                startActivity(intent);
            }
        });



    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    private void registration_url(final String name, final String phone, final String email,
                                  final String password , final String device_id,
                                  final String fcm_token , final String device_type) {
        // Tag used to cancel the request
        String tag_string_req = "req_signup";
        pd.show();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.signup, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: "+ WebserviceUrl.login);
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
                        //String name = jsonObject.get("name").toString().replaceAll("\"", "");


                        globalClass.setId(uid);
                        globalClass.setName(name);
                        globalClass.setPhone_number(mobile);
                        globalClass.setEmail(email);
                        globalClass.setLogin_status(true);

                        prefrence.savePrefrence();

                        Intent intent = new Intent(Signup.this, DrawerActivity.class);
                        startActivity(intent);
                        finish();

                        pd.dismiss();
                    }else{
                        Toasty.error(Signup.this, message, Toast.LENGTH_LONG, true).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sign_up Error: " + error.getMessage());
                Toast.makeText(Signup.this, error.getMessage(), Toast.LENGTH_LONG).show();
                //  hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", name);
                params.put("mobile",phone);
                params.put("email", email);
                params.put("password", password);
                params.put("device_id", device_id);
                params.put("fcm_reg_token", fcm_token);
                params.put("device_type", device_type);

                Log.d(TAG, "getParams: "+params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }



/*    public void registration_url(){

        pd.show();

        String url = WebserviceUrl.user_signup;
        AsyncHttpClient cl = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("first_name",input_fname.getText().toString());
        params.put("last_name",input_lname.getText().toString());
        params.put("email_id",input_email.getText().toString());
        params.put("password",input_password.getText().toString());
        params.put("mobile",input_mobile.getText().toString());
        params.put("device_type",globalClass.device_type);
        params.put("device_id",globalClass.getDeviceid());
        params.put("fcm_token",fcm_token);




        Log.d(TAG , "URL "+url);
        Log.d(TAG , "params "+params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        cl.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        cl.post(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (response != null) {
                    Log.d(TAG, "registration_url- " + response.toString());
                    try {

                       // JSONObject result = response.getJSONObject("result");

                        int status = response.getInt("status");
                        String message = response.getString("message");


                        Log.d(TAG, "onSuccess:status>>>> "+status);
                        Log.d(TAG, "onSuccess:message>>>> "+message);



                        if (status == 1) {

                            JSONObject data= response.getJSONObject("data");
                            Log.d(TAG, "onSuccess:data1>>>> "+data);

                            String id = data.getString("id");
                            String first_name = data.getString("first_name");
                            String last_name = data.getString("last_name");
                            String mobile = data.getString("mobile");
                            String email = data.getString("email");
                            String profile_pic = data.getString("profile_pic");
                            String address = data.getString("address");

                            //   String name = fname+" "+lname;

                            globalClass.setId(id);
                            globalClass.setFname(first_name);
                            globalClass.setLname(last_name);
                            globalClass.setPhone_number(mobile);
                            globalClass.setEmail(email);
                            globalClass.setLogin_status(true);
                            globalClass.setProfil_pic(profile_pic);
                            globalClass.setShipping_full_address(address);
                            prefrence.savePrefrence();



                            Toasty.success(Signup.this, getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(Signup.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                            pd.dismiss();



                        }else if (status == 0){

                            Toasty.error(Signup.this, getResources().getString(R.string.already_registered), Toast.LENGTH_SHORT, true).show();
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
                Log.d(TAG, "onFailure: "+responseString);
            }
        });


    }*/


}
