package com.example.administrator.weather.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.administrator.weather.R;

public class NetworkActivity extends BaseActivity {
    private RelativeLayout rlNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        rlNetwork = (RelativeLayout) findViewById(R.id.rl_network);
        rlNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NetworkActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });
    }
}
