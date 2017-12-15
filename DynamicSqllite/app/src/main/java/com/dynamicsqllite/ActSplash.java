package com.dynamicsqllite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public class ActSplash extends ParentActivity
{

    @BindView(R.id.tvName)
    TextView tvName;

    @Override
    int getMainView() {
        return  R.layout.act_splash;
    }

    @Override
    void initViews() {

        tvName.setText("Hello Java");


    }
}
