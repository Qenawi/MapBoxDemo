package com.panda.stuedent_map_1.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.RecyclerView;
import com.google.firebase.database.*;
import com.panda.stuedent_map_1.Main.Models.Route_Module;
import com.panda.stuedent_map_1.Map_Pkg.MapActi2;
import com.panda.stuedent_map_1.R;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.Routes)
    RecyclerView recyclerView;
    CompositeDisposable disposable;
    Ordersadapter adapter;
    ArrayList<Route_Module> Routes = new ArrayList<>();
    int SelectedPos = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        ButterKnife.bind(this);
        disposable = new CompositeDisposable();
        initRv();
        getData();
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Lines");
        myRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    Routes.add((Route_Module) post.getValue(Route_Module.class));
                }
                adapter.replaceData(Routes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRv() {
        adapter = new Ordersadapter(new ArrayList<>(), new Ordersadapter.Callback() {
            @Override
            public void Select(int pos) {
                SelectedPos = pos;
                recyclerView.setEnabled(false);
                disposable.add(io.reactivex.Observable.timer(150, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s ->
                        {
                            Intent intent = new Intent
                                    (MainActivity.this, MapActi2.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("RouteModule", Routes.get(SelectedPos));
                            intent.putExtras(bundle);
                            recyclerView.setEnabled(true);
                            startActivity(intent);
                        }, e ->
                        {
                            e.printStackTrace();
                        })
                );

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new SlideInUpAnimator());

        recyclerView.getItemAnimator().setAddDuration(800);
        recyclerView.getItemAnimator().setRemoveDuration(500);
        recyclerView.getItemAnimator().setMoveDuration(500);
        recyclerView.getItemAnimator().setChangeDuration(500);

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.clear();
    }
}
