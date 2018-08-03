package com.example.tiny.covertphonenum.model.databasehelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tiny.covertphonenum.model.models.Prefix;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DB_Prefix";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PREFIX = "Prefix";
    private static final String COLUMN_PREFIX_ID = "Prefix_Id";
    private static final String COLUMN_PREFIX_OLD = "Prefix_Title";
    private static final String COLUMN_PREFIX_NEW = "Prefix_Content";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script tạo bảng.
        String script = "CREATE TABLE " + TABLE_PREFIX + "("
                + COLUMN_PREFIX_ID + " INTEGER PRIMARY KEY," + COLUMN_PREFIX_OLD + " TEXT,"
                + COLUMN_PREFIX_NEW + " TEXT" + ")";
        // Chạy lệnh tạo bảng.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Hủy (drop) bảng cũ nếu nó đã tồn tại.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFIX);
        // Và tạo lại.
        onCreate(db);
    }

    public void createDefaultPrefixsIfNeed() {
        int count = this.getPrefixsCount();
        if (count == 0) {
            Prefix prefix1 = new Prefix(1,"0120", "070");
            Prefix prefix2 = new Prefix(2,"0121", "079");
            Prefix prefix3 = new Prefix(3,"0122", "077");
            Prefix prefix4 = new Prefix(4,"0126", "076");
            Prefix prefix5 = new Prefix(5,"0128", "078");
            Prefix prefix6 = new Prefix(6,"0123", "083");
            Prefix prefix7 = new Prefix(7,"0124", "084");
            Prefix prefix8 = new Prefix(8,"0125", "085");
            Prefix prefix9 = new Prefix(9,"0127", "081");
            Prefix prefix10 = new Prefix(10,"0129", "082");
            Prefix prefix11 = new Prefix(11,"0162", "032");
            Prefix prefix12 = new Prefix(12,"0163", "033");
            Prefix prefix13 = new Prefix(13,"0164", "034");
            Prefix prefix14 = new Prefix(14,"0165", "035");
            Prefix prefix15 = new Prefix(15,"0166", "036");
            Prefix prefix16 = new Prefix(16,"0167", "037");
            Prefix prefix17 = new Prefix(17,"0168", "038");
            Prefix prefix18 = new Prefix(18,"0169", "039");
            Prefix prefix19 = new Prefix(19,"0186", "056");
            Prefix prefix20 = new Prefix(20,"0188", "058");
            Prefix prefix21 = new Prefix(21,"0199", "059");
            this.addPrefix(prefix1);
            this.addPrefix(prefix2);
            this.addPrefix(prefix3);
            this.addPrefix(prefix4);
            this.addPrefix(prefix5);
            this.addPrefix(prefix6);
            this.addPrefix(prefix7);
            this.addPrefix(prefix8);
            this.addPrefix(prefix9);
            this.addPrefix(prefix10);
            this.addPrefix(prefix11);
            this.addPrefix(prefix12);
            this.addPrefix(prefix13);
            this.addPrefix(prefix14);
            this.addPrefix(prefix15);
            this.addPrefix(prefix16);
            this.addPrefix(prefix17);
            this.addPrefix(prefix18);
            this.addPrefix(prefix19);
            this.addPrefix(prefix20);
            this.addPrefix(prefix21);       }
    }

    public void addPrefix(Prefix Prefix) {
        Log.i(TAG, "MyDatabaseHelper.addPrefix ... " + Prefix.getNewPre());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PREFIX_ID, Prefix.getId());
        values.put(COLUMN_PREFIX_OLD, Prefix.getOldPRe());
        values.put(COLUMN_PREFIX_NEW, Prefix.getNewPre());
        db.insert(TABLE_PREFIX, null, values);
        db.close();
    }

    @SuppressLint("Recycle")
    public Prefix getPrefix(int id) {
        Log.i(TAG, "MyDatabaseHelper.getPrefix ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor;
        cursor = db.query(TABLE_PREFIX, new String[]{COLUMN_PREFIX_ID,
                        COLUMN_PREFIX_OLD, COLUMN_PREFIX_NEW}, COLUMN_PREFIX_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        return new Prefix(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
    }

    public List<Prefix> getAllPrefix() {
        Log.i(TAG, "MyDatabaseHelper.getAllPrefixs ... ");

        List<Prefix> prefixList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PREFIX;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Prefix prefix = new Prefix();
                prefix.setId(Integer.parseInt(cursor.getString(0)));
                prefix.setOldPRe(cursor.getString(1));
                prefix.setNewPre(cursor.getString(2));

                prefixList.add(prefix);
            } while (cursor.moveToNext());
        }
        return prefixList;
    }

    private int getPrefixsCount() {
        Log.i(TAG, "MyDatabaseHelper.getPrefixsCount ... ");

        String countQuery = "SELECT  * FROM " + TABLE_PREFIX;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void updatePrefix(Prefix prefix) {
        Log.i(TAG, "MyDatabaseHelper.updatePrefix ... " + prefix.getOldPRe());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PREFIX_ID,prefix.getId());
        values.put(COLUMN_PREFIX_OLD, prefix.getOldPRe());
        values.put(COLUMN_PREFIX_NEW, prefix.getNewPre());

        db.update(TABLE_PREFIX, values, COLUMN_PREFIX_ID + " = " + prefix.getId(),null);
        db.close();

    }

    public void deletePrefix(Prefix prefix) {
        Log.i(TAG, "MyDatabaseHelper.delete ... " + prefix.getId());

        SQLiteDatabase db = this.getWritableDatabase();
        int rowDeleted = db.delete(TABLE_PREFIX, COLUMN_PREFIX_ID + " = "+ prefix.getId(),null);
        db.close();
        if(rowDeleted != 0){
            System.out.println("success");
        } else {
            System.out.println("failed");
        }
    }
}

