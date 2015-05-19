package com.dalogax.sportevents;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class EventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        EventInfo event = (EventInfo) intent.getExtras().getSerializable(MainActivity.event);
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
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
