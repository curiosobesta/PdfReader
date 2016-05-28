package sagar.pdfreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import sagar.pdfreader.Globals;
import sagar.pdfreader.beans.RecentDoc;
import sagar.pdfreader.beans.SessionDetails;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sagar on 18-04-2016.
 */
public class QueriesSessions {
    private DbInit dbCon;

    public QueriesSessions(Context context) {
        dbCon = new DbInit(context);
    }

    public int add(String filePath) {

        //Open connection to write data
        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();

        String currentTime = Long.toString(System.currentTimeMillis());
        values.put( Globals.RF_DATE , currentTime);
        values.put( Globals.SESSIONS_DOC , filePath);

        // Inserting Row
        long id;
        try {
            id = db.insert(Globals.TABLE_SESSIONS, null, values);
        }
        catch (SQLiteConstraintException exception){
            id = -1;
        }
        finally {
            db.close(); // Closing database connection
        }
        if(id == -1){
            id = update(filePath);
        }
        return (int) id;
    }

    public int update(String filePath) {

        SQLiteDatabase db = dbCon.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Globals.SESSIONS_DATE, new Date().getTime() + "");

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Globals.TABLE_SESSIONS, values, Globals.SESSIONS_DOC + "= ?", new String[]{filePath});
        String findSessionId =  "SELECT  " +
                Globals.SESSIONS_ID +
                " FROM " + Globals.TABLE_SESSIONS +
                " WHERE " + Globals.SESSIONS_DOC + " = '"+filePath +"'"+
                " ORDER BY " + Globals.SESSIONS_DATE + " DESC";
        Cursor cursor = db.rawQuery(findSessionId, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(Globals.SESSIONS_ID));

        cursor.close();
        db.close(); // Closing database connection

        return id;
    }

    public ArrayList<RecentDoc> getRecDocs() {
        //Open connection to read only
        SQLiteDatabase db = dbCon.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Globals.SESSIONS_ID +", " +
                Globals.SESSIONS_DOC +", " +
                Globals.SESSIONS_DATE +
                " FROM " + Globals.TABLE_SESSIONS +
                " ORDER BY " + Globals.SESSIONS_DATE + " DESC";

        //Student student = new Student();
        ArrayList<RecentDoc> al = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                RecentDoc recentDoc = new RecentDoc();
                recentDoc.setSessionId(cursor.getInt(cursor.getColumnIndex(Globals.SESSIONS_ID)));
                recentDoc.setName(cursor.getString(cursor.getColumnIndex(Globals.SESSIONS_DOC)));
                recentDoc.setDate(
                        new Date(
                                cursor.getLong(cursor.getColumnIndex(Globals.SESSIONS_DATE))
                        )
                );
                al.add(recentDoc);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return al;
    }
}
