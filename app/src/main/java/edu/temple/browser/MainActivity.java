package edu.temple.browser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final int MAX_TABS = 20;

    ViewPager viewPager;
    EditText searchEditText;
    Button searchButton;
    ArrayList<WebFragment> fragments = new ArrayList<>();
    WebFragment currentFragment;
    String url;
    MyAdapter myAdapter;

    //TODO use onNewIntent for if the activity is already running and you open a link


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("IntentAction", intent.getAction());
        setIntent(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Service external requests
        Intent intent = getIntent();
        String action = intent.getAction();

        Log.d("IntentAction", action);

        // Instantiate the view pager that will display the browser tabs
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        myAdapter = new MyAdapter(getSupportFragmentManager(), fragments);

        WebFragment webFragment = new WebFragment();
        fragments.add(webFragment);
        myAdapter.notifyDataSetChanged();

        viewPager.setAdapter(myAdapter);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                WebFragment fragment = myAdapter.getItem(viewPager.getCurrentItem());
                if(fragment != null){
                    if(fragment.getArguments() != null) {
                        String oldUrl = fragment.getArguments().getString("url");
                        fragment.loadPage(oldUrl);
                        searchEditText.setText(oldUrl);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Instantiate the search edit text and button
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchButton = (Button) findViewById(R.id.search_button);

        // Search for the specified URL on click of the Go button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Action", "Search");
                url = searchEditText.getText().toString();

                // Check that the URL is not null or has a length of 0
                if(url != null && url.length() != 0) {
                    //search(url);
                    WebFragment fragment = myAdapter.getItem(viewPager.getCurrentItem());
                    WebView wv = fragment.webView;
                    wv.loadUrl(url);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    fragment.setArguments(bundle);
                }
            }
        });



        Uri data;
        if((data = getIntent().getData()) != null) {
            Log.d("IntentURL", data.toString());
            url = data.toString();
            search(url);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate the App Bar with the previous tab, next tab, and new tab buttons
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Generate a new tab
            case R.id.new_tab:
                Log.d("Action", "New Tab");
                viewPager.setCurrentItem(myAdapter.getTotal());
                searchEditText.setText("");
                return true;

            // Go to the next tab, if it exists
            case R.id.next_tab:
                Log.d("Action", "Next Tab");
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                return true;

            // Go to the previous tab, if it exists
            case R.id.previous_tab:
                Log.d("Action", "Previous Tab");
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyAdapter extends FragmentStatePagerAdapter{
        ArrayList<WebFragment> fragments;
        public MyAdapter(FragmentManager fragmentManager, ArrayList<WebFragment> fragments){
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public WebFragment getItem(int position) {
            // Create a new instance of the fragment and add it to the ArrayList of Fragments
            Log.d("fragments.size()", String.valueOf(fragments.size()));

            if(fragments.size() == 0){
                WebFragment webFragment = new WebFragment();
                fragments.add(webFragment);
                return webFragment;
            } else if(fragments.size() <= position) {
                Log.d("position_inside_if", String.valueOf(position));
                WebFragment webFragment = new WebFragment();
                fragments.add(webFragment);
                return webFragment;
            } else {
                Log.d("position_inside_else", String.valueOf(position));
                return fragments.get(position);
            }
        }

        @Override
        public int getCount() {
            return MAX_TABS;
        }

        public int getTotal(){
            return fragments.size()-1;
        }
    }

    // A function that loads the specified URL and displays it on the WebView of the Fragment
    private void search(String url) {
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString("url", url);

        currentFragment = fragments.get(viewPager.getCurrentItem());
        currentFragment.setArguments(fragmentBundle);
        //currentFragment.loadPage(url);
    }
}
