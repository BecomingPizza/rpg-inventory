package com.pizzatech.rpg_inventory.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.pizzatech.rpg_inventory.MainActivity;
import com.pizzatech.rpg_inventory.R;
import com.pizzatech.rpg_inventory.adapters.InventoryRecyclerAdapter;
import com.pizzatech.rpg_inventory.objects.InventoryData;


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

    private InventoryData invData;

    private RecyclerView invRecyclerView;
    private RecyclerView.LayoutManager invLayoutManager;
    private RecyclerView.Adapter invWrapperAdapter;
    private RecyclerViewExpandableItemManager invRecyclerViewExpandableItemManager;
    private RecyclerViewDragDropManager invRecyclerViewDragDropManager;

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

        // TODO: Data goes in here?
        invData = new InventoryData(PCID);

        // Set up the recycler
        invRecyclerView = (RecyclerView) v.findViewById(R.id.inventory_recycler_list_view);
        invLayoutManager = new LinearLayoutManager(v.getContext());

        invRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(null);
        invRecyclerViewExpandableItemManager.setOnGroupExpandListener(new RecyclerViewExpandableItemManager.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition, boolean fromUser) {
               // Stuff here?
            }
        });
        invRecyclerViewExpandableItemManager.setOnGroupCollapseListener(new RecyclerViewExpandableItemManager.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition, boolean fromUser) {
                // And here?
            }
        });

        // drag & drop manager
        invRecyclerViewDragDropManager = new RecyclerViewDragDropManager();

        final InventoryRecyclerAdapter itemAdapter = new InventoryRecyclerAdapter(invRecyclerViewExpandableItemManager, invData);
        // Wrap ALL the adapters!
        invWrapperAdapter = invRecyclerViewExpandableItemManager.createWrappedAdapter(itemAdapter);
        invWrapperAdapter = invRecyclerViewDragDropManager.createWrappedAdapter(invWrapperAdapter);

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);

        invRecyclerView.setLayoutManager(invLayoutManager);
        invRecyclerView.setAdapter(invWrapperAdapter);
        invRecyclerView.setItemAnimator(animator);
        invRecyclerView.setHasFixedSize(false);

        invRecyclerViewDragDropManager.attachRecyclerView(invRecyclerView);
        invRecyclerViewExpandableItemManager.attachRecyclerView(invRecyclerView);
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
