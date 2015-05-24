package com.dalogax.sportevents;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.util.Date;
import java.util.List;

public class EventActivity extends Activity {

    EventInfo event;
    ParseObject actualEventAssist;

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
            Button buttonSingup = (Button) findViewById(R.id.button_singup);
            buttonSingup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getSinguplink()));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            manageVoyButton();
        }
    }

    private void manageVoyButton(){
        Button buttonVoy = (Button) findViewById(R.id.button_voy);
        if (AccessToken.getCurrentAccessToken()!=null){
            buttonVoy.setVisibility(View.VISIBLE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("EventAssist");
            query.whereEqualTo("user", AccessToken.getCurrentAccessToken().getUserId());
            query.whereEqualTo("event", event.getObjectId());
            query.setLimit(1);
            List<ParseObject> eventAssistList = null;
            try {
                eventAssistList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (eventAssistList!=null && eventAssistList.size()>0) {
                actualEventAssist = eventAssistList.get(0);
                buttonVoy.setText(getText(R.string.button_no_voy));
                buttonVoy.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("Button Voy", AccessToken.getCurrentAccessToken().getUserId() + " ya no va al evento " + event.getObjectId());
                        actualEventAssist.deleteInBackground(new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    manageVoyButton();
                                } else {
                                    Log.d("Parse", "Error al eliminar asistencia");
                                }
                            }
                        });
                    }
                });
            }else{
                buttonVoy.setText(getText(R.string.button_voy));
                buttonVoy.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("Button Voy", AccessToken.getCurrentAccessToken().getUserId() + " va al evento " + event.getObjectId());
                        ParseObject po = new ParseObject("EventAssist");
                        po.put("user", AccessToken.getCurrentAccessToken().getUserId());
                        po.put("event", event.getObjectId());
                        po.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    manageVoyButton();
                                } else {
                                    Log.d("Parse", "Error al guardar asistencia");
                                }
                            }
                        });
                    }
                });
            }
        }
        else {
            buttonVoy.setVisibility(View.INVISIBLE);
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
