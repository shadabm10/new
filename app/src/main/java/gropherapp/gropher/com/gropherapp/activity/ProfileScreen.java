package gropherapp.gropher.com.gropherapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

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
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.Shared_Preference;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class ProfileScreen extends AppCompatActivity {

    String TAG = "profile";
    ImageView imageView2;
    TextView name_tv,txt_phone_no,txt_email,toolbar_save,change_pic_tv,toolbar_edit;
    GlobalClass globalClass;
    ProgressDialog pd;
    Shared_Preference preference;
    ImageLoader loader;
    DisplayImageOptions defaultOptions;
    EditText edt_phone_no,name_edt;
    RatingBar ratingBar;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    File p_image;
    String str_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        initialisation();
        function();
        get_profile_url();



    }

    public void initialisation(){

        globalClass = (GlobalClass)getApplicationContext();
        preference = new Shared_Preference(ProfileScreen.this);
        pd=new ProgressDialog(ProfileScreen.this);


        imageView2 = findViewById(R.id.imageView2);
        name_tv = findViewById(R.id.name_tv);
        txt_phone_no = findViewById(R.id.txt_phone_no);
        txt_email = findViewById(R.id.txt_email);
        toolbar_save = findViewById(R.id.toolbar_save);
        edt_phone_no = findViewById(R.id.edt_phone_no);
        name_edt = findViewById(R.id.name_edt);
        change_pic_tv = findViewById(R.id.change_pic_tv);
        toolbar_edit = findViewById(R.id.toolbar_edit);
        ratingBar = findViewById(R.id.ratingBar);



    }

    public  void function(){

        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getResources().getString(R.string.loading));


        preference.loadPrefrence();

        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                //  .showImageOnLoading(R.mipmap.loading_black128px)
                //  .showImageForEmptyUri(R.mipmap.no_image)
                //  .showImageOnFail(R.mipmap.no_image)
                //  .showImageOnFail(R.mipmap.img_failed)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ProfileScreen.this.getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
        loader = ImageLoader.getInstance();



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileScreen.this,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ProfileScreen.this,
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



        name_edt.setVisibility(View.GONE);
        change_pic_tv.setVisibility(View.GONE);
        edt_phone_no.setVisibility(View.GONE);
        toolbar_save.setVisibility(View.GONE);


        if(globalClass.getProfil_pic().isEmpty()){
            imageView2.setImageResource(R.mipmap.no_image);
        }else{

            loader.displayImage(globalClass.getProfil_pic(), imageView2 , defaultOptions);
        }





        toolbar_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_edt.setVisibility(View.VISIBLE);
                change_pic_tv.setVisibility(View.VISIBLE);
                edt_phone_no.setVisibility(View.VISIBLE);
                toolbar_save.setVisibility(View.VISIBLE);
                name_tv.setVisibility(View.GONE);
                txt_phone_no.setVisibility(View.GONE);
                toolbar_edit.setVisibility(View.GONE);
                edt_phone_no.setSelection(edt_phone_no.getText().length());
                name_edt.setSelection(name_edt.getText().length());
            }
        });


        toolbar_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_edt.setVisibility(View.GONE);
                change_pic_tv.setVisibility(View.GONE);
                edt_phone_no.setVisibility(View.GONE);
                toolbar_save.setVisibility(View.GONE);
                name_tv.setVisibility(View.VISIBLE);
                txt_phone_no.setVisibility(View.VISIBLE);
                toolbar_edit.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_phone_no.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(name_edt.getWindowToken(), 0);

                update_profile_details_url();
            }
        });

        change_pic_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public boolean checkForPermission(final String[] permissions, final int permRequestCode) {

        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            final String perm = permissions[i];
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(ProfileScreen.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {

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
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ProfileScreen.this);
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
                Toasty.error(ProfileScreen.this, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ProfileScreen.this,  getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
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
                 imageView2.setImageBitmap(bitmap);
              //  tv_img_name.setVisibility(View.VISIBLE);
              //  String imageName = p_image.getName();
             //   tv_img_name.setText(imageName);
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

                 imageView2.setImageBitmap(bitmap);


                String path = Environment.getExternalStorageDirectory()+File.separator;
                // + File.separator
                //   + "Phoenix" + File.separator + "default";
                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image = file;



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



    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void get_profile_url() {
        // Tag used to cancel the request
        pd.show();
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.get_profile_details, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.get_profile_details);
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

                        JsonObject jsonObject = jobj.getAsJsonObject("info");

                        String uid = jsonObject.get("uid").toString().replaceAll("\"", "");
                        String name = jsonObject.get("name").toString().replaceAll("\"", "");
                        String mobile = jsonObject.get("mobile").toString().replaceAll("\"", "");
                        String emailid = jsonObject.get("emailid").toString().replaceAll("\"", "");
                        String image = jsonObject.get("image").toString().replaceAll("\"", "");
                        String rating = jsonObject.get("rating").toString().replaceAll("\"", "");


                        Log.d(TAG, "onResponse: "+name);

                        name_tv.setText(name);
                        txt_phone_no.setText(mobile);
                        txt_email.setText(emailid);
                        edt_phone_no.setText(mobile);
                        name_edt.setText(name);
                        ratingBar.setRating(Float.parseFloat(rating));

                        if(image.isEmpty()){
                            imageView2.setImageResource(R.mipmap.no_image);
                        }else {

                            loader.displayImage(image, imageView2, defaultOptions);
                        }
                        globalClass.setId(uid);
                        globalClass.setName(name);
                        globalClass.setPhone_number(mobile);
                        globalClass.setEmail(emailid);
                        globalClass.setProfil_pic(image);


                        preference.savePrefrence();

                    }else{
                        Toasty.error(ProfileScreen.this, message, Toast.LENGTH_LONG, true).show();

                    }
                pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "get_profile_details Error: " + error.getMessage());
                Toast.makeText(ProfileScreen.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();



                params.put("id",globalClass.getId());


                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }


    public void update_profile_details_url(){

         pd.show();

        String url = WebserviceUrl.update_profile_details;
        AsyncHttpClient cl = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("id",globalClass.getId());
        params.put("name",name_edt.getText().toString());
        params.put("mobile",edt_phone_no.getText().toString());


        try{
            params.put("profile_pic", p_image);
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
                    Log.d(TAG, "update_profile_details- " + response.toString());
                    try {

                        //JSONObject result = response.getJSONObject("result");

                        int status = response.getInt("status");
                        String message = response.getString("message");

                        if (status == 1) {

                            // Log.d(TAG, "name: "+name)

                            JSONObject object = response.getJSONObject("info");


                            String uid = object.getString("uid");
                            String name = object.getString("name");
                            String mobile = object.getString("mobile");
                            String emailid = object.getString("emailid");
                            String image = object.getString("image");
                            String rating = object.getString("rating");




                            name_tv.setText(name);
                            txt_phone_no.setText(mobile);
                            txt_email.setText(emailid);
                            edt_phone_no.setText(mobile);
                            name_edt.setText(name);
                            ratingBar.setRating(Float.parseFloat(rating));

                            if(image.isEmpty()){
                                imageView2.setImageResource(R.mipmap.no_image);
                            }else {

                                loader.displayImage(image, imageView2, defaultOptions);
                            }
                            globalClass.setId(uid);
                            globalClass.setName(name);
                            globalClass.setPhone_number(mobile);
                            globalClass.setEmail(emailid);
                            globalClass.setProfil_pic(image);


                            preference.savePrefrence();


                            Toasty.success(ProfileScreen.this, message, Toast.LENGTH_SHORT, true).show();

                        } else{


                            Toasty.warning(ProfileScreen.this, message, Toast.LENGTH_SHORT, true).show();
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
                Toasty.error(ProfileScreen.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                pd.dismiss();

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }

}