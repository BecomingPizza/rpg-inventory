package com.pizzatech.rpg_inventory.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;

import com.pizzatech.rpg_inventory.objects.AbstractInventoryData;
import com.pizzatech.rpg_inventory.objects.InventoryData;
import com.pizzatech.rpg_inventory.objects.PlayerCharacter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    public List<Pair<AbstractInventoryData.GroupData, List<AbstractInventoryData.ChildData>>> getInventory(Integer PCID) {

        List<Pair<AbstractInventoryData.GroupData, List<AbstractInventoryData.ChildData>>> data = new LinkedList<>();

        String query1 = "SELECT * FROM container WHERE CHARACTER_ID = " + PCID + " ORDER BY LIST_ORDER ASC";
        Cursor grpCursor = database.rawQuery(query1, null);
        if (grpCursor.moveToFirst()) {

            while (!grpCursor.isAfterLast()) {

                Integer grpId = grpCursor.getInt(6);
                String grpName = grpCursor.getString(2);
                Integer grpCapacity = grpCursor.getInt(3);
                Boolean grpOnPerson;
                if (grpCursor.getInt(4) == 1) {
                    grpOnPerson = true;
                } else {
                    grpOnPerson = false;
                }
                Integer grpNextChildId = grpCursor.getInt(5);
                Integer grpDbId = grpCursor.getInt(0); // Grab this to make updating easier later

                InventoryData.ConcreteGroupData group = new InventoryData.ConcreteGroupData(grpId, grpName, grpCapacity, grpOnPerson, grpNextChildId, grpDbId);

                String query2 = "SELECT * FROM items WHERE CONTAINER_ID = " + grpId + " ORDER BY LIST_ORDER ASC";
                Cursor childCursor = database.rawQuery(query2, null);

                List<AbstractInventoryData.ChildData> children = new ArrayList<>();

                if (childCursor.moveToFirst()) {
                    while (!childCursor.isAfterLast()) {

                        Integer childId = childCursor.getInt(1);
                        String childName = childCursor.getString(3);
                        String childDesc = childCursor.getString(4);
                        Integer childWeight = childCursor.getInt(5);
                        Integer childQuantity = childCursor.getInt(6);
                        Integer childDbId = childCursor.getInt(0); // Grab  this to make updating easier later

                        children.add(new InventoryData.ConcreteChildData(childId, childName, childDesc, childWeight, childQuantity, childDbId));

                        childCursor.moveToNext();
                    }
                }

                childCursor.close();


                data.add(new Pair<AbstractInventoryData.GroupData, List<AbstractInventoryData.ChildData>>(group, children));

                grpCursor.moveToNext();
            }

        }

        grpCursor.close();

        return data;
    }

    public void updateItemPoaition(Integer containerDbId, Integer itemDbId, Integer itemOrder) {
        String sql = "UPDATE items SET CONTAINER_ID = " + containerDbId + ", LIST_ORDER = " + itemOrder + " WHERE ID = " + itemDbId;
        database.execSQL(sql);
    }

    public void updateContainerPosition(Integer containerDbId, Integer containerOrder) {
        String sql = "UPDATE container SET LIST_ORDER = " + containerOrder + " WHERE ID = " + containerDbId;
    }
}
