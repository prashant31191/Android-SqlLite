package com.dynamicsqllite.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dynamicsqllite.ActBase;
import com.dynamicsqllite.App;
import com.dynamicsqllite.R;
import com.dynamicsqllite.database.PostDatabaseHelper;
import com.dynamicsqllite.model.Post;
import com.dynamicsqllite.model.User;
import com.dynamicsqllite.utils.SwipeRevealLayout;
import com.dynamicsqllite.utils.ViewBinderHelper;

import java.util.List;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public class ActDynamicList extends ActBase
{

    // In any activity just pass the context and use the singleton method
    PostDatabaseHelper helper;

    SwipeRefreshLayout srLayout;
    RecyclerView recyclerView;
    FloatingActionButton fabAdd;


    List<Post>  postList;
    ListDataAdapter listDataAdapter;
    Paint p;// = new Paint();


    @Override
    protected int baseViewData() {
        return R.layout.act_post_list;
    }

    @Override
    protected void baseSetData() {
        initSubViews();
        initClickEvents();

        helper = PostDatabaseHelper.getInstance(this);

        postList = getPopstList();

        for (int i=0; i< postList.size(); i++)
        {
            App.showLog("====text="+postList.get(i).text);
            App.showLog("====user="+postList.get(i).user);
        }

        listDataAdapter = new ListDataAdapter(ActDynamicList.this, postList);
        recyclerView.setAdapter(listDataAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void initSubViews() {
        srLayout = _findViewById(R.id.srLayout);
        recyclerView = _findViewById(R.id.recyclerView);
        fabAdd = _findViewById(R.id.fabAdd);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActDynamicList.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void initClickEvents() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
    }

    private List<Post>  getPopstList()
    {
        return helper.getAllPosts();
    }

    private void insertData()
    {
        // Create sample data
        User sampleUser = new User();
        sampleUser.userName = "Steph";
        sampleUser.profilePictureUrl = "https://i.imgur.com/tGbaZCY.jpg";

        Post samplePost = new Post();
        samplePost.user = sampleUser;
        samplePost.text = "Won won!";

        // Get singleton instance of database
     //   PostDatabaseHelper databaseHelper = PostDatabaseHelper.getInstance(this);

        // Add sample post to the database
        helper.addPost(samplePost);

        // Get all posts from database
        List<Post> posts = helper.getAllPosts();
        for (Post post : posts) {
            // do something
            App.showLog("====post==="+post.text);
        }
    }






    // setting with reclicker

    public void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                /*if (direction == ItemTouchHelper.LEFT){
                    listDataAdapter.removeItem(position);
                }*/

                if (direction == ItemTouchHelper.LEFT && listDataAdapter != null) {
                    //11




                    final Dialog dialog = new Dialog(ActDynamicList.this);
                    // Include dialog.xml file
                    // Include dialog.xml file
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                //    dialog.getWindow().setBackgroundDrawableResource(R.drawable.prograss_bg); //temp removed
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.popup_exit);
                    dialog.setCancelable(false);

                    // set values for custom dialog components - text, image and button
                    // set values for custom dialog components - text, image and button
                    TextView tvExitMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                    TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
                    TextView tvOK = (TextView) dialog.findViewById(R.id.tvOk);

                    String strAlertMessage = "Are you sure you want to delete this request?";

                    String strYes = "YES";

                    String strNo = "NO";


                    App.showLog("==al-msg====strAlertMessage====" + strAlertMessage);
                    App.showLog("==al-0=====strYes===" + strYes);
                    App.showLog("==al-1====strNo====" + strNo);

                    tvExitMessage.setText(strAlertMessage);
                    tvCancel.setText(strNo);
                    tvOK.setText(strYes);


                  /*  tvExitMessage.setTypeface(App.getFont_Regular());
                    tvCancel.setTypeface(App.getFont_Bold());
                    tvOK.setTypeface(App.getFont_Bold());*/

                    dialog.show();

                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listDataAdapter.notifyDataSetChanged();
                                }
                            }, 200);
                        }
                    });

                    tvOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listDataAdapter.removeItem(position);
                                }
                            }, 200);

                        }
                    });
                    //asyncLogout(App.sharePrefrences.getStringPref(PreferencesKeys.strUserId));


                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {


                        /*p.setColor(Color.RED);
                        c.drawRect(background,p);*/

                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());


                        p.setColor(Color.RED);
                        p.setStyle(Paint.Style.FILL); //fill the background with blue color
                        //+ App.convertDpToPixel(5,ActDashboard.this)
                        c.drawRect(background.left , background.top, background.right, background.bottom, p);

                        p.setColor(Color.WHITE);
                        p.setTextSize(App.convertDpToPixel(20,ActDynamicList.this));
                        //p.setColor(Color.RED);

                        //c.drawText("Delete", background.centerX(), background.centerY(), p);
                        c.drawText("      Delete", background.left, background.centerY(), p);

                        //versionViewHolder.tvRowTitle.setTypeface(App.getFont_Regular());

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



    public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.VersionViewHolder> {
        List<Post> mArrListMyJobListModel;
        Context mContext;
        private ViewBinderHelper binderHelper = new ViewBinderHelper();


        public ListDataAdapter(Context context, List<Post> arrayList) {
            mArrListMyJobListModel = arrayList;
            mContext = context;

            // uncomment if you want to open only one row at a time
            binderHelper.setOpenOnlyOne(true);
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_myjob, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {

                if (mArrListMyJobListModel != null && 0 <= i && i < mArrListMyJobListModel.size()) {
                    final String data = "" + i;// = mArrListMyJobListModel.get(i).id;

                    // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
                    // put an unique string id as value, can be any string which uniquely define the data
                    binderHelper.bind(versionViewHolder.swipeLayout, data);

                    // Bind your data here
                    //holder.bind(data);
                }




                Post myJobListModel = mArrListMyJobListModel.get(i);

                versionViewHolder.tvRowTitle.setText(myJobListModel.text);
                //versionViewHolder.tvRowDesc.setText(Html.fromHtml("<b>"+myJobListModel.totalintrest+"<b> people interested"));



                versionViewHolder.deleteLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.showLog("==Removed==");

                        if (mArrListMyJobListModel.get(i) != null) {
                            final Dialog dialog = new Dialog(ActDynamicList.this);
                            // Include dialog.xml file
                            // Include dialog.xml file
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                            //dialog.getWindow().setBackgroundDrawableResource(R.drawable.prograss_bg); //temp removed
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.popup_exit);
                            dialog.setCancelable(false);

                            // set values for custom dialog components - text, image and button
                            // set values for custom dialog components - text, image and button
                            TextView tvExitMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                            TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
                            TextView tvOK = (TextView) dialog.findViewById(R.id.tvOk);

                            String strAlertMessage = "Are you sure you want to delete this request?";
                            String strYes = "YES";
                            String strNo = "NO";


                            App.showLog("==al-msg====strAlertMessage====" + strAlertMessage);
                            App.showLog("==al-0=====strYes===" + strYes);
                            App.showLog("==al-1====strNo====" + strNo);

                            tvExitMessage.setText(strAlertMessage);
                            tvCancel.setText(strNo);
                            tvOK.setText(strYes);


                         dialog.show();

                            tvCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            listDataAdapter.notifyDataSetChanged();
                                        }
                                    }, 200);
                                }
                            });

                            tvOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();


                                  //  if (App.isInternetAvail(ActMyJobList.this))
                                    {

                                        mArrListMyJobListModel.remove(i);

                                        binderHelper.closeLayout(""+i);

                                        notifyItemRemoved(i);
                                        notifyItemRangeChanged(i, mArrListMyJobListModel.size());

                                    }
                                }
                            });

                        }
                        else
                        {
                         //   App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                        }

                    }
                });
                versionViewHolder.llRowMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mArrListMyJobListModel == null)
                return 0;


            return mArrListMyJobListModel.size();
        }
        /**
         * Only if you need to restore open/close state when the orientation is changed.
         * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
         */
        public void saveStates(Bundle outState) {
            binderHelper.saveStates(outState);
        }

        /**
         * Only if you need to restore open/close state when the orientation is changed.
         * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
         */
        public void restoreStates(Bundle inState) {
            binderHelper.restoreStates(inState);
        }


        public void removeItem(int position) {

            if(App.isInternetAvail(ActDynamicList.this))
            {

                mArrListMyJobListModel.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mArrListMyJobListModel.size());

            }



        }


        class VersionViewHolder extends RecyclerView.ViewHolder {
            SwipeRevealLayout swipeLayout;
            LinearLayout llRowMain;
            TextView tvDelete,tvRowTitle, tvRowDesc;

            View deleteLayout;


            public VersionViewHolder(View itemView) {
                super(itemView);

                swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipeLayout);
                deleteLayout = itemView.findViewById(R.id.delete_layout);

                llRowMain = (LinearLayout) itemView.findViewById(R.id.llRowMain);
                tvRowTitle = (TextView) itemView.findViewById(R.id.tvRowTitle);
                tvRowDesc = (TextView) itemView.findViewById(R.id.tvRowDesc);
                tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);


            }

        }
    }
}
