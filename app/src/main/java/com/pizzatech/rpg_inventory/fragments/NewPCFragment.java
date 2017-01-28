package com.pizzatech.rpg_inventory.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pizzatech.rpg_inventory.MainActivity;
import com.pizzatech.rpg_inventory.R;
import com.pizzatech.rpg_inventory.objects.PlayerCharacter;

import static com.pizzatech.rpg_inventory.MainActivity.dbAccess;

/**
 * Created by Ashley on 15/01/2017.
 *
 * Add a new PC & then shove the user straight into an InventoryFragment for that PC
 *
 * Also handle edits?
 */

public class NewPCFragment extends Fragment {

    View v;

    Integer PCID;

    EditText nameEditText;
    EditText campaignEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_pc, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Do setup stuff

        v = view;

        nameEditText = (EditText) v.findViewById(R.id.new_pc_name_edit_text);
        campaignEditText = (EditText) v.findViewById(R.id.new_pc_campaign_edit_text);

        // Get ID to work on
        Bundle bundaru = getArguments();
        PCID = bundaru.getInt("ID");

        // Anything other than -1 indicates we need to load the character info
        if (!PCID.equals(-1)) {
            dbAccess.open();
            PlayerCharacter currentPC = dbAccess.getCharacter(PCID);
            dbAccess.close();
            nameEditText.setText(currentPC.getName(), TextView.BufferType.EDITABLE);
            campaignEditText.setText(currentPC.getCampaign(), TextView.BufferType.EDITABLE);
        }

        // Set listener on confirmBtn
        Button confirmBtn = (Button) v.findViewById(R.id.new_pc_confirm_button);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upsertPC();
            }
        });
    }

    // Insert or update PC
    void upsertPC() {

        String name = nameEditText.getText().toString();
        String campaign = campaignEditText.getText().toString();

        if (!name.isEmpty()) {

            dbAccess.open();

            if (!PCID.equals(-1)) {
                dbAccess.updateCharacter(PCID, name, campaign);
                Toast.makeText(v.getContext(), "Character successfully edited", Toast.LENGTH_SHORT).show();
            } else {
                PCID = dbAccess.insertCharacter(name, campaign);
                Toast.makeText(v.getContext(), "Character successfully added", Toast.LENGTH_SHORT).show();
            }

            dbAccess.close();

            // Refresh Drawer
            ((MainActivity)getActivity()).updateDrawer();

            // Move to inventory fragment
            String actionBarText = name;
            if (!campaign.equals("")) {
                actionBarText += " (" + campaign + ")";
            }
            swapToInventoryFragment(actionBarText, PCID);

            // Close teh keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


        } else {
            Toast.makeText(v.getContext(), "Name is required", Toast.LENGTH_SHORT).show();
        }
    }

    // Open Inventory Fragment for new/updated character
    private void swapToInventoryFragment(String actionBarText, Integer id) {
        // Create a new fragment
        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTran = fragMan.beginTransaction();

        Fragment fragment = new InventoryFragment();
        Bundle bundaru = new Bundle();
        bundaru.putInt("ID", id);
        fragment.setArguments(bundaru);

        fragTran.replace(R.id.content_frame, fragment);
        fragTran.commit();

        getActivity().setTitle(actionBarText);

    }

}
