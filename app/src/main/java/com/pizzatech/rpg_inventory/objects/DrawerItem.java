package com.pizzatech.rpg_inventory.objects;

import android.app.Fragment;

/**
 * Created by Ashley on 01/10/2016.
 *
 */

public class DrawerItem {
    private int img;
    private String str;
    private String fragType;
    private Integer PCId;

    public DrawerItem(int img, String str, String fragType) {
        super();
        this.img = img;
        this.str = str;
        this.fragType = fragType;
    }

    public DrawerItem(int img, String str, String fragType, Integer pc) {
        super();
        this.img = img;
        this.str = str;
        this.fragType = fragType;
        this.PCId = pc;
    }
    public int getImg () {
        return img;
    }

    public String getStr () {
        return str;
    }

    public String getFragType () {
        return fragType;
    }

    public Integer getPCId () {
        return PCId;
    }
}
