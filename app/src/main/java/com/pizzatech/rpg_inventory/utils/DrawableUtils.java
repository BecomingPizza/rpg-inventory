package com.pizzatech.rpg_inventory.utils;

/**
 * Created by Ashley on 29/01/2017.
 */

import android.graphics.drawable.Drawable;

public class DrawableUtils {
    private static final int[] EMPTY_STATE = new int[] {};

    public static void clearState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(EMPTY_STATE);
        }
    }
}