package gropherapp.gropher.com.gropherapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
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


public class TermsNCondition extends AppCompatActivity {
    String TAG = "terms";
    TextView tv_privacy,tv_terms;
    ImageView toolbar_back;
    GlobalClass globalClass;
    Shared_Preference prefrence;
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.termsncondtion);

        globalClass = (GlobalClass)getApplicationContext();
        prefrence = new Shared_Preference(TermsNCondition.this);
        pd=new ProgressDialog(TermsNCondition.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");

        tv_terms = findViewById(R.id.tv_terms);
        tv_privacy = findViewById(R.id.tv_privacy);
        toolbar_back = findViewById(R.id.toolbar_back);


        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cms_details_url();
    }

    private void cms_details_url() {
        // Tag used to cancel the request
        String tag_string_req = "req_TermsNCondition";
        pd.show();


        StringRequest strReq = new StringRequest(Request.Method.GET,
                WebserviceUrl.cms_details, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: "+ WebserviceUrl.cms_details);
                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {


                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");

                    if(status.equals("1")) {

                        String term_n_condition = jobj.get("term_n_condition").toString().replaceAll("\"", "");
                        String privacy_policy = jobj.get("privacy_policy").toString().replaceAll("\"", "");
                        String term = Html.fromHtml(term_n_condition).toString();
                        String privacy = Html.fromHtml(privacy_policy).toString();
                        tv_terms.setText(term);
                        tv_privacy.setText(privacy);

                    }else {

                        Toasty.error(TermsNCondition.this,message,Toast.LENGTH_LONG).show();
                    }

                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sign_up Error: " + error.getMessage());
              //  Toast.makeText(TermsNCondition.this, error.getMessage(), Toast.LENGTH_LONG).show();
                //  hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                Log.d(TAG, "getParams: "+params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }
}
