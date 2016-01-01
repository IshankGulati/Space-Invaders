package io.github.ishankgulati.spaceinvaders;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class SpaceInvadersActivity extends Activity {

    SpaceInvadersView spaceInvadersView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // get the screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // set the gameview to spaceInvadersView
        spaceInvadersView = new SpaceInvadersView(this, size);
        setContentView(spaceInvadersView);
    }

    // when our app goes out of focus
    @Override
    protected void onPause(){
        super.onPause();
        spaceInvadersView.pause();
    }

    // when our app gains focus
    @Override
    protected void onResume(){
        super.onResume();
        spaceInvadersView.resume();
    }

    // when activity is stopped
    @Override
    protected void onStop(){
        super.onStop();
        spaceInvadersView.stop();
    }
}
