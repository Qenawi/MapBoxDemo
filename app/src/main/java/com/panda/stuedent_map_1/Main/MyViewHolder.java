package com.panda.stuedent_map_1.Main;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.RelativeLayout;
import com.panda.stuedent_map_1.R;

public class MyViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.Whole_layout)
    RelativeLayout txtv3;
    @BindView(R.id.RouteName)
    carbon.widget.TextView cardView;



    public MyViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
