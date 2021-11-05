package com.example.listening_eye;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String database_name = "Login.db";

    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase my_database) {
        my_database.execSQL("create Table users (username TEXT primary key, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase my_database, int i, int i1) {
        my_database.execSQL("drop Table if exists users");
    }

    public Boolean insertNew(String username, String password) {
        SQLiteDatabase my_database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = my_database.insert("users", null, contentValues);
        return result != -1;
    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase my_database = this.getWritableDatabase();
        Cursor cursor = my_database.rawQuery("select * from users where username = ?", new String[] {username});
        return cursor.getCount() > 0;
    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase my_database = this.getWritableDatabase();
        Cursor cursor = my_database.rawQuery("select * from users where username = ? and password = ?", new String[] {username, password});
        return cursor.getCount() > 0;
    }
}
