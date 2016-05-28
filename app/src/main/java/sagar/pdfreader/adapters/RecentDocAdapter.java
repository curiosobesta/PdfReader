package sagar.pdfreader.adapters;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sagar.pdfreader.Globals;
import sagar.pdfreader.beans.RecentDoc;
import sagar.pdfreader.db.QueriesSessions;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import sagar.pdfreader.R;

/**
 * Created by Sagar on 25-03-2016.
 */


public class RecentDocAdapter{
    LinearLayout layout = null;
    Context context = null;
    ArrayList<RecentDoc> recentDocs;
    Callback callback = null;


    public RecentDocAdapter(Context context, LinearLayout layout, Callback callback){
        this.context = context;
        this.layout = layout;
        recentDocs = new QueriesSessions(context).getRecDocs();
        this.callback = callback;
    }

    public void addViews(){

        int count = 0;
        final ArrayList<View> views = new ArrayList<>();

        for (RecentDoc recentDoc : recentDocs) {
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.inner_start_rec_docs, null);
            LinearLayout cardView = (LinearLayout) view.findViewById(R.id.card_view);
            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);

            Typeface tf = Typeface.createFromAsset(context.getAssets(), Globals.font);
            tvName.setTypeface(tf);
            tvDate.setTypeface(tf);

            //Card View Background
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setBackground(context.getDrawable(R.drawable.ripple_selector));
            }
            //-- Card View Background

            //Add RecentDoc Name
            tvName.setText(new File(recentDoc.getName()).getName());
            layout.addView(view);
            //-- Add RecentDoc Name

            //Add RecentDoc Date And Time
            SimpleDateFormat format = new SimpleDateFormat("dd MMM, hh:mm a");
            String date = format.format(recentDoc.getDate());
            tvDate.setText(date);
            //-- Add RecentDoc Date And Time

            cardView.setId(count);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    RecentDoc rd = recentDocs.get(i);
                    callback.onCardClicked(rd.getName());
                }
            });

            views.add(view);
        }
    }

    public void notifyChange(){
        //recentDocs = new QueriesRecentDocs(context).getDocs();
        recentDocs = new QueriesSessions(context).getRecDocs();
        layout.removeAllViews();
        addViews();
    }

    public interface Callback{
        void onCardClicked(String fileName);
    }
}
