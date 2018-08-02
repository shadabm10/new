package gropherapp.gropher.com.gropherapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.activity.LoginScreen;

import gropherapp.gropher.com.gropherapp.activity.ProfileScreen;
import gropherapp.gropher.com.gropherapp.fragment.FragmentHistory;
import gropherapp.gropher.com.gropherapp.fragment.FragmentMyOrder;
import gropherapp.gropher.com.gropherapp.fragment.FragmentReview;
import gropherapp.gropher.com.gropherapp.fragment.FragmentStatistic;
import gropherapp.gropher.com.gropherapp.fragment.FragmentSupport;
import gropherapp.gropher.com.gropherapp.fragment.FragmentWallet;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.Shared_Preference;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class DrawerActivity extends AppCompatActivity {

    String TAG = "drawer";
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    Toolbar toolbar;
    ImageView toolbar_back,toolbar_profile,toolbar_logo;

    Fragment fragment = null;
    TextView tooltext;
    String video;
    ImageView toolbar_image;


    ProgressDialog progressBar;
    MenuItem target;
    Boolean install_first_time = false;
    String s,mTime;
    boolean doubleBackToExitPressedOnce = false;
    AlertDialog alertDialog1;
    String m_name;
    String video_type;
    ArrayList<String> participants = new ArrayList<>();
    String VIDEO_ID;
    int frag ;
    //TextView header_text;
    String k_stat;
    String fcm_reg_token="not found";
    String fcm_reg_token_temp;
    String device_id;
    GlobalClass globalClass;
    Shared_Preference prefrence;
    ProgressDialog pd;
  /*  ImageLoader loader;
    DisplayImageOptions defaultOptions;*/
   // ImageView imageView2;



    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_drawer);

        globalClass = (GlobalClass)getApplicationContext();
        prefrence = new Shared_Preference(DrawerActivity.this);
        prefrence.loadPrefrence();
        pd=new ProgressDialog(DrawerActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading...");

     /*   defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                //  .showImageOnLoading(R.mipmap.loading_black128px)
                //  .showImageForEmptyUri(R.mipmap.no_image)
                //  .showImageOnFail(R.mipmap.no_image)
                //  .showImageOnFail(R.mipmap.img_failed)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(DrawerActivity.this.getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
        loader = ImageLoader.getInstance();


*/

        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Set a Toolbar to replace the ActionBar.

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_back =  toolbar.findViewById(R.id.toolbar_back);
        toolbar_profile =  toolbar.findViewById(R.id.toolbar_profile);
        toolbar_logo =  toolbar.findViewById(R.id.toolbar_logo);
        toolbar_back.setVisibility(View.GONE);

        toolbar_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawerActivity.this,ProfileScreen.class);
                startActivity(intent);
            }
        });



        // Find our drawer view
        mDrawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nvView);
        navigationView.setItemIconTintList(null);
        View head=navigationView.getHeaderView(0);
     /*   header_text = head.findViewById(R.id.header_text);
        imageView2 = head.findViewById(R.id.imageView2);*/
      /*  header_text.setText("Hi, "+globalClass.getFname());


        if(globalClass.getProfil_pic().isEmpty()){
            imageView2.setImageResource(R.mipmap.no_image);
        }else{

            loader.displayImage(globalClass.getProfil_pic(), imageView2 , defaultOptions);
        }
*/

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //the method we have create in activity
        }

        fragment = new FragmentStatistic();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


        // Setup drawer view
        setupDrawerContent(navigationView);

        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle




        DrawerLayout drawer =  findViewById(R.id.drawer_layout);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {



            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(R.string.app_name);
                InputMethodManager inputMethodManager = (InputMethodManager) DrawerActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(DrawerActivity.this.getCurrentFocus().getWindowToken(), 0);
                invalidateOptionsMenu();

            }


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle("Home");
                InputMethodManager inputMethodManager = (InputMethodManager) DrawerActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(DrawerActivity.this.getCurrentFocus().getWindowToken(), 0);
                invalidateOptionsMenu();

                 /*   if(globalClass.getProfil_pic().isEmpty()){
                        imageView2.setImageResource(R.mipmap.no_image);
                    }else{
                        loader.displayImage(globalClass.getProfil_pic(), imageView2 , defaultOptions);
                    }
                    header_text.setText("Hi, "+globalClass.getFname());*/

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();




    }







    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("frag", mTime);
        //  Log.d("savedInstanceState", "from_frag: "+outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        s =savedInstanceState.getString("frag");
        // Log.d("savedInstanceState", "onRestoreInstanceState: "+s);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        // Class fragmentClass = null;


        switch (menuItem.getItemId()) {
            case R.id.nav_statistics:

                frag = 1;


                fragment = new FragmentStatistic();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


                break;
            case R.id.nav_my_order:

                frag = 2;


                fragment = new FragmentMyOrder();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction().replace(R.id.flContent, fragment).commit();
                break;
            case R.id.nav_wallet:

                frag = 3;


                fragment = new FragmentWallet();
                FragmentManager fragmentManager2 = getSupportFragmentManager();
                fragmentManager2.beginTransaction().replace(R.id.flContent, fragment).commit();
                break;


            case R.id.nav_ord_his:

                frag = 3;


                fragment = new FragmentHistory();
                FragmentManager fragmentManager3 = getSupportFragmentManager();
                fragmentManager3.beginTransaction().replace(R.id.flContent, fragment).commit();
                break;

            case R.id.nav_reviews:

                frag = 3;


                fragment = new FragmentReview();
                FragmentManager fragmentManager4 = getSupportFragmentManager();
                fragmentManager4.beginTransaction().replace(R.id.flContent, fragment).commit();
                break;

            case R.id.nav_support:

                frag = 3;


                fragment = new FragmentSupport();
                FragmentManager fragmentManager5 = getSupportFragmentManager();
                fragmentManager5.beginTransaction().replace(R.id.flContent, fragment).commit();
                break;

            case R.id.nav_Logout:

                if(globalClass.isNetworkAvailable()){
                    logout_url(globalClass.getId());
                }else{
                    Toasty.info(DrawerActivity.this, "please check your internet connection", Toast.LENGTH_SHORT, true).show();
                }





                break;


            default:



        }

        if (fragment != null) {

            try {
                // fragment = (Fragment) fragmentCla0ss.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title

            // Close the navigation drawer
            mDrawer.closeDrawers();

        }

    }
    @Override
    protected void onResume() {

        //  show_chat();
        super.onResume();
        //  startService(new  Intent(this, Service_class.class));

        //Log.d("kite", "onResume: ");



        // Log.d("ORRRR", "Call Onresume");
    }

    @Override
    protected void onPause() {
        super.onPause();

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void logout_url(final String id) {
        // Tag used to cancel the request
        String tag_string_req = "req_logout";



        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.logout, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: "+ WebserviceUrl.logout);
                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {


                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");


                    Log.d("TAG", "status :\t" + status);
                    Log.d("TAG", "message :\t" + message);

                    switch (status) {
                        case "1":

                            prefrence.clearPrefrence();
                            globalClass.setLogin_status(false);
                            Toasty.success(DrawerActivity.this, message, Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(DrawerActivity.this, LoginScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                            break;
                        case "2":
                            Toasty.error(DrawerActivity.this, message, Toast.LENGTH_SHORT, true).show();
                            break;
                        case "0":
                            Toasty.info(DrawerActivity.this, message, Toast.LENGTH_SHORT, true).show();
                            break;
                    }

/*

                    JsonObject jsonObject = jobj.getAsJsonObject("info");

                    String uid = jsonObject.get("uid").toString().replaceAll("\"", "");
                    String name = jsonObject.get("name").toString().replaceAll("\"", "");
                    String mobile = jsonObject.get("mobile").toString().replaceAll("\"", "");
                    String email = jsonObject.get("email").toString().replaceAll("\"", "");
                    //String name = jsonObject.get("name").toString().replaceAll("\"", "");
*/


                  /*  globalClass.setId("");
                    globalClass.setName("");
                    globalClass.setPhone_number("");
                    globalClass.setEmail("");
*/





                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "logout Error: " + error.getMessage());
                Toast.makeText(DrawerActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                //  hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", id);


                Log.d(TAG, "getParams: "+params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }
















    // boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toasty.info(DrawerActivity.this,"Tap back button in order to exit", Toast.LENGTH_SHORT,true).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}
