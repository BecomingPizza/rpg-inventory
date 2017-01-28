package com.pizzatech.rpg_inventory.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pizzatech.rpg_inventory.objects.PlayerCharacter;

import java.util.ArrayList;

/**
 * Created by Ashley on 26/09/2016.
 * <p>
 * YEAH DATABASE
 */

public class DBAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DBAccess instance;

    private DBAccess(Context context) {
        this.openHelper = new DBHelper(context);
    }

    public static DBAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DBAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Integer insertCharacter (String name, String campaign) {
        String sql = "INSERT INTO character (NAME, CAMPAIGN) VALUES ('" + name + "', '" + campaign + "')" ;
        database.execSQL(sql);

        // Look up the ID we just added so we can use it
        String query = "SELECT ID FROM character WHERE NAME = '" + name + "' AND CAMPAIGN = '" + campaign + "'";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        Integer id = cursor.getInt(0);
        cursor.close();

        return id;
    }

    public void updateCharacter (Integer id, String name, String campaign) {
        String sql = "UPDATE character SET NAME = '" + name + "', CAMPAIGN = '" + campaign + "' WHERE ID = " + id;
        database.execSQL(sql);
    }

    public PlayerCharacter getCharacter (Integer id) {
        String sql = "SELECT * FROM character WHERE ID = " + id;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        String name = cursor.getString(1);
        String campaign = cursor.getString(2);
        cursor.close();

        PlayerCharacter pc = new PlayerCharacter(id, name, campaign);
        return pc;
    }

    public void deleteCharacter (Integer id) {
        String sql = "DELETE FROM character WHERE ID = " + id;
        database.execSQL(sql);
    }

    public ArrayList<PlayerCharacter> getAllCharacters () {
        ArrayList<PlayerCharacter> pcs = new ArrayList<>();

        String sql = "SELECT * FROM character";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                PlayerCharacter pc = new PlayerCharacter(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                pcs.add(pc);
                cursor.moveToNext();
            }
        }

        cursor.close();

        return pcs;

    }
}
