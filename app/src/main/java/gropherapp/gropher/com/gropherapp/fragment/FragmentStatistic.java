package gropherapp.gropher.com.gropherapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gropherapp.gropher.com.gropherapp.R;
import gropherapp.gropher.com.gropherapp.activity.PlaceOrder;


public class FragmentStatistic extends Fragment {
    TextView tv_place_order;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        tv_place_order = view.findViewById(R.id.tv_place_order);

        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PlaceOrder.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
