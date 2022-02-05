package com.example.schneller;

import static com.example.schneller.DatabaseContract.BfarmData.SQL_COMBINED_SEARCH;
import static com.example.schneller.DatabaseContract.BfarmData.SQL_CREATE_ENTRIES;
import static com.example.schneller.DatabaseContract.BfarmData.SQL_DROP_TABLE;
import static com.example.schneller.DatabaseContract.BfarmData.SQL_GET_TABLE;
import static com.example.schneller.DatabaseContract.BfarmData.SQL_SEARCH_BY_TEST_ID;
import static com.example.schneller.DatabaseContract.BfarmData.SQL_SEARCH_QUERY;
import static com.example.schneller.DatabaseContract.EanData.SQL_SEARCH_EAN;
import static com.example.schneller.DatabaseContract.EanData.SQL_SINGLE_SEARCH_EAN;
import static com.example.schneller.DatabaseContract.SQL_LOAD_BY_TEST_ID;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BfarmDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 6 ;
    public static final String DATABASE_NAME = "bfarm.db";
    private Context context;
    private String DB_PATH;
    private static String DB_NAME = "bfarm.db";
    private String outFileName;
    private SQLiteDatabase db;

    public BfarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        ContextWrapper cw = new ContextWrapper(context);
        DB_PATH = cw.getFilesDir().getAbsolutePath() + "/databases/";
        outFileName = DB_PATH + DB_NAME;
        File file = new File(DB_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }


    public void onCreate(SQLiteDatabase db) {
        // db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL(SQL_DROP_TABLE);
        // onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // onUpgrade(db, oldVersion, newVersion);
    }

    public void createDatabase() throws IOException {
        boolean dbExists = checkDatabase();
        if (!dbExists) {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDatabase() {
        SQLiteDatabase dbCheck = null;
        try {
            dbCheck = SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException sqlEx) {
            try{
                copyDatabase();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }

        if (dbCheck != null) {
            dbCheck.close();
        }

        return dbCheck != null;
    }

    private void copyDatabase() throws IOException{
        byte[] buffer = new byte[1024];
        OutputStream output = null;
        int length;
        InputStream in = null;
        try {
            String stuff = Arrays.toString(context.getAssets().list("databases"));
            in = this.context.getAssets().open("databases/" + DB_NAME);
            output = new FileOutputStream(DB_PATH + DB_NAME);
            while((length = in.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.close();
            output.flush();
            in.close();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void openDatabase() throws SQLiteException {
        String dbPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public ArrayList<Testcheck> search(String keyword) {
        ArrayList<Testcheck> results = null;

        // new sql search:
        //  get every unique test_id where keyword mit ean, name, text, sonstwas matcht
        //  get all data from bfarm_table; ean too? maybe laterrr

        String padded_k = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(SQL_COMBINED_SEARCH, new String[]{padded_k, padded_k, padded_k, padded_k}); //, padded_k, padded_k, padded_k

        // first get test_id candidates
        if (cursor.moveToFirst())   {
            List<String> candidates = new ArrayList<String>();
            do {
                candidates.add(cursor.getString(0));
            } while (cursor.moveToNext());

            // fill result list
            results = new ArrayList<Testcheck>();
            for (String candidate:
                 candidates) {
                Cursor candidate_cursor = db.rawQuery(SQL_LOAD_BY_TEST_ID, new String[]{candidate});
                if (candidate_cursor.moveToFirst()) {
                    Testcheck tc = new Testcheck();
                    tc.setTest_id(candidate_cursor.getString(0));
                    tc.setName(candidate_cursor.getString(1));
                    tc.setManufacturer(candidate_cursor.getString(3));
                    tc.setPeiTested(candidate_cursor.getString(2).equals("Ja"));
                    tc.setSensitivity(asDouble(candidate_cursor.getString(8)));
                    tc.setSpecificity(asDouble(candidate_cursor.getString(10)));
                    tc.setLink(candidate_cursor.getString(12));
                    tc.setTest_id_pei(candidate_cursor.getString(14));
                    tc.setCq_25_30(asDouble(candidate_cursor.getString(15)));
                    tc.setCq_lt_25(asDouble(candidate_cursor.getString(16)));
                    tc.setCq_gt_30(asDouble(candidate_cursor.getString(17)));
                    tc.setTotal_sensitivity(asDouble(candidate_cursor.getString(18)));
                    results.add(tc);
                } else {
                    // db error
                }
            }
        }
        return results;
    }

    private double asDouble(String val) {
        if (val != null) {
            return Double.parseDouble(val.replace(",", "."));
        }
        return 0.0;
    }

    public Testcheck searchByEAN(String ean) {
        Testcheck testcheck = null;
        Cursor cursor = db.rawQuery(SQL_SINGLE_SEARCH_EAN, new String[]{ean});
        if (cursor.moveToFirst()) {
            testcheck = new Testcheck();
            testcheck.setTest_id(cursor.getString(0));
            testcheck.setName(cursor.getString(1));
            testcheck.setManufacturer(cursor.getString(3));
            testcheck.setPeiTested(cursor.getString(2).equals("Ja"));
            testcheck.setSensitivity(cursor.getDouble(8));
            testcheck.setSpecificity(cursor.getDouble(10));
            testcheck.setLink(cursor.getString(12));
        }
        return testcheck;
    }
}
