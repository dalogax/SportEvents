package com.dalogax.sportevents;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.parse.ParseObject;

import java.io.File;

public class EventActivity extends Activity {

    EventInfo event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        event = (EventInfo) intent.getExtras().getSerializable(MainActivity.event);
        setContentView(R.layout.activity_event);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (event!=null){
            ActionBar actionBar = getActionBar();
            actionBar.setTitle(event.title);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File imgFile=new File(directory,event.getObjectId()+".jpg");
            if(imgFile.exists()){
                Bitmap image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ImageView imageview = (ImageView) findViewById(R.id.e_image);
                imageview.setImageBitmap(image);
            }
            TextView eDesc = (TextView) findViewById(R.id.e_description);
            eDesc.setText(event.description);
            TextView eDate = (TextView) findViewById(R.id.e_date);
            eDate.setText(event.getDate());
            TextView eTitle = (TextView) findViewById(R.id.e_title);
            eTitle.setText(event.getTitle());
            Button buttonVoy = (Button) findViewById(R.id.button_voy);
            if (AccessToken.getCurrentAccessToken()!=null){
                buttonVoy.setVisibility(View.VISIBLE);
                buttonVoy.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("Button Voy", AccessToken.getCurrentAccessToken().getUserId() + " va al evento " + event.getObjectId());
                        ParseObject gameScore = new ParseObject("EventAssist");
                        gameScore.put("user", AccessToken.getCurrentAccessToken().getUserId());
                        gameScore.put("event", event.getObjectId());
                        gameScore.saveInBackground();
                    }
                });
            }
            else {
                buttonVoy.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
