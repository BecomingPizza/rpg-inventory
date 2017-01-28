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
    private String campaign;

    public DrawerItem(int img, String str, String fragType) {
        super();
        this.img = img;
        this.str = str;
        this.fragType = fragType;
        this.PCId = null;
        this.campaign = null;
    }

    // Special thingamy for PCs as they have some extra functionality
    public DrawerItem(int img, String str, String fragType, Integer pc, String campaign) {
        super();
        this.img = img;
        this.str = str;
        this.fragType = fragType;
        this.PCId = pc;
        this.campaign = campaign;
    }
    public int getImg () {
        return img;
    }

    public String getActionBarStr () {
        if (!campaign.equals("")) {
            return str + " (" + campaign + ")";
        } else {
            return str;
        }
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

    public String getCampaign () {
        return campaign;
    }
}
