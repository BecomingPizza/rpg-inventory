package com.pizzatech.rpg_inventory.objects;

/**
 * Created by Ashley on 15/01/2017.
 *
 */

public class PlayerCharacter {

    private Integer id;
    private String name;
    private String campaign;

    // TODO: Add player total capacity

    public PlayerCharacter(Integer id, String name, String campaign) {
        super();
        this.id = id;
        this.name = name;
        this.campaign = campaign;
    }

    public Integer getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public String getCampaign () {
        return campaign;
    }
}
