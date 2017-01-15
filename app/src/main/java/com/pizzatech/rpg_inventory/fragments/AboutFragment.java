package com.pizzatech.rpg_inventory.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pizzatech.rpg_inventory.R;

/**
 * Created by Ashley on 15/01/2017.
 *
 * It's the About Fragment
 * How thrilling
 */

public class AboutFragment  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Do setup stuff
    }

}
