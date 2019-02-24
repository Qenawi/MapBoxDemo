package com.panda.stuedent_map_1;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import carbon.widget.TextView;
import com.panda.stuedent_map_1.Map_Pkg.MainMapActivity;
import com.panda.stuedent_map_1.Map_Pkg.MapActi2;
import io.reactivex.disposables.CompositeDisposable;

import java.util.Observable;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.start_ptn1)
    TextView textView;
    CompositeDisposable disposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        ButterKnife.bind(this);
        disposable = new CompositeDisposable();
    }

    @OnClick({R.id.start_ptn1, R.id.start_ptn2})
    public void Start() {
        disposable.add(
                io.reactivex.Observable.timer(150, TimeUnit.MILLISECONDS).subscribe(s ->
                {
                    startActivity(new Intent(this, MapActi2.class));

                }, e -> {

                }));
    }

}
