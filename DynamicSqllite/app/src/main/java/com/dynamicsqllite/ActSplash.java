package com.dynamicsqllite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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
    }
}
