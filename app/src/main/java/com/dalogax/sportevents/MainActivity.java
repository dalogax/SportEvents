package com.dalogax.sportevents;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    public EventDataSource eventDataSource;

    RecyclerView recList = null;

    public static String event = "EVENT";

    public List<EventInfo> listed = new ArrayList<EventInfo>();

    public List<String> categories;

    public int selectedCateogry = 0;

    private ProgressBar spinner;

    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (categories==null){
            loadCategories();
        }

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (spinner==null) {
            spinner = (ProgressBar) findViewById(R.id.progressBar1);
        }
        spinner.setVisibility(View.VISIBLE);
        new DownloadDataTask().execute();

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(Gravity.START);

        initializeFacebookButton();
    }

    private void initializeFacebookButton() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook","Login success");
            }

            @Override
            public void onCancel() {
                Log.d("Facebook","Login canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Facebook","Login error");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loadSelectedCategory(){
        listed = getEventDataSource().searchEventsByCategory(selectedCateogry,null);
        EventAdapter ca = new EventAdapter(listed);
        getRecList().setAdapter(ca);
    }

    private void loadSearchByName(String term){
        listed = getEventDataSource().searchEventsByCategory(selectedCateogry,term);
        EventAdapter ca = new EventAdapter(listed);
        getRecList().setAdapter(ca);
    }

    private EventDataSource getEventDataSource(){
        if(eventDataSource==null){
            eventDataSource = new EventDataSource(this);
            eventDataSource.open();
        }
        return eventDataSource;
    }

    private RecyclerView getRecList() {
        if (recList==null) {
            recList = (RecyclerView) findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);

            recList.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getBaseContext(), EventActivity.class);
                            intent.putExtra(event, getEventDataSource().getEvent(listed.get(position).getId()));
                            startActivity(intent);
                        }
                    })
            );
        }
        return recList;
    }

    private void loadCategories() {
        categories = new ArrayList<>();
        categories.add(getString(R.string.app_name));
        categories.add(getString(R.string.title_section1));
        categories.add(getString(R.string.title_section2));
        categories.add(getString(R.string.title_section3));
        categories.add(getString(R.string.title_section4));
        categories.add(getString(R.string.title_section5));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        mTitle=getSectionTitle(number);

    }

    private CharSequence getSectionTitle(int number) {
        int cat=number-1;
        selectedCateogry = cat;
        loadSelectedCategory();
        return categories.get(cat);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    loadSearchByName(newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    loadSearchByName(query);
                    return true;
                }
            };

            searchView.setOnQueryTextListener(queryTextListener);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (AccessToken.getCurrentAccessToken()!=null){
            new AfterLoginTask().execute();
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            TextView textFacebook = (TextView) findViewById(R.id.text_facebook);
                            try {
                                textFacebook.setText(getString(R.string.logged_as) + " " + object.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
        else{
            TextView textFacebook = (TextView) findViewById(R.id.text_facebook);
            textFacebook.setText(getString(R.string.text_facebook));
            ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
            profilePic.setVisibility(View.INVISIBLE);
        }
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private class DownloadDataTask extends AsyncTask<String, Float, Integer> {
        protected Integer doInBackground(String... data) {
            obtainEventsFromCloud();
            return 0;
        }

        protected void onPostExecute(Integer bytes){
            loadSelectedCategory();
            spinner.setVisibility(View.GONE);
        }

        private void obtainEventsFromCloud() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("EventInfo");
            query.orderByAscending("date");
            query.whereGreaterThanOrEqualTo("date", new Date());
            query.setLimit(50);
            List<ParseObject> eventList = null;
            try {
                eventList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            saveEventsInDB(eventList);
        }

        private void saveEventsInDB(List<ParseObject> eventList){
            for (ParseObject obj : eventList){
                if (getEventDataSource().getEvent(obj.getObjectId())==null) {
                    EventInfo ci = new EventInfo();
                    ci.setObjectId(obj.getObjectId());
                    ci.setTitle(obj.getString("title"));
                    ci.setDescription(obj.getString("description"));
                    ci.setSinguplink(obj.getString("singuplink"));
                    ci.setCategory(obj.getInt("category"));
                    ci.setDate(obj.getDate("date").toString());
                    final EventInfo newEvent = getEventDataSource().createEvent(ci);
                    if (newEvent != null) {
                        ParseFile imageFile = obj.getParseFile("image");
                        byte[] data = new byte[0];
                        try {
                            data = imageFile.getData();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        saveImageToInternalStorage(bitmap, newEvent.getObjectId());
                    }
                }
            }
        }

        private String saveImageToInternalStorage(Bitmap bitmapImage,String objectId){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File mypath=new File(directory,objectId+".jpg");
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(mypath);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return directory.getAbsolutePath();
        }
    }

    private class AfterLoginTask extends AsyncTask<String, Float, Bitmap> {
        protected Bitmap doInBackground(String... data) {
            String url = "https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large";
            return GetBitmapfromUrl(url);
        }

        public Bitmap GetBitmapfromUrl(String scr) {
            try {
                URL url=new URL(scr);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input=connection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(input);
                return bmp;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap bmp) {
            ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
            profilePic.setImageBitmap(bmp);
            profilePic.setVisibility(View.VISIBLE);
        }
    }
}
