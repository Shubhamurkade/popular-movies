package com.example.shubham.popularmovies;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnArticleSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            MainActivityFragment firstFragment = new MainActivityFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();

        }

    }

    @Override
    public void onArticleSelected(String movieClicked, boolean dualPane) {

            Fragment singleMovieFragment = SingleMovieFragment.newInstance(movieClicked, dualPane);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            Bundle args = new Bundle();
            args.putString("movieClicked", movieClicked);
            args.putBoolean("isDual", dualPane);
            singleMovieFragment.setArguments(args);

            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics ();
            display.getMetrics(outMetrics);

            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;

            if(dpWidth < 600 )
            {
                ft.addToBackStack(null);
                ft.replace(R.id.fragment_container, singleMovieFragment);
            }
            else
            {
                ft.replace(R.id.frag_single_activity, singleMovieFragment);
            }


            ft.commit();

        }
}
