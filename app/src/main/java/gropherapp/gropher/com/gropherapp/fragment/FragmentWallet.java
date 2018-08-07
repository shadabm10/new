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
import android.widget.EditText;
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
import gropherapp.gropher.com.gropherapp.activity.LoginScreen;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.Shared_Preference;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class FragmentWallet extends Fragment {
    EditText edt_amount;
    TextView add_tv;
    String TAG = "wallet";
    GlobalClass globalClass;
    Shared_Preference prefrence;
    ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        globalClass = (GlobalClass) getActivity().getApplicationContext();
        prefrence = new Shared_Preference(getActivity());
        prefrence.loadPrefrence();

        pd = new ProgressDialog(getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");


        edt_amount = view.findViewById(R.id.edt_amount);
        add_tv = view.findViewById(R.id.add_tv);


        add_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_to_wallet_url(edt_amount.getText().toString());
            }
        });
        return view;
    }

    private void add_to_wallet_url(final String amount) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pd.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.add_to_wallet, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.add_to_wallet);
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

                        String id = jsonObject.get("id").toString().replaceAll("\"", "");
                        String wallet_amount = jsonObject.get("wallet_amount").toString().replaceAll("\"", "");

                        globalClass.setWallet_balance(wallet_amount);

                        prefrence.savePrefrence();


                        Toasty.success(getActivity(), message, Toast.LENGTH_LONG).show();
                    }else{
                        Toasty.error(getActivity(), message, Toast.LENGTH_LONG).show();
                    }

                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "add_to_wallet Error: " + error.getMessage());
              //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("wallet_amount", amount);
                params.put("id",globalClass.getId() );

                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }

}
