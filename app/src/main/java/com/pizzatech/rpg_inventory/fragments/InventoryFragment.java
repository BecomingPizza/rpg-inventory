package com.pizzatech.rpg_inventory.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pizzatech.rpg_inventory.MainActivity;
import com.pizzatech.rpg_inventory.R;
import com.pizzatech.rpg_inventory.objects.PlayerCharacter;

import static com.pizzatech.rpg_inventory.MainActivity.dbAccess;

/**
 * Created by Ashley on 28/01/2017.
 *
 * Manage Inventory
 * Buttons to Delete PC & Edit
 */

public class InventoryFragment extends Fragment {

    View v;

    Integer PCID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.inventory, container, false);
    }

    // Add the menu buttons to edit or delete the PC
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inventory_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Override the item selection on the menu to make it do things
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                swapToNewPCFrag("Edit Character", PCID);
                return true;
            case R.id.action_delete:
                deletePC();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Do setup stuff
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        v = view;

        // Get the ID for PC
        Bundle bundaru = getArguments();
        PCID = bundaru.getInt("ID");

    }

    // Move to new PC fragment, either for editing or as a result of deleting
    private void swapToNewPCFrag(String actionBarText, Integer id) {
        // Create a new fragment
        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTran = fragMan.beginTransaction();

        Fragment fragment = new NewPCFragment();
        Bundle bundaru = new Bundle();
        bundaru.putInt("ID", id);
        fragment.setArguments(bundaru);

        fragTran.replace(R.id.content_frame, fragment);
        fragTran.commit();

        getActivity().setTitle(actionBarText);

    }

    // Delete Current PC and go back to New PC page
    private void deletePC() {
        // Make an alert dialog to check in case they thought a trash can meant something else
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure you want to delete this character?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbAccess.open();
                dbAccess.deleteCharacter(PCID);
                dbAccess.close();
                swapToNewPCFrag("New Character", -1);
                Toast.makeText(v.getContext(), "Successfully deleted character", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).updateDrawer();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
