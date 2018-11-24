package gropherapp.gropher.com.gropherapp.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.ListView;
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
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.activity.OtpScreen;
import gropherapp.gropher.com.gropherapp.activity.PayMoreMoney;
import gropherapp.gropher.com.gropherapp.activity.RecentOrderDetailScreen;
import gropherapp.gropher.com.gropherapp.activity.TrackOrderScreen;
import gropherapp.gropher.com.gropherapp.utils.AppController;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;
import gropherapp.gropher.com.gropherapp.utils.WebserviceUrl;


public class AdapterRecentOrder extends BaseAdapter {

    Context mContext;
    ArrayList<HashMap<String, String>> arr_recent_order;

    LayoutInflater inflater;
    GlobalClass globalClass;


    ImageView img_product;
    TextView tv_name, tv_description, tv_location, tv_date,tv_cancel,tv_order_status_val,tv_pay_more;
    ProgressDialog pd;
    ListView lv_recent_order;
    String TAG = "a_received";
    String cancellation_policy;
    String is_request_for_money,status;

    public AdapterRecentOrder(Context c, ArrayList<HashMap<String, String>> arr_recent_order,
                            ProgressDialog pd, ListView lv_recent_order,String cancellation_policy) {
        mContext = c;
        this.arr_recent_order = arr_recent_order;
        this.pd = pd;
        this.lv_recent_order = lv_recent_order;
        this.cancellation_policy = cancellation_policy;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        globalClass = (GlobalClass)mContext.getApplicationContext();


        // custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/open_sans_light.ttf");
    }

    @Override
    public int getCount() {
        return arr_recent_order.size();
    }

