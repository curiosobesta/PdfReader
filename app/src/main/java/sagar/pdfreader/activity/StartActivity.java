package sagar.pdfreader.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import sagar.pdfreader.Globals;
import sagar.pdfreader.adapters.RecentDocAdapter;
import sagar.pdfreader.db.QueriesSessions;
import sagar.pdfreader.utils.ShareBucketBuilder;
import sagar.pdfreader.utils.Sleeper;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import sagar.pdfreader.R;

public class StartActivity
        extends AppCompatActivity
        implements RecentDocAdapter.Callback, Sleeper.Callback {

    private final int FILE_SELECT_CODE = 1;
    private RecentDocAdapter adapter = null;
    private AppBarLayout layout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        layout = (AppBarLayout) findViewById(R.id.app_bar);

        animatorWork();
        //-- Main Content
    }

    private void animatorWork(){

        getSupportActionBar().hide();

        final CollapsingToolbarLayout collapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setExpanded(false);

        new Sleeper(this).execute();
    }

    private void containerWork(){

        getSupportActionBar().show();

        final CollapsingToolbarLayout collapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final TextView tv = (TextView) findViewById(R.id.tvHeader);
        Typeface tf = Typeface.createFromAsset(getAssets(), Globals.font_bold);
        tv.setTypeface(tf);
        ((TextView)findViewById(R.id.textRecentFiles)).setTypeface(tf);

        layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(collapse.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapse)) {
                    tv.animate().alpha(1).setDuration(400);
                } else {
                    tv.animate().alpha(0).setDuration(400);
                }
            }
        });

        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Main Contents
        FloatingActionButton fabStartSession = (FloatingActionButton) findViewById(R.id.fabStart);
        fabStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show FileChooser
                Intent intent = new Intent(StartActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                intent.setType("mime/pdf");

                intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(intent, FILE_SELECT_CODE);
                //-- Show FileChooser
            }
        });

        //Set List Content
        LinearLayout llMain = (LinearLayout) findViewById(R.id.ll_recent_doc);
        adapter = new RecentDocAdapter(this, llMain, this);
        adapter.addViews();
        //-- Set List Content
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filePath = uri.getPath();

                    startPdfActivity(filePath);
                }
                break;
            default: Toast.makeText(this, "Cannot Start Session: Unsupported File Type", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPdfActivity(String filePath){

        //Insert file in Database
        Globals.sessionId = new QueriesSessions(this).add(filePath);
        //-- Insert file in Database

        adapter.notifyChange();

        //Start Another Activity
        Intent intent = new Intent(this, FileViewerActivityServer.class);
        Bundle basket = new Bundle();
        basket.putString("path", filePath);
        intent.putExtras(basket);
        startActivity(intent);
        //Start Another Activity
    }

    @Override
    public void onCardClicked(String filePath) {
        startPdfActivity(filePath);
    }

    @Override
    public void onWakeup() {
        //Expand AppLayout
        layout.setExpanded(true, true);
        //-- Expand AppLayout

        //Animate Splash Image
        findViewById(R.id.splash_img).animate().alpha(0).setDuration(300);
        //-- Animate Splash Image

        //Animate RecentDoc List
        LinearLayout ll= (LinearLayout) findViewById(R.id.ll_main);
        ll.setVisibility(View.VISIBLE);
        ll.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).setDuration(1200);
        ll.animate().alpha(1).setDuration(2000);
        //-- Animate RecentDoc List

        containerWork();

    }
}
