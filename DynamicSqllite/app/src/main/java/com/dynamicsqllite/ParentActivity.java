package com.dynamicsqllite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public abstract class ParentActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getMainView());

        ButterKnife.bind(this);

        initViews();
    }

    abstract int getMainView();
    abstract void initViews();

}
