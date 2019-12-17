package com.testechnique.minifacebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.GridView;
import android.widget.Toast;
import com.testechnique.minifacebook.storage.SharedPrefManager;
import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    static Intent intent;
    Toolbar toolbar;
    GridView simpleGrid;
    Bitmap bitmap;
    String imageURL,id;
    CustomAdapter customAdapter;
    int logos[] = {R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo,
            R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo,
            R.drawable.logo, R.drawable.logo, R.drawable.logo, R.drawable.logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        simpleGrid = (GridView) findViewById(R.id.simpleGridView);
        customAdapter = new CustomAdapter(getApplicationContext(), logos);
        simpleGrid.setAdapter(customAdapter);
        intent = getIntent();
        if (intent != null) {
            id ="";
            if (intent.hasExtra("userId")) {

                id = (String) intent.getStringExtra("userId");
                Log.i("userID",id);
            }
        }
        try {
            bitmap = getFacebookProfilePicture(id);
            Toast.makeText(MainActivity.this," "+bitmap.getGenerationId(),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static Bitmap getFacebookProfilePicture(String userID) throws Exception
    {
        String imageURL;

        Bitmap bitmap;
        imageURL = "http://graph.facebook.com/"+userID+"/picture?type=large";
        InputStream in = (InputStream) new URL(imageURL).getContent();
        bitmap = BitmapFactory.decodeStream(in);

        return bitmap;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId())
        {
            case R.id.deconnexion:
                deconnexion();
                return true;

        }
        return true;

    }








    private void deconnexion() {
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            SharedPrefManager.getInstance(MainActivity.this).clear();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


}
