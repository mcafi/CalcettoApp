package com.mcafi.calcetto.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbCreator {


        // database constants
        public static final String DB_NAME = "calcetto.db";
        public static final int    DB_VERSION = 2;

        // user table constants
        public static final String USER_TABLE = "user";

        public static final String USER_ID = "id_user";
        public static final int    USER_ID_COL = 0;

        public static final String USER_NOTIFY = "notify";
        public static final int    USER_NOTIFY_COL = 1;

        // match table constants
        public static final String MATCH_TABLE = "partita";

       public static final String MATCH_TABLE_ID = "id_table_partita";
       public static final int    MATCH_TABLE_ID_COL = 0;

        public static final String MATCH_ID = "id_match";
        public static final int    MATCH_ID_COL = 1;

        public static final String MATCH_USER_ID = "id_user";
        public static final int    MATCH_USER_ID_COL = 2;

        public static final String MATCH_NOTIFY = "notify";
        public static final int    MATCH_NOTIFY_COL = 3;


        // CREATE and DROP TABLE statements
        public static final String CREATE_USER_TABLE =
                "CREATE TABLE " + USER_TABLE + " (" +
                        USER_ID   + " STRING PRIMARY KEY, " +
                        USER_NOTIFY + " INTEGER);";

        public static final String CREATE_MATCH_TABLE =
                "CREATE TABLE " + MATCH_TABLE + " (" +
                        MATCH_TABLE_ID         + " STRING PRIMARY KEY, " +
                        MATCH_ID         + " STRING, " +
                        MATCH_USER_ID    + " STRING, " +
                        MATCH_NOTIFY       + " INTEGER    NOT NULL);";

        public static final String DROP_USER_TABLE =
                "DROP TABLE IF EXISTS " + USER_TABLE;

        public static final String DROP_MATCH_TABLE = "DROP TABLE IF EXISTS "+ MATCH_TABLE;

        private static class DBHelper extends SQLiteOpenHelper {

            public DBHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
                super(context, name, factory, version);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                // create tables
                db.execSQL(CREATE_USER_TABLE);
                db.execSQL(CREATE_MATCH_TABLE);

                // insert default lists
            /*db.execSQL("INSERT INTO list VALUES (1, 'Personal')");
            db.execSQL("INSERT INTO list VALUES (2, 'Business')");

            // insert sample tasks
            db.execSQL("INSERT INTO task VALUES (1, 1, 'Pay bills', " +
                    "'Rent\nPhone\nInternet', '0', '0')");
            db.execSQL("INSERT INTO task VALUES (2, 1, 'Get hair cut', " +
                    "'', '0', '0')");*/
            }

            @Override
            public void onUpgrade(SQLiteDatabase db,
                                  int oldVersion, int newVersion) {

                Log.d("Task list", "Upgrading db from version "
                        + oldVersion + " to " + newVersion);

                db.execSQL(DbCreator.DROP_USER_TABLE);
                db.execSQL(DbCreator.DROP_MATCH_TABLE);
                onCreate(db);
            }
        }

        // database and database helper objects
        private SQLiteDatabase db;
        private DBHelper dbHelper;

        // constructor
        public DbCreator(Context context) {
            dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        }

        // private methods
        private void openReadableDB() {
            db = dbHelper.getReadableDatabase();
        }

        private void openWriteableDB() {
            db = dbHelper.getWritableDatabase();
        }

        private void closeDB() {
            if (db != null)
                db.close();
        }
        public void execQuery(String query){
            db.execSQL(query);
        }

        public int getUser(String id) {
            String where = USER_ID + "= ?";
            String[] whereArgs = { id };

            openReadableDB();
            Cursor cursor = db.query(USER_TABLE, null,
                    where, whereArgs, null, null, null);
            int user_id=-1;

            cursor.moveToFirst();

            if(cursor.getCount()!=0){
                user_id = cursor.getInt(USER_NOTIFY_COL);
            }
            if(cursor!=null)cursor.close();

            return user_id;
        }

        public int getMatch(String id_partita) {
            String where = MATCH_TABLE_ID + "= ?";
            String[] whereArgs = { id_partita };

            this.openReadableDB();
            Cursor cursor = db.query(MATCH_TABLE,
                    null, where, whereArgs, null, null, null);
            int notify=-1;
            cursor.moveToFirst();

            if(cursor.getCount()!=0){
                notify = cursor.getInt(MATCH_NOTIFY_COL);
            }
            if(cursor!=null)cursor.close();

            return notify;
        }

        /*public long insertTask(Task task) {
            ContentValues cv = new ContentValues();
            cv.put(TASK_LIST_ID, task.getListId());
            cv.put(TASK_NAME, task.getName());
            cv.put(TASK_NOTES, task.getNotes());
            cv.put(TASK_COMPLETED, task.getCompletedDate());
            cv.put(TASK_HIDDEN, task.getHidden());

            this.openWriteableDB();
            long rowID = db.insert(TASK_TABLE, null, cv);
            this.closeDB();

            return rowID;
        }*/

        /*public int updateTask(Task task) {
            ContentValues cv = new ContentValues();
            cv.put(TASK_LIST_ID, task.getListId());
            cv.put(TASK_NAME, task.getName());
            cv.put(TASK_NOTES, task.getNotes());
            cv.put(TASK_COMPLETED, task.getCompletedDate());
            cv.put(TASK_HIDDEN, task.getHidden());

            String where = TASK_ID + "= ?";
            String[] whereArgs = { String.valueOf(task.getId()) };

            this.openWriteableDB();
            int rowCount = db.update(TASK_TABLE, cv, where, whereArgs);
            this.closeDB();

            return rowCount;
        }*/

        /*public int deleteTask(long id) {
            String where = TASK_ID + "= ?";
            String[] whereArgs = { String.valueOf(id) };

            this.openWriteableDB();
            int rowCount = db.delete(TASK_TABLE, where, whereArgs);
            this.closeDB();

            return rowCount;
        }*/

}
