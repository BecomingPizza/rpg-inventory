package com.pizzatech.rpg_inventory.database;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Ashley on 26/09/2016.
 *
 * I LIKE BEANS
 */

class DBHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "RPGInv.db";
    private static final int DATABASE_VERSION = 1;

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
