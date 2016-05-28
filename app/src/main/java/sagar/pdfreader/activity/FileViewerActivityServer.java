package sagar.pdfreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.joanzapata.pdfview.PDFView;

import java.io.File;

import sagar.pdfreader.R;

public class FileViewerActivityServer extends AppCompatActivity {

    PDFView.Configurator con;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    //Main Code

        //Get Data from previous Activity
        Bundle basket = getIntent().getExtras();
        String path = basket.getString("path");
        //-- Get Data from previous Activity

        //Open Pdf
        pdfView = (PDFView) findViewById(R.id.pdfviewLib);
        File myFile = new File(path);
        con = pdfView.fromFile(myFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .swipeVertical(true);
        con.load();
        //-- Open Pdf

    //-- Main Code

    }
}
