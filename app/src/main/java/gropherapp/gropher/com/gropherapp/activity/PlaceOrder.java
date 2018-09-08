package gropherapp.gropher.com.gropherapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.fragment.FragmentWallet;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class PlaceOrder extends AppCompatActivity{

    String TAG = "place_order";
    GlobalClass globalClass;
    ProgressDialog pd;
    Spinner spinner1;
    ArrayList<String> spinner_items;
    ImageView img_proceed;
    EditText edt_name,edt_product_qty,edt_shop_name,edt_instruction,edt_price,edt_shop_add;
    RelativeLayout rl_image,rl_pin_loc;
    TextView tv_img_name,tv_location;
    ArrayList<HashMap<String, String>> product_array;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    File p_image;
    String str_id;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1234;
    double lat, lng;
    String add;
    String f_address;
    String lat1,lng1;
    Fragment fragment = null;
    RelativeLayout rl_container;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_order_screen);


        globalClass = (GlobalClass) getApplicationContext();
        pd = new ProgressDialog(PlaceOrder.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading..");






        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PlaceOrder.this,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PlaceOrder.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: ");
            }
            else{
                if(checkForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 124)){
                    Log.d(TAG, "onCreate: ");
                }

            }
        }

        ImageView img_back = findViewById(R.id.toolbar_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        spinner_items = new ArrayList<>();
        product_array = new ArrayList<>();

        spinner1 = findViewById(R.id.spinner1);
        img_proceed = findViewById(R.id.img_proceed);
        edt_name = findViewById(R.id.edt_name);
        edt_product_qty = findViewById(R.id.edt_product_qty);
        edt_shop_name = findViewById(R.id.edt_shop_name);
        edt_shop_add = findViewById(R.id.edt_shop_add);
        edt_instruction = findViewById(R.id.edt_instruction);
        edt_price = findViewById(R.id.edt_price);
        rl_image = findViewById(R.id.rl_image);
        tv_img_name = findViewById(R.id.tv_img_name);
        rl_pin_loc = findViewById(R.id.rl_pin_loc);
        tv_location = findViewById(R.id.tv_location);
        rl_container = findViewById(R.id.rl_container);


        tv_img_name.setVisibility(View.GONE);
        rl_container.setVisibility(View.GONE);

        product_type_url();


        rl_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        img_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLatlng(edt_shop_add.getText().toString());


            }
        });



        rl_pin_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceOrder.this,MapScreen.class);
                startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             /*   Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();*/
                Log.d(TAG, "onItemSelected: pk>>  "+product_array);
                 str_id = product_array.get(position ).get("id");
                String str_name = product_array.get(position).get("name");

                Log.d(TAG, "onItemSelected: str_id : "+str_id);
                Log.d(TAG, "onItemSelected: str_name : "+str_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
    }

    public boolean checkForPermission(final String[] permissions, final int permRequestCode) {

        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            final String perm = permissions[i];
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(PlaceOrder.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {

                    Log.d("permisssion","not granted");


                    if (shouldShowRequestPermissionRationale(permissions[i])) {

                        Log.d("if","if");
                        permissionsNeeded.add(perm);

                    } else {
                        // add the request.
                        Log.d("else","else");
                        permissionsNeeded.add(perm);
                    }

                }
            }
        }

        if (permissionsNeeded.size() > 0) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // go ahead and request permissions
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), permRequestCode);
            }
            return false;
        } else {
            // no permission need to be asked so all good...we have them all.
            return true;
        }

    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {
                        getResources().getString(R.string.take_photo),
                        getResources().getString(R.string.choose_from_gallery),
                        getResources().getString(R.string.cancel),
                };
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PlaceOrder.this);
                builder.setTitle(getResources().getString(R.string.select_option));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(getResources().getString(R.string.take_photo))) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals(getResources().getString(R.string.choose_from_gallery))) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals(getResources().getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toasty.error(PlaceOrder.this, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(PlaceOrder.this,  getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            p_image = new File(getRealPathFromURI(uri));


            Log.d(TAG, "image = "+p_image);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                //imageView2.setImageBitmap(bitmap);
                tv_img_name.setVisibility(View.VISIBLE);
                String imageName = p_image.getName();
                tv_img_name.setText(imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK) {


            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }


            try {
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();


                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

/*
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                        bitmapOptions);*/

                Log.d(TAG, "bitmap: "+bitmap);

               // imageView2.setImageBitmap(bitmap);


                String path = Environment.getExternalStorageDirectory()+File.separator;
                // + File.separator
                //   + "Phoenix" + File.separator + "default";
                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image = file;

                    tv_img_name.setVisibility(View.VISIBLE);
                    String imageName = p_image.getName();
                    tv_img_name.setText(imageName);


                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Bitmap photo = (Bitmap) data.getExtras().get("data");
            // iv_product_image.setImageBitmap(photo);
        }

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                // get String data from Intent


                String address = data.getStringExtra("address");
                String city = data.getStringExtra("city");
                String state = data.getStringExtra("state");
                String country = data.getStringExtra("country");
                String zip = data.getStringExtra("zip");


            //    add = address+", "+city+", "+state+", "+country+", "+zip;

               // getLatlng(add);
                 lat1 = data.getStringExtra("lat1");
                 lng1 = data.getStringExtra("lng1");
                f_address = data.getStringExtra("f_address");

                tv_location.setText(f_address);
          /*      input_address.setText(address);
                input_city.setText(city);
                // input_state.setText(state);
                input_country.setText(country);
                // input_zip.setText(zip);*/



                // set text view with string
          /*      TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(returnString);*/
            }
        }


    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public  void getLatlng(String address_full){
        pd.show();

        String URL ="https://maps.googleapis.com/maps/api/geocode/json?address="
                +address_full+"&key="
                +getResources().getString(R.string.GEO_CODE_API_KEY);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //Here response will be received in form of JSONObject

                Log.d(TAG,"Server response - "+response );

                try {




                    JSONArray results = response.getJSONArray("results");

                    for(int i = 0; i<results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);


                        //  Log.d(TAG, "address_components: "+address_components.toString());

                     /*   f_address = obj.getString("formatted_address");
                        Log.d(TAG, "f_address: "+f_address);*/

                        JSONObject geometry = obj.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");

                        lat = location.getDouble("lat");
                        lng = location.getDouble("lng");
                        Log.d(TAG, "lat: "+lat);
                        Log.d(TAG, "lng: "+lng);

                        break;
                    }

                    place_order_url(lat,lng);





                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void product_type_url() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pd.show();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                WebserviceUrl.product_type, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.product_type);
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

                        JsonArray jsonArray =jobj.getAsJsonArray("info");
                        for(int i=0; i<jsonArray.size();i++) {

                            JsonObject jObject = (JsonObject) jsonArray.get(i);

                            String id = jObject.get("id").toString().replaceAll("\"", "");
                            String name = jObject.get("name").toString().replaceAll("\"", "");


                            HashMap<String, String> map_ser = new HashMap<String, String>();
                            map_ser.put("id", id);
                            map_ser.put("name", name);

                            product_array.add(map_ser);
                            spinner_items.add(name);

                        }

                        Log.d(TAG, "onResponse: p_arr:  "+product_array);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PlaceOrder.this, R.layout.spinner_text, spinner_items);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner1.setAdapter(adapter);
                        pd.dismiss();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "login Error: " + error.getMessage());
                Toast.makeText(PlaceOrder.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }


    public void place_order_url(double s_lat, double s_lng){

        // pd.show();

        String url = WebserviceUrl.place_order;
        AsyncHttpClient cl = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("id",globalClass.getId());
        params.put("name",edt_name.getText().toString());
        params.put("product_type",str_id);
        params.put("product_quantity",edt_product_qty.getText().toString());
        params.put("shop_name",edt_shop_name.getText().toString());
        params.put("instruction",edt_instruction.getText().toString());
        params.put("product_price",edt_price.getText().toString());
      /*  params.put("latitute",lat1);
        params.put("longitude",lng1);
        params.put("address", f_address);  */
        params.put("latitute","22.451170");
        params.put("longitude","88.301800");
        params.put("address", "Sketch Web Solutions, Minerva Gardens, Opp. IIM Joka, Diamond Harbour Rd, Joka, Kolkata, West Bengal" );

        params.put("shop_latitute",s_lat);
        params.put("shop_longitude",s_lng);
        params.put("shop_address",edt_shop_add.getText().toString());


        try{
            params.put("product_image", p_image);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        Log.d(TAG , "URL "+url);
        Log.d(TAG , "params "+params.toString());


        int DEFAULT_TIMEOUT = 15 * 1000;
        cl.setMaxRetriesAndTimeout(3 , DEFAULT_TIMEOUT);
        cl.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (response != null) {
                    Log.d(TAG, "user_profile_pic_update- " + response.toString());
                    try {

                        //JSONObject result = response.getJSONObject("result");

                        int status = response.getInt("status");
                        String message = response.getString("message");

                        if (status == 1) {

                            // Log.d(TAG, "name: "+name)


                            String order_id = response.getString("order_id");
                            String otp = response.getString("otp");


                            alert_for_otp(otp,order_id);



                          //  Toasty.success(PlaceOrder.this, getResources().getString(R.string.profile_pic_updated), Toast.LENGTH_SHORT, true).show();

                        } else if(status == 2){
                            Toasty.error(PlaceOrder.this, "Failed To Place Order", Toast.LENGTH_SHORT, true).show();




                        } else if(status == 3){
                            Toasty.info(PlaceOrder.this, "Please Add Amount To Your Wallet", Toast.LENGTH_SHORT, true).show();
                            rl_container.setVisibility(View.VISIBLE);
                            fragment = new FragmentWallet();
                            FragmentManager fragmentManager2 = getSupportFragmentManager();
                            fragmentManager2.beginTransaction().replace(R.id.flContent, fragment).commit();

                        } else{

                            Toasty.warning(PlaceOrder.this, message, Toast.LENGTH_SHORT, true).show();
                        }

                         pd.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                // pd.dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {

                Log.d(TAG+"Failed: ", ""+statusCode);
                Log.d(TAG+"Error : ", "" + throwable);
                Log.e(TAG, String.valueOf(throwable instanceof ConnectTimeoutException));
                Toasty.error(PlaceOrder.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                pd.dismiss();

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }

    public  void  alert_for_otp(final String otp, final String order_id){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PlaceOrder.this);
        builder1.setMessage("The OTP for your order is "+otp+". Copy this otp to confirm your order.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Copy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(otp);
                        Toast.makeText(PlaceOrder.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PlaceOrder.this,OtpScreen.class);
                        intent.putExtra("order_id",order_id);
                        startActivity(intent);
                    }
                });

       /* builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
*/
        AlertDialog alert11 = builder1.create();
        alert11.show();
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
