package com.dalogax.sportevents;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.HashMap;
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

        eventDataSource = new EventDataSource(this);
        eventDataSource.open();
        if (eventDataSource.getAllEvents().size()==0) {
            createMockList(12);
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getBaseContext(), EventActivity.class);
                        intent.putExtra(event, eventDataSource.getEvent(listed.get(position).getId()));
                        startActivity(intent);
                    }
                })
        );
        listed = eventDataSource.getAllEvents();
        EventAdapter ca = new EventAdapter(listed);
        recList.setAdapter(ca);
    }

    private void loadSelectedCategory(){
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        if (selectedCateogry>0) {
            listed = eventDataSource.getEventsByCategory(selectedCateogry);
        }
        else{
            listed = eventDataSource.getAllEvents();
        }
        EventAdapter ca = new EventAdapter(listed);
        recList.setAdapter(ca);
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

    private void createMockList(int size) {
        eventDataSource.deleteAllEvents();
        for (int i=1; i <= size; i++) {
            EventInfo ci = new EventInfo();
            ci.title = "SampleEvent"+i;
            ci.description = "text sample";
            ci.category = (i%5)+1;
            eventDataSource.createEvent(ci);
        }
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
