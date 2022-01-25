package com.example.schneller;

import android.provider.BaseColumns;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public final class DatabaseContract {
    private DatabaseContract() {}

    public static class BfarmData implements BaseColumns {
        public static final String TABLE_NAME = "bfarm_data";
        public static final String COLUMN_NAME_TEST_ID = "id";
        public static final String COLUMN_NAME_TEST_NAME = "name";
        public static final String COLUMN_NAME_TEST_MANUFACTURER = "manufacturer";
        public static final String COLUMN_NAME_PEI_TESTED = "pei_tested";
        public static final String COLUMN_NAME_SENSITIVITY = "sensitivity";

        // crate table
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + BfarmData.TABLE_NAME + " (" +
                BfarmData._ID + " INTEGER PRIMARY KEY," +
                BfarmData.COLUMN_NAME_TEST_ID + " TEXT," +
                BfarmData.COLUMN_NAME_TEST_NAME + " TEXT," +
                BfarmData.COLUMN_NAME_TEST_MANUFACTURER + " TEXT," +
                BfarmData.COLUMN_NAME_PEI_TESTED + " INTEGER," +
                BfarmData.COLUMN_NAME_SENSITIVITY + " REAL)";

        // delete table
        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + BfarmData.TABLE_NAME;

        // query
        public static final String SQL_SEARCH_QUERY =
                "SELECT * FROM " + BfarmData.TABLE_NAME + " WHERE " +
                BfarmData.COLUMN_NAME_TEST_ID + " LIKE ? OR " +
                BfarmData.COLUMN_NAME_TEST_NAME + " LIKE ? OR " +
                BfarmData.COLUMN_NAME_TEST_MANUFACTURER + " LIKE ?";

        // get table
        public static final String SQL_GET_TABLE =
                "SELECT * FROM " + BfarmData.TABLE_NAME + " WHERE 1=1";

        // search in bfarm_data and in ean_data
        public static final String SQL_COMBINED_SEARCH =
                "SELECT " + EanData.COLUMN_NAME_TEST_ID + " FROM " +
                        EanData.TABLE_NAME + " WHERE " +
                        EanData.COLUMN_NAME_EAN + " LIKE ?"
                + " UNION " +
                "SELECT " + BfarmData.COLUMN_NAME_TEST_ID + " FROM " +
                        BfarmData.TABLE_NAME + " WHERE " +
                        BfarmData.COLUMN_NAME_TEST_ID + " LIKE ? OR " +
                        BfarmData.COLUMN_NAME_TEST_NAME + " LIKE ? OR " +
                        BfarmData.COLUMN_NAME_TEST_MANUFACTURER + " LIKE ?";

        public static final String SQL_SEARCH_BY_TEST_ID =
                "SELECT * FROM " +
                BfarmData.TABLE_NAME + " WHERE " +
                BfarmData.COLUMN_NAME_TEST_ID + " = ?";

    }

    public static class EanData implements  BaseColumns {
        public static final String TABLE_NAME = "ean_data";
        public static final String COLUMN_NAME_TEST_ID = "test_id";
        public static final String COLUMN_NAME_EAN = "ean";
        public static final String COLUMN_NAME_INFO = "info";

        // query one
        public static final String SQL_SINGLE_SEARCH_EAN =
                "SELECT * FROM " +
                BfarmData.TABLE_NAME +
                " LEFT OUTER JOIN " + EanData.TABLE_NAME + " ON " +
                BfarmData.TABLE_NAME + "." + BfarmData.COLUMN_NAME_TEST_ID + " = " +
                EanData.TABLE_NAME + "." + EanData.COLUMN_NAME_TEST_ID + " WHERE " +
                EanData.COLUMN_NAME_EAN + " = ?";

        // query multiple
        public static final String SQL_SEARCH_EAN =
                "SELECT * FROM " + EanData.TABLE_NAME + " WHERE " +
                EanData.COLUMN_NAME_EAN + " LIKE ?";

    }
}
