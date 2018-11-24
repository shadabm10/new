package gropherapp.gropher.com.gropherapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import gropherapp.gropher.com.gropherapp.R;


public class RecentOrderDetailScreen extends AppCompatActivity{


    TextView tv_order_id,price_tv,tv_date,tv_description,tv_pickup,tv_delivery_location,tv_track;
    String status;
    ImageView img_back,img_accepted,img_picked_up,img_out_for_delivery;
    TextView tv_status_1,tv_status_2,tv_status_3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_screen);

         img_back = findViewById(R.id.toolbar_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        tv_order_id = findViewById(R.id.tv_order_id);
        price_tv = findViewById(R.id.price_tv);
        tv_date = findViewById(R.id.tv_date);
        tv_description = findViewById(R.id.tv_description);
        tv_pickup = findViewById(R.id.tv_pickup);
        tv_delivery_location = findViewById(R.id.tv_delivery_location);
        tv_track = findViewById(R.id.tv_track);


        tv_status_1=findViewById(R.id.tv_status_1);
        tv_status_2=findViewById(R.id.tv_status_2);
        tv_status_3=findViewById(R.id.tv_status_3);

        img_accepted=findViewById(R.id.img_accepted);
        img_picked_up=findViewById(R.id.img_picked_up);
        img_out_for_delivery=findViewById(R.id.img_out_for_delivery);


        status =  getIntent().getStringExtra("job_status");

        tv_order_id.setText("Order Id : "+getIntent().getStringExtra("id"));
        price_tv.setText("$ "+getIntent().getStringExtra("product_price"));
        tv_date.setText(getIntent().getStringExtra("order_placed_on"));
        tv_pickup.setText(getIntent().getStringExtra("shop_address"));
        tv_description.setText(getIntent().getStringExtra("instruction"));
        tv_delivery_location.setText(getIntent().getStringExtra("address"));

        tv_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RecentOrderDetailScreen.this, TrackOrderScreen.class);
                intent.putExtra("latitute",getIntent().getStringExtra("latitute"));
                intent.putExtra("longitude",getIntent().getStringExtra("longitude"));
                intent.putExtra("shop_latitude",getIntent().getStringExtra("shop_latitude"));
                intent.putExtra("shop_longitude",getIntent().getStringExtra("shop_longitude"));
                intent.putExtra("shop_address",getIntent().getStringExtra("shop_address"));
                intent.putExtra("address",getIntent().getStringExtra("address"));

                startActivity(intent);
            }
        });


        switch (status) {
            case "accepted":
                tv_status_1.setVisibility(View.VISIBLE);
                tv_status_2.setVisibility(View.GONE);
                tv_status_3.setVisibility(View.GONE);

                img_accepted.setImageResource(R.mipmap.status_filled);
                img_out_for_delivery.setImageResource(R.mipmap.status_empty);
                img_picked_up.setImageResource(R.mipmap.status_empty);

                break;

            case "picked_up":
                tv_status_1.setVisibility(View.GONE);
                tv_status_2.setVisibility(View.VISIBLE);
                tv_status_3.setVisibility(View.GONE);

                img_accepted.setImageResource(R.mipmap.status_empty);
                img_out_for_delivery.setImageResource(R.mipmap.status_empty);
                img_picked_up.setImageResource(R.mipmap.status_filled);

                break;
            case "on_the_way":
                tv_status_1.setVisibility(View.GONE);
                tv_status_2.setVisibility(View.GONE);
                tv_status_3.setVisibility(View.VISIBLE);

                img_accepted.setImageResource(R.mipmap.status_empty);
                img_out_for_delivery.setImageResource(R.mipmap.status_filled);
                img_picked_up.setImageResource(R.mipmap.status_empty);

                break;

            case "viewed":
                tv_status_1.setVisibility(View.GONE);
                tv_status_2.setVisibility(View.GONE);
                tv_status_3.setVisibility(View.GONE);

                img_accepted.setImageResource(R.mipmap.status_empty);
                img_out_for_delivery.setImageResource(R.mipmap.status_empty);
                img_picked_up.setImageResource(R.mipmap.status_empty);

                break;

            case "current":
                tv_status_1.setVisibility(View.GONE);
                tv_status_2.setVisibility(View.GONE);
                tv_status_3.setVisibility(View.GONE);

                img_accepted.setImageResource(R.mipmap.status_empty);
                img_out_for_delivery.setImageResource(R.mipmap.status_empty);
                img_picked_up.setImageResource(R.mipmap.status_empty);

                break;

            case "delivered":


                break;

        }



    }
}
