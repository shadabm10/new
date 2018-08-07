package gropherapp.gropher.com.gropherapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import gropherapp.gropher.com.gropherapp.R;


public class RecentOrderDetailScreen extends AppCompatActivity{

    RatingBar rating1,rating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_screen);

        ImageView img_back = findViewById(R.id.toolbar_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        rating1 = findViewById(R.id.rating1);
        rating = findViewById(R.id.rating);

        rating.setNumStars(5);
        rating1.setNumStars(5);
    }
}
