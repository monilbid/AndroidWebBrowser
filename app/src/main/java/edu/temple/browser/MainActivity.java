package edu.temple.browser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    EditText searchEditText;
    Button searchButton;
    ArrayList<WebFragment> fragments = new ArrayList<>();
    int hashSizeIndex = 0;
    private int NUM_TABS = 1;
    PagerAdapter pagerAdapter;
    WebFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the view pager that will display the browser tabs
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        // Instantiate the search edit text and button
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchButton = (Button) findViewById(R.id.search_button);

        // Search for the specified URL on click of the Go button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Action", "Search");
                String url = searchEditText.getText().toString();

                // Check that the URL is not null or has a length of 0
                if(url != null && url.length() != 0) {
                    search(url);
                }
            }
        });
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
                WebFragment webFragment = new WebFragment();
                fragments.add(hashSizeIndex, webFragment);
                NUM_TABS++;
                pagerAdapter = viewPager.getAdapter();
                pagerAdapter.notifyDataSetChanged();
                currentFragment = webFragment;
                viewPager.setCurrentItem(hashSizeIndex);
                searchEditText.setText("");
                return true;

            // Go to the next tab, if it exists
            case R.id.next_tab:
                Log.d("Action", "Next Tab");
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                if(currentFragment != null){
                    Log.d("Current URL", fragments.get(viewPager.getCurrentItem()).getURL());
                    searchEditText.setText(fragments.get(viewPager.getCurrentItem()).getURL());
                }
                return true;

            // Go to the previous tab, if it exists
            case R.id.previous_tab:
                Log.d("Action", "Previous Tab");
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                if(currentFragment != null){
                    Log.d("Current URL", fragments.get(viewPager.getCurrentItem()).getURL());
                    searchEditText.setText(fragments.get(viewPager.getCurrentItem()).getURL());
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyAdapter extends FragmentStatePagerAdapter{

        public MyAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            // Create a new instance of the fragment and add it to the ArrayList of Fragments
            WebFragment webFragment = new WebFragment();
            fragments.add(hashSizeIndex, webFragment);
            hashSizeIndex++;
            return webFragment;
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }
    }

    // A function that loads the specified URL and displays it on the WebView of the Fragment
    private void search(String url) {
        currentFragment = fragments.get(viewPager.getCurrentItem());
        currentFragment.loadPage(url);
    }
}
