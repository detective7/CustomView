package com.ys.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ys.customview.view.WatchBoard;
import com.ys.customview.view.Wave;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tv1,tv2;
    WatchBoard colock;
    Wave wave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = (TextView)findViewById(R.id.clock);
        tv2 = (TextView)findViewById(R.id.wave);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        colock = (WatchBoard)findViewById(R.id.clock_r);
        wave=(Wave)findViewById(R.id.wave_r);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.clock:
                colock.setVisibility(View.VISIBLE);
                wave.setVisibility(View.GONE);
                wave.clearAnimation();
                break;
            case R.id.wave:
                colock.setVisibility(View.GONE);
                wave.setVisibility(View.VISIBLE);
                break;
        }

    }
}
