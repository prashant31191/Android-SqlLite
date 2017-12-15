package com.dynamicsqllite;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dynamicsqllite.view.ActStudentList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public class ActSplash extends ActBase
{
    TextView tvName;

    @Override
    protected int baseViewData() {
        return  R.layout.act_splash;
    }

    @Override
    protected void baseSetData() {
        tvName = _findViewById(R.id.tvName);

        tvName.setText("Hello world");
        rlHeader.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ActSplash.this, ActStudentList.class));
                finish();
            }
        },2500);
    }
}
