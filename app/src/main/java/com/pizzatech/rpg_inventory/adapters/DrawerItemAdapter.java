package com.pizzatech.rpg_inventory.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pizzatech.rpg_inventory.R;
import com.pizzatech.rpg_inventory.objects.DrawerItem;

import java.util.ArrayList;

/**
 * Created by Ashley on 01/10/2016.
 *
 */

public class DrawerItemAdapter extends ArrayAdapter<DrawerItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<DrawerItem> dil = null;

    public DrawerItemAdapter(Context context, int layoutResourceId, ArrayList<DrawerItem> dil) {
        super(context, layoutResourceId, dil);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.dil = dil;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(layoutResourceId, parent, false);
        }

        DrawerItem di = dil.get(position);

        if (di != null) {
            TextView tv = (TextView) v.findViewById(R.id.drawer_item_text);
            ImageView iv = (ImageView) v.findViewById(R.id.drawer_item_img);

            if (tv != null) {
                tv.setText(di.getStr());
            }

            if (iv != null) {
                iv.setImageResource(di.getImg());
            }
        }

        return v;
    }
}
