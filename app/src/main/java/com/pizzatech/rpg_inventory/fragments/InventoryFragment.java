package com.pizzatech.rpg_inventory.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;
import android.widget.ViewAnimator;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.pizzatech.rpg_inventory.MainActivity;
import com.pizzatech.rpg_inventory.R;
import com.pizzatech.rpg_inventory.adapters.InventoryRecyclerAdapter;
import com.pizzatech.rpg_inventory.objects.InventoryData;


import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.pizzatech.rpg_inventory.MainActivity.dbAccess;

/**
 * Created by Ashley on 28/01/2017.
 * <p>
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

    private ViewAnimator animator;

    private Menu iMenu;

    Spinner spinner;

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
        iMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Override the item selection on the menu to make it do things
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_pc:
                swapToNewPCFrag("Edit Character", PCID);
                return true;
            case R.id.action_delete_pc:
                deletePC();
                return true;
            case R.id.action_save_container:
                saveContainer();
                return true;
            case R.id.action_delete_container:
                deleteContainer();
                return true;
            case R.id.action_save_item:
                saveItem();
                return true;
            case R.id.action_delete_item:
                deleteItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Do setup stuff
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        v = view;

        // Instantiate (?) animator
        animator = (ViewAnimator) v.findViewById(R.id.inventory_animator);
        animator.setDisplayedChild(0); // Main inventory screen

        // Listeners on + buttons
        FloatingActionButton addItemBtn = (FloatingActionButton) v.findViewById(R.id.action_add_item);
        FloatingActionButton addContainerBtn = (FloatingActionButton) v.findViewById(R.id.action_add_container);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Check we have containers!
                clearItemView();
                swapToItemView();
            }
        });
        addContainerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearContainerView();
                swapToContainerView();
            }
        });

        // Get the ID for PC
        Bundle bundaru = getArguments();
        PCID = bundaru.getInt("ID");

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

        final InventoryRecyclerAdapter itemAdapter = new InventoryRecyclerAdapter(invRecyclerViewExpandableItemManager, invData, InventoryFragment.this);
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
                ((MainActivity) getActivity()).updateDrawer();
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

    private void swapToItemView() {
        //TODO: Animate?
        animator.setDisplayedChild(1);

        // Menu stuff
        iMenu.findItem(R.id.action_edit_pc).setVisible(false);
        iMenu.findItem(R.id.action_delete_pc).setVisible(false);
        iMenu.findItem(R.id.action_save_item).setVisible(true);
        iMenu.findItem(R.id.action_delete_item).setVisible(true);

    }

    private void swapToContainerView() {
        //TODO: Animate?
        animator.setDisplayedChild(2);

        // Menu stuff
        iMenu.findItem(R.id.action_edit_pc).setVisible(false);
        iMenu.findItem(R.id.action_delete_pc).setVisible(false);
        iMenu.findItem(R.id.action_save_container).setVisible(true);
        iMenu.findItem(R.id.action_delete_container).setVisible(true);
    }

    private void clearItemView() {

        buildSpinner();
        EditText itemNameEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_name);
        EditText itemDescEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_desc);
        EditText itemWeightEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_weight);
        EditText itemQuantityEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_quantity);

        spinner.setSelection(0);
        itemNameEditText.setText(null);
        itemDescEditText.setText(null);
        itemWeightEditText.setText(null);
        itemQuantityEditText.setText(null);
    }

    private void clearContainerView() {

        EditText containerNameEditText = (EditText) v.findViewById(R.id.inventory_add_edit_container_name);
        EditText containerCapacityEditText = (EditText) v.findViewById(R.id.inventory_add_edit_container_capacity);
        Switch containerOnPersonSwitch = (Switch) v.findViewById(R.id.inventory_add_edit_container_on_person_switch);

        containerNameEditText.setText(null);
        containerCapacityEditText.setText(null);
        containerOnPersonSwitch.setChecked(true);
    }

    public void editContainer(Integer pos) {

        InventoryData.ConcreteGroupData group = (InventoryData.ConcreteGroupData) invData.getGroupItem(pos);

        // Populate fields
        EditText containerNameEditText = (EditText) v.findViewById(R.id.inventory_add_edit_container_name);
        EditText containerCapacityEditText = (EditText) v.findViewById(R.id.inventory_add_edit_container_capacity);
        Switch containerOnPersonSwitch = (Switch) v.findViewById(R.id.inventory_add_edit_container_on_person_switch);
        containerNameEditText.setText(group.getText());
        containerCapacityEditText.setText(group.getCapacity().toString());
        containerOnPersonSwitch.setChecked(group.isOnPerson());

        swapToContainerView();
    }

    public void editItem(Integer grpPos, Integer childPos) {

        InventoryData.ConcreteChildData child = (InventoryData.ConcreteChildData) invData.getChildItem(grpPos, childPos);

        // Build the stupid spinner
        buildSpinner();

        // Populate fields
        EditText itemNameEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_name);
        EditText itemDescEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_desc);
        EditText itemWeightEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_weight);
        EditText itemQuantityEditText = (EditText) v.findViewById(R.id.inventory_add_edit_item_quantity);
        itemNameEditText.setText(child.getText());
        itemDescEditText.setText(child.getSubText());
        itemWeightEditText.setText(String.valueOf(child.getWeightPerUnit()));
        itemQuantityEditText.setText(child.getQuantity().toString());


        swapToItemView();
    }

    private void buildSpinner() {
        spinner = (Spinner) v.findViewById(R.id.inventory_add_edit_item_container);
        ArrayList<CharSequence> justSpinnerThings = new ArrayList<>();
        for (int i = 0; i < invData.getGroupCount(); i++) {
            justSpinnerThings.add(invData.getGroupItem(i).getText());
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_dropdown_item, justSpinnerThings);
        spinner.setAdapter(adapter);

    }

    private void saveContainer() {
        // New or editing?
        // Add to list
        // Save to db
        // Cheat and restart the fragment
    }

    private void deleteContainer() {
        // If not editing skip to end
        // Warn that items will be lost
        // Delete container & items from db
        // Cheat and restart the fragment
    }

    private void saveItem() {
        // New or editing?
        // Save to db
        // Cheat and restart the fragment
    }

    private void deleteItem() {
        // If not editing skip to end
        // Delete item from db
        // Delete from list
        // Cheat and restart the fragment
    }
}
