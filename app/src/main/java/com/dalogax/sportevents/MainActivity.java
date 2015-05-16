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
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public EventDataSource eventDataSource;

    RecyclerView recList = null;

    public static String event = "EVENT";

    public List<EventInfo> listed = new ArrayList<EventInfo>();

    public List<String> categories;

    public int selectedCateogry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (categories==null){
            loadCategories();
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //getEventDataSource().deleteAllEvents();
        obtainEventsFromCloud();

        //if (getEventDataSource().getAllEvents().size()==0) {
        //    createMockList(12);
        //}

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        listed = getEventDataSource().getAllEvents();
        EventAdapter ca = new EventAdapter(listed);
        getRecList().setAdapter(ca);
    }

    private void obtainEventsFromCloud() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventInfo");
        query.orderByAscending("date");
        query.setLimit(50);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    Log.d("Parse", "Retrieved " + eventList.size() + " events");
                    saveEventsInDB(eventList);
                    loadSelectedCategory();
                } else {
                    Log.d("Parse", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void saveEventsInDB(List<ParseObject> eventList){
        for (ParseObject obj : eventList){
            if (getEventDataSource().getEvent(obj.getObjectId())==null) {
                EventInfo ci = new EventInfo();
                ci.setObjectId(obj.getObjectId());
                ci.setTitle(obj.getString("title"));
                ci.setDescription(obj.getString("description"));
                ci.setCategory(obj.getInt("category"));
                ci.setDate(obj.getDate("date").toString());
                final EventInfo newEvent = getEventDataSource().createEvent(ci);
                if (newEvent != null) {
                    ParseFile imageFile = obj.getParseFile("image");
                    imageFile.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                saveImageToInternalStorage(bitmap, newEvent.getObjectId());
                            } else {
                                Log.d("Parse", "Error al obtener el fichero de imagen");
                            }
                        }
                    });
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmapImage,String objectId){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,objectId+".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private void createMockList(int size) {
        getEventDataSource().deleteAllEvents();
        for (int i=1; i <= size; i++) {
            EventInfo ci = new EventInfo();
            ci.title = "SampleEvent"+i;
            ci.description = "text sample";
            ci.category = (i%5)+1;
            getEventDataSource().createEvent(ci);
        }
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
        // update the main content by replacing fragments
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
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();


            // Associate searchable configuration with the SearchView
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
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

}
