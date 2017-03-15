package com.pizzatech.rpg_inventory.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.util.Log;

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

    public Integer insertCharacter(String name, String campaign, Integer maxCarry) {
        String sql = "INSERT INTO character (NAME, CAMPAIGN, CAPACITY) VALUES ('" + name + "', '" + campaign + "', " + maxCarry + ")";
        database.execSQL(sql);

        // Look up the ID we just added so we can use it
        String query = "SELECT ID FROM character WHERE NAME = '" + name + "' AND CAMPAIGN = '" + campaign + "'";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        Integer id = cursor.getInt(0);
        cursor.close();

        // Add some sample data
        String pocketsSQL = "INSERT INTO container (CHARACTER_ID, NAME, CAPACITY, ON_PERSON, NEXT_CHILD_ID, LIST_ORDER) VALUES (" + id + ", 'Pockets', 2, 1, 1, 0)";
        database.execSQL(pocketsSQL);
        String pocketsIdQuery = "SELECT ID FROM container WHERE CHARACTER_ID = " + id;
        Cursor pocketsCursor = database.rawQuery(pocketsIdQuery, null);
        pocketsCursor.moveToFirst();
        Integer pocketsId = pocketsCursor.getInt(0);
        String lintSQL = "INSERT INTO items (LIST_ORDER, CONTAINER_ID, NAME, DESCRIPTION, WEIGHT, QUANTITY) VALUES (0, " + pocketsId + ", 'Lint', 'Pocket fluff', 0.01, 7)";
        database.execSQL(lintSQL);

        return id;
    }

    public void updateCharacter(Integer id, String name, String campaign, Integer maxCarry) {
        String sql = "UPDATE character SET NAME = '" + name + "', CAMPAIGN = '" + campaign + "', CAPACITY = " + maxCarry + " WHERE ID = " + id;
        database.execSQL(sql);
    }

    public PlayerCharacter getCharacter(Integer id) {
        String sql = "SELECT * FROM character WHERE ID = " + id;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        String name = cursor.getString(1);
        String campaign = cursor.getString(2);
        Integer maxCarry = cursor.getInt(3);
        cursor.close();

        PlayerCharacter pc = new PlayerCharacter(id, name, campaign, maxCarry);
        return pc;
    }

    public void deleteCharacter(Integer id) {
        String sql = "DELETE FROM character WHERE ID = " + id;
        database.execSQL(sql);
    }

    public ArrayList<PlayerCharacter> getAllCharacters() {
        ArrayList<PlayerCharacter> pcs = new ArrayList<>();

        String sql = "SELECT * FROM character";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                PlayerCharacter pc = new PlayerCharacter(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
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

                String query2 = "SELECT * FROM items WHERE CONTAINER_ID = " + grpDbId + " ORDER BY LIST_ORDER ASC";
                Cursor childCursor = database.rawQuery(query2, null);

                List<AbstractInventoryData.ChildData> children = new ArrayList<>();

                if (childCursor.moveToFirst()) {
                    while (!childCursor.isAfterLast()) {

                        Integer childId = childCursor.getInt(1);
                        String childName = childCursor.getString(3);
                        String childDesc = childCursor.getString(4);
                        double childWeight = childCursor.getDouble(5);
                        Integer childQuantity = childCursor.getInt(6);
                        Integer childDbId = childCursor.getInt(0); // Grab  this to make updating easier later

                        children.add(new InventoryData.ConcreteChildData(childId, childName, childDesc, childQuantity, childWeight, childDbId));

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
        database.execSQL(sql);
    }

    public void addContainer(Integer character_id, String name, Integer capacity, Integer onPerson, Integer nextChildId, Integer listOrder) {
        String sql = "INSERT INTO container (CHARACTER_ID, NAME, CAPACITY, ON_PERSON, NEXT_CHILD_ID, LIST_ORDER) VALUES (" + character_id + ", '" + name + "', " + capacity + ", " + onPerson + ", " + nextChildId + ", " + listOrder + ")";
        database.execSQL(sql);
    }

    public void updateContainer(Integer dbId, String name, Integer capacity, Integer onPerson, Integer nextChildId, Integer listOrder) {
        String sql = "UPDATE container SET NAME = '" + name + "', CAPACITY = " + capacity + ", ON_PERSON = " + onPerson + ", NEXT_CHILD_ID = " + nextChildId + ", LIST_ORDER = " + listOrder + " WHERE ID = " + dbId;
        database.execSQL(sql);
    }

    public void deleteContainer(Integer grpDbId) {
        String sql = "DELETE FROM container WHERE ID = " + grpDbId;
        database.execSQL(sql);
        sql = "DELETE FROM items WHERE CONTAINER_ID = " + grpDbId;
        database.execSQL(sql);
    }

    public void addItem(Integer listOrder, Integer grpDbId, String name, String description, Double weight, Integer quantity) {
        String sql = "INSERT INTO items (LIST_ORDER, CONTAINER_ID, NAME, DESCRIPTION, WEIGHT, QUANTITY) VALUES (" + listOrder + ", " + grpDbId + ", '" + name + "', '" + description + "', " + weight + ", " + quantity + ")";
        database.execSQL(sql);
    }

    public void updateItem(Integer childDbId, Integer listOrder, Integer grpDbId, String name, String description, Double weight, Integer quantity) {
        String sql = "UPDATE items SET LIST_ORDER = " + listOrder + ", CONTAINER_ID = " + grpDbId + ", NAME = '" + name + "', DESCRIPTION = '" + description + "', WEIGHT = " + weight + ", QUANTITY = " + quantity + " WHERE ID = " + childDbId;
        database.execSQL(sql);
    }

    public void deleteItem(Integer childDbId) {
        String sql = "DELETE FROM items WHERE ID = " + childDbId;
        database.execSQL(sql);
    }
}