    @Override
    public Object getItem(int position) {
        return arr_recent_order.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //   View view= null;


        View view1 = inflater.inflate(R.layout.recent_row_view, null, true);

        tv_name = view1.findViewById(R.id.tv_name);
        tv_description = view1.findViewById(R.id.tv_description);
        tv_location = view1.findViewById(R.id.tv_location);
        tv_date = view1.findViewById(R.id.tv_date);
        tv_cancel = view1.findViewById(R.id.tv_cancel);
        tv_order_status_val = view1.findViewById(R.id.tv_order_status_val);
        tv_pay_more = view1.findViewById(R.id.tv_pay_more);

      

        tv_name.setText(arr_recent_order.get(position).get("shop_name"));
        tv_description.setText(arr_recent_order.get(position).get("instruction"));
        tv_location.setText(arr_recent_order.get(position).get("address"));
        tv_date.setText(arr_recent_order.get(position).get("order_placed_on"));

        is_request_for_money = arr_recent_order.get(position).get("is_request_for_money");

        switch (is_request_for_money) {
            case "1":

                tv_order_status_val.setText("Money Requested");

                tv_order_status_val.setTextColor(mContext.getResources().getColor(R.color.green));
                tv_pay_more.setVisibility(View.VISIBLE);
                tv_cancel.setVisibility(View.GONE);
                break;



            default:

                status = arr_recent_order.get(position).get("status1");
                tv_pay_more.setVisibility(View.GONE);
                tv_cancel.setVisibility(View.VISIBLE);

                switch (status) {
                    case "order_placed":
                        tv_order_status_val.setText("Placed");
                        tv_order_status_val.setTextColor(mContext.getResources().getColor(R.color.orange));

                        break;
                    case "order_accepted":
                        tv_order_status_val.setText("Accepted");
                        tv_order_status_val.setTextColor(mContext.getResources().getColor(R.color.purple));

                        break;
                    case "order_out_for_delivery":
                        tv_order_status_val.setText("Out For Delivery");
                        tv_order_status_val.setTextColor(mContext.getResources().getColor(R.color.track_blue));

                        break;
                    case "order_completed":
                        tv_order_status_val.setText("Completed");
                        tv_order_status_val.setTextColor(mContext.getResources().getColor(R.color.green));

                        break;
                    case "order_cancelled":
                        tv_order_status_val.setText("Cancelled");
                        tv_order_status_val.setTextColor(mContext.getResources().getColor(R.color.red));

                        break;
                    default:
                        tv_order_status_val.setText("");
                        break;
                }
                break;
        }


/*

        if (position % 2 == 0) {

            view1.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {

            view1.setBackgroundColor(mContext.getResources().getColor(R.color.lightblue));
        }
*/


        tv_pay_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PayMoreMoney.class);
                intent.putExtra("id",arr_recent_order.get(position).get("id"));
                intent.putExtra("p_name",arr_recent_order.get(position).get("name"));
                mContext.startActivity(intent);
            }
        });

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,RecentOrderDetailScreen.class);
                intent.putExtra("id",arr_recent_order.get(position).get("id"));
                intent.putExtra("product_price",arr_recent_order.get(position).get("product_price"));
                intent.putExtra("order_placed_on",arr_recent_order.get(position).get("order_placed_on"));
                intent.putExtra("shop_address",arr_recent_order.get(position).get("shop_address"));
                intent.putExtra("address",arr_recent_order.get(position).get("address"));
                intent.putExtra("instruction",arr_recent_order.get(position).get("instruction"));
                intent.putExtra("job_status",arr_recent_order.get(position).get("job_status"));
                intent.putExtra("latitute",arr_recent_order.get(position).get("latitute"));
                intent.putExtra("longitude",arr_recent_order.get(position).get("longitude"));
                intent.putExtra("shop_latitude",arr_recent_order.get(position).get("shop_latitude"));
                intent.putExtra("shop_longitude",arr_recent_order.get(position).get("shop_longitude"));
                mContext.startActivity(intent);

            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = arr_recent_order.get(position).get("id");
              ///  job_cancel_url(id);

             //   android.text.Html.fromHtml(instruction).toString();

                String message = Html.fromHtml(cancellation_policy).toString();
                alert_for_cancellation_policy(id,message);
            }
        });








        return view1;
    }

    public  void alert_for_cancellation_policy(final String id, final String message){

        new FancyAlertDialog.Builder((Activity) mContext)
                .setTitle("Do you really want to cancel this order ?")
                .setBackgroundColor(Color.parseColor("#D35400"))  //Don't pass R.color.colorvalue
                .setMessage(message)
                .setNegativeBtnText("No")
                .setNegativeBtnBackground(Color.parseColor("#D35400"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Yes")
                .setPositiveBtnBackground(Color.parseColor("#145A32"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_clear_white_48dp, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                       // Toast.makeText(mContext,"Rate",Toast.LENGTH_SHORT).show();
                        job_cancel_url(id);
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                     //   Toast.makeText(mContext,"Cancel",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }
    private void job_cancel_url(final String id) {
        pd.show();
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                WebserviceUrl.job_cancel, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "url_hit: " + WebserviceUrl.job_cancel);
                Log.d(TAG, "Response: " + response);

                Gson gson = new Gson();

                try {

                    arr_recent_order.clear();
                    JsonObject jobj = gson.fromJson(response, JsonObject.class);
                    //JSONObject jObject = new JSONObject(String.valueOf(content));
                    String status = jobj.get("status").toString().replaceAll("\"", "");
                    String message = jobj.get("message").toString().replaceAll("\"", "");


                    Log.d("TAG", "status :\t" + status);
                    Log.d("TAG", "message :\t" + message);

                    //   JsonObject jsonObject = jobj.getAsJsonObject("info");

                    JsonArray jsonArray =jobj.getAsJsonArray("info");
                    for(int i=0; i<jsonArray.size();i++) {

                        JsonObject jObject = (JsonObject) jsonArray.get(i);

                        String id = jObject.get("id").toString().replaceAll("\"", "");
                        String name = jObject.get("name").toString().replaceAll("\"", "");
                        String image = jObject.get("image").toString().replaceAll("\"", "");
                        String shop_name = jObject.get("shop_name").toString().replaceAll("\"", "");
                        String instruction = jObject.get("instruction").toString().replaceAll("\"", "");
                        String address = jObject.get("address").toString().replaceAll("\"", "");
                        String customer_id = jObject.get("customer_id").toString().replaceAll("\"", "");
                        String deliveryboy_id = jObject.get("deliveryboy_id").toString().replaceAll("\"", "");
                        String product_type = jObject.get("product_type").toString().replaceAll("\"", "");
                        String product_quantity = jObject.get("product_quantity").toString().replaceAll("\"", "");
                        String product_price = jObject.get("product_price").toString().replaceAll("\"", "");
                        String order_placed_on = jObject.get("order_placed_on").toString().replaceAll("\"", "");
                        String latitute = jObject.get("latitute").toString().replaceAll("\"", "");
                        String longitude = jObject.get("longitude").toString().replaceAll("\"", "");
                        String shop_address = jObject.get("shop_address").toString().replaceAll("\"", "");
                        String shop_latitude = jObject.get("shop_latitude").toString().replaceAll("\"", "");
                        String shop_longitude = jObject.get("shop_longitude").toString().replaceAll("\"", "");
                        String status1 = jObject.get("status").toString().replaceAll("\"", "");
                        String job_status = jObject.get("job_status").toString().replaceAll("\"", "");


                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", id);
                        map.put("name", name);
                        map.put("image", image);
                        map.put("shop_name", shop_name);
                        map.put("instruction", instruction);
                        map.put("address", address);
                        map.put("customer_id", customer_id);
                        map.put("order_placed_on", order_placed_on);
                        map.put("deliveryboy_id", deliveryboy_id);
                        map.put("product_type", product_type);
                        map.put("product_quantity", product_quantity);
                        map.put("product_price", product_price);
                        map.put("latitute", latitute);
                        map.put("longitude", longitude);
                        map.put("shop_address", shop_address);
                        map.put("shop_latitude", shop_latitude);
                        map.put("shop_longitude", shop_longitude);
                        map.put("status1", status1);
                        map.put("job_status", job_status);

                        arr_recent_order.add(map);

                    }


                    notifyDataSetChanged();
                    Toasty.success(mContext, message, Toast.LENGTH_SHORT, true).show();
                    pd.dismiss();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "job_cancel Error: " + error.getMessage());
                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("id", globalClass.getId());
                params.put("job_id", id);

                Log.d(TAG, "getParams: " + params);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        strReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));


    }

}
