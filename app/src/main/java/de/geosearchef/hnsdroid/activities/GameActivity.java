package de.geosearchef.hnsdroid.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import de.geosearchef.hnsdroid.R;

public class GameActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    GameMapFragment mapFragment = new GameMapFragment();
    GameHomeFragment homeFragment = new GameHomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        bottomNavigationView = findViewById(R.id.gameBottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.gameFragmentContainer, mapFragment)
                .commit();

        bottomNavigationView.setSelectedItemId(0);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_map:
                    selectedFragment = mapFragment;
                    break;
                case R.id.nav_home:
                    selectedFragment = homeFragment;
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.gameFragmentContainer, selectedFragment)
                    .commit();

            return true;
        }
    };

    @Override
    public void onBackPressed() {
       //do not call super
    }
}
