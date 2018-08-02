package gropherapp.gropher.com.gropherapp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import gropherapp.gropher.com.gropherapp.R;



public class AdapterRecentOrder extends BaseAdapter {

    Context mContext;
    ArrayList<HashMap<String, String>> arr_recent_order;

    LayoutInflater inflater;


    ImageView img_product;
    TextView tv_name, tv_description, tv_location, tv_date;
    ProgressDialog pd;
    String TAG = "a_received";

    public AdapterRecentOrder(Context c, ArrayList<HashMap<String, String>> arr_recent_order,
                            ProgressDialog pd) {
        mContext = c;
        this.arr_recent_order = arr_recent_order;
        this.pd = pd;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        /*global_class = (Global_Class)mContext.getApplicationContext();
        global_class.grid_images = grid_images;*/


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
       
      

        tv_name.setText(arr_recent_order.get(position).get("shop_name"));
        tv_description.setText(arr_recent_order.get(position).get("instruction"));
        tv_location.setText(arr_recent_order.get(position).get("address"));
        tv_date.setText(arr_recent_order.get(position).get("order_placed_on"));
/*

        if (position % 2 == 0) {

            view1.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {

            view1.setBackgroundColor(mContext.getResources().getColor(R.color.lightblue));
        }
*/








        return view1;
    }
}
