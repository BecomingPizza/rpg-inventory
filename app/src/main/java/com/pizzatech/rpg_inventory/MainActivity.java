package com.pizzatech.rpg_inventory;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pizzatech.rpg_inventory.adapters.DrawerItemAdapter;
import com.pizzatech.rpg_inventory.fragments.InventoryFragment;
import com.pizzatech.rpg_inventory.objects.DrawerItem;
import com.pizzatech.rpg_inventory.objects.PlayerCharacter;
import com.pizzatech.rpg_inventory.fragments.AboutFragment;
import com.pizzatech.rpg_inventory.fragments.NewPCFragment;
import com.pizzatech.rpg_inventory.database.DBAccess;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Resources res;

    public static DBAccess dbAccess;

    Fragment fragment;

    // Misc drawer shiz
    private ArrayList<DrawerItem> leftDrawerItems = new ArrayList<>();
    private DrawerItemAdapter leftDrawerItemAdapter;
    private ListView leftDrawerList;
    private DrawerLayout leftDrawerLayout;
    private ActionBarDrawerToggle leftDrawerToggle;

    private ArrayList<PlayerCharacter> playerCharactersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._main);

        res = getResources();
        dbAccess = DBAccess.getInstance(this);

        updateDrawer();

        // Initialize drawer
        leftDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Action bar drawer toggle
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leftDrawerToggle = new ActionBarDrawerToggle(this, leftDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        leftDrawerLayout.addDrawerListener(leftDrawerToggle);

        // Default to First Fragment
        selectItem(0);

        doAdStuff();
    }

    // Drawer stuff
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (leftDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        leftDrawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    // Swap fragments.... maybe?
    private void selectItem(int position) {
        // Create a new fragment
        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTran = fragMan.beginTransaction();

        String fragType = leftDrawerItems.get(position).getFragType();
        String actionBarText = "";

        Log.e("FragType:", fragType);

        switch (fragType) {
            case "About":
                fragment = new AboutFragment();
                actionBarText = leftDrawerItems.get(position).getStr();
                break;
            case "PC":
                fragment = new InventoryFragment();
                Bundle bundaro = new Bundle();
                bundaro.putInt("ID", leftDrawerItems.get(position).getPCId());
                fragment.setArguments(bundaro);
                actionBarText = leftDrawerItems.get(position).getActionBarStr();
                break;
            case "New PC":
                fragment = new NewPCFragment();
                Bundle bundaru = new Bundle();
                bundaru.putInt("ID", -1);
                fragment.setArguments(bundaru);
                actionBarText = leftDrawerItems.get(position).getStr();
                break;
        }

        fragTran.replace(R.id.content_frame, fragment);
        fragTran.commit();

        leftDrawerList.setItemChecked(position, true);
        setTitle(actionBarText);
        leftDrawerLayout.closeDrawer(leftDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    public void rateInGP(View v) {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // Need these to get back to the app on a back button push supposedly
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    public void doAdStuff() {
        // Ads
        MobileAds.initialize(this, res.getString(R.string.banner_ad_app_id));
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void updateDrawer() {
        // Clear so we don't just keep adding (yes this was a bug at one point)
        playerCharactersList.clear();
        leftDrawerItems.clear();

        dbAccess.open();
        playerCharactersList = dbAccess.getAllCharacters();
        dbAccess.close();

        // Loop through playerCharactersList and add to the drawer
        for (int i = 0; i < playerCharactersList.size(); i++) {
            // TODO: custom images?
            PlayerCharacter pc = playerCharactersList.get(i);
            leftDrawerItems.add(new DrawerItem(R.drawable.ic_person, pc.getName(), "PC", pc.getId(), pc.getCampaign()));
        }

        // Add New Character DrawerItem
        leftDrawerItems.add(new DrawerItem(R.drawable.ic_add_circle_outline, "New Character", "New PC"));

        // Add About DrawerItem
        leftDrawerItems.add(new DrawerItem(R.drawable.ic_info_outline, "About", "About"));

        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        leftDrawerItemAdapter = new DrawerItemAdapter(this, R.layout.drawer_list_item, leftDrawerItems);
        leftDrawerList.setAdapter(leftDrawerItemAdapter);
    }
}
