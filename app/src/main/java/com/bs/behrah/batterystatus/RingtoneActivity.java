package com.bs.behrah.batterystatus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class RingtoneActivity extends AppCompatActivity implements View.OnClickListener {

    ringtoneAdapter adapter;
    LinearLayoutManager layoutManager;
    ImageView ivOk ;
    RecyclerView ringtone_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);

        ABC();
        init();
    }

    void init() {
        adapter = new ringtoneAdapter(getApplicationContext());
        layoutManager = new LinearLayoutManager(this);
        ringtone_rv.setLayoutManager(layoutManager);
        ringtone_rv.setAdapter(adapter);

    }

    void ABC() {
        ivOk = findViewById(R.id.iv_ok);
        ivOk.setOnClickListener(this);
        ringtone_rv = (RecyclerView) findViewById(R.id.ringtone_rv);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        adapter.getMp().stop();
        adapter.getMp().release();
        overridePendingTransition(R.anim.in2, R.anim.out2);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_ok) {
            if (adapter.getMp() != null)  {
                adapter.getMp().stop();
                adapter.getMp().release();
            }
            finish();
        }
    }
}
