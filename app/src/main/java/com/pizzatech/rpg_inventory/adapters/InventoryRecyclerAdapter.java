package com.pizzatech.rpg_inventory.adapters;

import android.support.annotation.IntRange;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.pizzatech.rpg_inventory.R;
import com.pizzatech.rpg_inventory.fragments.InventoryFragment;
import com.pizzatech.rpg_inventory.objects.InventoryData;
import com.pizzatech.rpg_inventory.utils.DrawableUtils;
import com.pizzatech.rpg_inventory.utils.ViewUtils;
import com.pizzatech.rpg_inventory.widgets.ExpandableItemIndicator;

import org.w3c.dom.Text;


/**
 * Created by Ashley on 29/01/2017.
 * <p>
 * Welcome to the most complicated adapter I have ever copied most of from somewhere else
 */

public class InventoryRecyclerAdapter
        extends AbstractExpandableItemAdapter<InventoryRecyclerAdapter.MyGroupViewHolder, InventoryRecyclerAdapter.MyChildViewHolder>
        implements ExpandableDraggableItemAdapter<InventoryRecyclerAdapter.MyGroupViewHolder, InventoryRecyclerAdapter.MyChildViewHolder> {

    private interface Expandable extends ExpandableItemConstants {
    }

    private interface Draggable extends DraggableItemConstants {
    }

    private InventoryData invData;
    private RecyclerViewExpandableItemManager invExpandableItemManager;
    private View.OnClickListener invItemViewOnClickListener;
    private EventListener eventListener;

    private InventoryFragment invFragment;


    public interface EventListener {
        void onItemViewClicked(View v, boolean pinned);
    }

    public static abstract class MyBaseViewHolder extends AbstractDraggableItemViewHolder implements ExpandableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;
        public LinearLayout mLongClickDetector;
        private int mExpandStateFlags;

        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = (TextView) v.findViewById(R.id.text1);
            mLongClickDetector = (LinearLayout) v.findViewById(R.id.long_click_detector);
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }

        @Override
        public void setExpandStateFlags(int flag) {
            mExpandStateFlags = flag;
        }
    }

    public class MyGroupViewHolder extends MyBaseViewHolder {
        public ExpandableItemIndicator indicator;
        public TextView mGrpCapacityTextView;

        public MyGroupViewHolder(View itemView) {
            super(itemView);
            indicator = (ExpandableItemIndicator) itemView.findViewById(R.id.indicator);
            mGrpCapacityTextView = (TextView) itemView.findViewById(R.id.capacity_text_view);

        }
    }

    public class MyChildViewHolder extends MyBaseViewHolder {

        public TextView mDescTextView;
        public TextView mQuantityTextView;
        public TextView mWeightTextView;

        public MyChildViewHolder(View itemView) {
            super(itemView);
            mDescTextView = (TextView) itemView.findViewById(R.id.desc_text_view);
            mQuantityTextView = (TextView) itemView.findViewById(R.id.quantity_text_view);
            mWeightTextView = (TextView) itemView.findViewById(R.id.weight_text_view);
        }
    }

    public InventoryRecyclerAdapter(RecyclerViewExpandableItemManager expandableItemManager, InventoryData data, InventoryFragment fragment) {
        invExpandableItemManager = expandableItemManager;
        invData = data;
        invFragment = fragment;

        invItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        setHasStableIds(true);
    }

    private void onItemViewClick(View v) {
        if (eventListener != null) {
            eventListener.onItemViewClicked(v, true);
        }
    }

    @Override
    public boolean onCheckGroupCanStartDrag(MyGroupViewHolder holder, int groupPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckChildCanStartDrag(MyChildViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetGroupItemDraggableRange(MyGroupViewHolder holder, int groupPosition) {
        return null;
    }

    @Override
    public ItemDraggableRange onGetChildItemDraggableRange(MyChildViewHolder holder, int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public void onMoveGroupItem(int fromGroupPosition, int toGroupPosition) {
        invData.moveGroupItem(fromGroupPosition, toGroupPosition);
    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        invData.moveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
    }

    @Override
    public boolean onCheckGroupCanDrop(int draggingGroupPosition, int dropGroupPosition) {
        return true;
    }

    @Override
    public boolean onCheckChildCanDrop(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return invData.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return invData.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return invData.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return invData.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.inv_list_group, parent, false);
        return new MyGroupViewHolder(v);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.inv_list_item, parent, false);
        return new MyChildViewHolder(v);
    }


    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, final int groupPosition, int viewType) {
        // group item
        final InventoryData.ConcreteGroupData item = (InventoryData.ConcreteGroupData) invData.getGroupItem(groupPosition);

        // set listeners
        holder.itemView.setOnClickListener(invItemViewOnClickListener);

        holder.mLongClickDetector.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                invFragment.editContainer(groupPosition);
                return false;
            }
        });

        // set text
        holder.mTextView.setText(item.getText());


        double t = 0;
        for (int i = 0; i < invData.getChildCount(groupPosition); i++) {
            InventoryData.ConcreteChildData c = (InventoryData.ConcreteChildData) invData.getChildItem(groupPosition, i);
            t += c.getTotalWeight();
        }

        String s = Double.toString(t) + " / " + item.getCapacityText() + " lbs";
        holder.mGrpCapacityTextView.setText(s);

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
        final int expandState = holder.getExpandStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) ||
                ((expandState & Expandable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_group_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_group_item_dragging_state;
            } else if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            holder.mContainer.setBackgroundResource(bgResId);
            holder.indicator.setExpandedState(isExpanded, animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, final int groupPosition, final int childPosition, int viewType) {
        // child item
        final InventoryData.ConcreteChildData item = (InventoryData.ConcreteChildData) invData.getChildItem(groupPosition, childPosition);

        // set listeners
        // (if the item is *pinned*, click event comes to the itemView)
        holder.itemView.setOnClickListener(invItemViewOnClickListener);
        holder.mLongClickDetector.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                invFragment.editItem(groupPosition, childPosition);
                return false;
            }
        });

        // set text
        holder.mTextView.setText(item.getText());
        holder.mDescTextView.setText(item.getSubText());
        holder.mQuantityTextView.setText("Quantity: " + item.getQuantity());
        holder.mWeightTextView.setText("Weight: " + item.getTotalWeight() + " lbs");

        final int dragState = holder.getDragStateFlags();

        if ((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
        if (invData.getGroupItem(groupPosition).isPinned()) {
            // return false to raise View.OnClickListener#onClick() event
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return !ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(EventListener e) {
        eventListener = e;
    }

    private static class GroupUnpinResultAction extends SwipeResultActionDefault {
        private InventoryRecyclerAdapter mAdapter;
        private final int mGroupPosition;

        GroupUnpinResultAction(InventoryRecyclerAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            InventoryData.GroupData item = mAdapter.invData.getGroupItem(mGroupPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.invExpandableItemManager.notifyGroupItemChanged(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class ChildUnpinResultAction extends SwipeResultActionDefault {
        private InventoryRecyclerAdapter mAdapter;
        private final int mGroupPosition;
        private final int mChildPosition;

        ChildUnpinResultAction(InventoryRecyclerAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            InventoryData.ChildData item = mAdapter.invData.getChildItem(mGroupPosition, mChildPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.invExpandableItemManager.notifyChildItemChanged(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

}
