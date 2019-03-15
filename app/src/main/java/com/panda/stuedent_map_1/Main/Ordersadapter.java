package com.panda.stuedent_map_1.Main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.panda.stuedent_map_1.Main.Models.Route_Module;
import com.panda.stuedent_map_1.R;
import io.reactivex.annotations.NonNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ahmed Kamal on 21-11-2017.
 */

public class Ordersadapter extends RecyclerView.Adapter<MyViewHolder>
{
    private List<Route_Module> movies;

    private Context c;
    Callback callback;
    int selection_pos = 0;

    public Ordersadapter(@NonNull List<Route_Module> movies, Callback callback) {

        this.movies = movies;
        this.callback = callback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.route_layout_item, parent, false);
        c = parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Route_Module movie = movies.get(position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.Select(holder.getAdapterPosition());
            }
        });
        OnBind(movie, holder);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void replaceData(List<Route_Module> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyItemRangeInserted(0, movies.size());
    }

    public void updateData(List<Route_Module> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public Route_Module getItem(int position) {
        if (position < 0 || position >= movies.size()) {
            throw new InvalidParameterException("INVALID IDX");
        }
        return movies.get(position);
    }

    public void clearData() {
        movies.clear();
        notifyDataSetChanged();
    }

    private void OnBind(Route_Module data, MyViewHolder viewHolder) {

        viewHolder.cardView.setText(data.getLineName());
    }

    public interface Callback {
        void Select(int pos);
    }
}
