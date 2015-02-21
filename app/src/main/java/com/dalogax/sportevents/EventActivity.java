package com.dalogax.sportevents;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class EventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        long id = intent.getLongExtra(MainActivity.eventId, 0);

        setContentView(R.layout.activity_event);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (id>0){
            EventInfo event = MainActivity.mockList.get((int) id);
            ActionBar actionBar = getActionBar();
            actionBar.setTitle(event.title);
            ImageView image = (ImageView) findViewById(R.id.e_image);
            image.setImageResource(R.drawable.race1);
            TextView eDesc = (TextView) findViewById(R.id.e_description);
            eDesc.setText(event.description);
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
