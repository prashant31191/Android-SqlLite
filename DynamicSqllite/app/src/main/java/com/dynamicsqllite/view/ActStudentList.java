package com.dynamicsqllite.view;

import com.dynamicsqllite.ActBase;
import com.dynamicsqllite.App;
import com.dynamicsqllite.ParentActivity;
import com.dynamicsqllite.R;
import com.dynamicsqllite.database.PostsDatabaseHelper;
import com.dynamicsqllite.model.Post;
import com.dynamicsqllite.model.User;

import java.util.List;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public class ActStudentList extends ActBase
{

    // In any activity just pass the context and use the singleton method
    PostsDatabaseHelper helper;



    @Override
    protected int baseViewData() {
        return R.layout.act_splash;
    }

    @Override
    protected void baseSetData() {
        helper = PostsDatabaseHelper.getInstance(this);
        insertData();

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
     //   PostsDatabaseHelper databaseHelper = PostsDatabaseHelper.getInstance(this);

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
