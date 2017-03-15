package com.pizzatech.rpg_inventory.objects;

/**
 * Created by Ashley on 15/01/2017.
 *
 */

public class PlayerCharacter {

    private Integer id;
    private String name;
    private String campaign;
    private Integer maxCarry;

    public PlayerCharacter(Integer id, String name, String campaign, Integer maxCarry) {
        super();
        this.id = id;
        this.name = name;
        this.campaign = campaign;
        this.maxCarry = maxCarry;
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

    public Integer getMaxCarry () {
        return maxCarry;
    }
}
