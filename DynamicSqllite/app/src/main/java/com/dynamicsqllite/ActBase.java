package com.dynamicsqllite;

import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public abstract class ActBase extends ParentActivity
{

    @BindView(R.id.rlHeader)
   protected RelativeLayout rlHeader;

    @BindView(R.id.rlData)
   protected RelativeLayout rlData;

    @Override
    protected int getMainView() {
        return  R.layout.act_parent;
    }

    @Override
    protected void initViews() {
        ViewGroup.inflate(this,baseViewData(), rlData);
        baseSetData();

    }

    protected abstract int baseViewData();
    protected abstract void baseSetData();


}
