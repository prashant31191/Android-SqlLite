package com.dynamicsqllite.view;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dynamicsqllite.ActBase;
import com.dynamicsqllite.App;
import com.dynamicsqllite.R;
import com.dynamicsqllite.database.DynamicDatabaseHelper;
import com.dynamicsqllite.model.Post;
import com.dynamicsqllite.model.User;

import java.util.List;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public class ActStudentList extends ActBase
{

    // In any activity just pass the context and use the singleton method
    DynamicDatabaseHelper helper;

    SwipeRefreshLayout srLayout;
    RecyclerView recyclerView;
    FloatingActionButton fabAdd;


    @Override
    protected int baseViewData() {
        return R.layout.act_stud_list;
    }

    @Override
    protected void baseSetData() {
        initSubViews();
        initClickEvents();

        helper = DynamicDatabaseHelper.getInstance(this);

        List<Post>  postList = getPopstList();

        for (int i=0; i< postList.size(); i++)
        {
            App.showLog("====text="+postList.get(i).text);
            App.showLog("====user="+postList.get(i).user);
        }

    }

    private void initSubViews() {
        srLayout = _findViewById(R.id.srLayout);
        recyclerView = _findViewById(R.id.recyclerView);
        fabAdd = _findViewById(R.id.fabAdd);
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
     //   DynamicDatabaseHelper databaseHelper = DynamicDatabaseHelper.getInstance(this);

        // Add sample post to the database
        helper.addPost(samplePost);

        // Get all posts from database
        List<Post> posts = helper.getAllPosts();
        for (Post post : posts) {
            // do something
            App.showLog("====post==="+post.text);
        }
    }
}
