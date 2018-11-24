package gropherapp.gropher.com.gropherapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.ArrayList;
import java.util.HashMap;

import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.utils.GlobalClass;


public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {

    Context mContext;
    ArrayList<HashMap<String,String>> arr_order_history;

    LayoutInflater inflater;
    ImageLoader loader;

    String TAG = "adapterhistory";

    DisplayImageOptions defaultOptions;
    GlobalClass globalClass;

    public AdapterHistory(Context c, ArrayList<HashMap<String,String>> arr_order_history ) {
        this.inflater = LayoutInflater.from(c);
        mContext = c;
        this.arr_order_history = arr_order_history;

        globalClass = (GlobalClass)mContext.getApplicationContext();

        // custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/open_sans_light.ttf");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_single_row, parent, false);
        return new AdapterHistory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String delivery_boy_name =arr_order_history.get(position).get("fname") +" "+arr_order_history.get(position).get("lname");
        String status =arr_order_history.get(position).get("status1");


        holder.tv_order_id.setText(arr_order_history.get(position).get("id"));
        holder.tv_customer_name.setText(delivery_boy_name);
        holder.tv_product_name.setText(arr_order_history.get(position).get("name"));
        holder.tv_order_date.setText(arr_order_history.get(position).get("order_placed_on"));
        holder.tv_price.setText("$ "+arr_order_history.get(position).get("product_price"));
        holder.tv_qty.setText(arr_order_history.get(position).get("product_quantity"));
        holder.tv_address.setText(arr_order_history.get(position).get("address"));


        switch (status) {
            case "order_cancelled":
                holder.tv_order_status.setText("Cancelled");
                holder.tv_order_status.setTextColor(mContext.getResources().getColor(R.color.red));

                break;
            case "order_completed":
                holder.tv_order_status.setText("Completed");
                holder.tv_order_status.setTextColor(mContext.getResources().getColor(R.color.green));
                break;
            default:
                holder.tv_order_status.setText("");
                break;
        }





        /*  holder.tv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = arr_order_history.get(position).get("id");
                String discount_amount = arr_order_history.get(position).get("discount_amount");
               // verify_coupon(id,discount_amount);
            }
        });*/


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arr_order_history.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order_date,tv_address,tv_qty,tv_price,tv_product_name,
                tv_customer_name,tv_order_id,tv_order_status;

        ViewHolder(View itemView) {
            super(itemView);



            tv_order_id = itemView.findViewById(R.id.tv_order_id);
            tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_order_date = itemView.findViewById(R.id.tv_order_date);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);





        }



    }

}

