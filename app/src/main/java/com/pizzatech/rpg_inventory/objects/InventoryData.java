package com.pizzatech.rpg_inventory.objects;

import android.support.v4.util.Pair;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.pizzatech.rpg_inventory.fragments.InventoryFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.pizzatech.rpg_inventory.MainActivity.dbAccess;


/**
 * Created by Ashley on 29/01/2017.
 *
 * Hold all the inventory data init u get me bruv
 */

public class InventoryData extends AbstractInventoryData {
    private List<Pair<GroupData, List<ChildData>>> mData;

    // for undo group item
    private Pair<GroupData, List<ChildData>> mLastRemovedGroup;
    private int mLastRemovedGroupPosition = -1;

    // for undo child item
    private ChildData mLastRemovedChild;
    private long mLastRemovedChildParentGroupId = -1;
    private int mLastRemovedChildPosition = -1;



    public InventoryData(Integer PCID) {

        // Load ze inventory!
        dbAccess.open();
        mData = dbAccess.getInventory(PCID);
        dbAccess.close();

    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mData.get(groupPosition).second.size();
    }

    @Override
    public GroupData getGroupItem(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        return mData.get(groupPosition).first;
    }

    @Override
    public ChildData getChildItem(int groupPosition, int childPosition) {
        if (groupPosition < 0 || groupPosition >= getGroupCount()) {
            throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
        }

        final List<ChildData> children = mData.get(groupPosition).second;

        if (childPosition < 0 || childPosition >= children.size()) {
            throw new IndexOutOfBoundsException("childPosition = " + childPosition);
        }

        return children.get(childPosition);
    }

    @Override
    public void moveGroupItem(int fromGroupPosition, int toGroupPosition) {
        if (fromGroupPosition == toGroupPosition) {
            return;
        }

        final Pair<GroupData, List<ChildData>> item = mData.remove(fromGroupPosition);
        mData.add(toGroupPosition, item);

        dbAccess.open();

        // EVERYTHING NEEDS TO UPDATE TO MAINTAIN ORDER
        for (int i = 0; i < mData.size(); i++ ) {
            dbAccess.updateContainerPosition(((ConcreteGroupData) mData.get(i).first).getDbId(), i);
        }

        dbAccess.close();

    }

    @Override
    public void moveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        if ((fromGroupPosition == toGroupPosition) && (fromChildPosition == toChildPosition)) {
            return;
        }

        final Pair<GroupData, List<ChildData>> fromGroup = mData.get(fromGroupPosition);
        final Pair<GroupData, List<ChildData>> toGroup = mData.get(toGroupPosition);

        final ConcreteChildData item = (ConcreteChildData) fromGroup.second.remove(fromChildPosition);

        if (toGroupPosition != fromGroupPosition) {
            // assign a new ID
            final Integer newId = ((ConcreteGroupData) toGroup.first).generateNewChildId();
            item.setChildId(newId);
        }

        toGroup.second.add(toChildPosition, item);

        // Databasey shiz
        Integer grpDbId = ((ConcreteGroupData)toGroup.first).getDbId();
        dbAccess.open();

        for (int i = 0; i < toGroup.second.size(); i++) {
            dbAccess.updateItemPoaition(grpDbId, ((ConcreteChildData)toGroup.second.get(i)).getDbId(), i);
        }

        dbAccess.close();
    }

    @Override
    public void removeGroupItem(int groupPosition) {
        mLastRemovedGroup = mData.remove(groupPosition);
        mLastRemovedGroupPosition = groupPosition;

        mLastRemovedChild = null;
        mLastRemovedChildParentGroupId = -1;
        mLastRemovedChildPosition = -1;
    }

    @Override
    public void removeChildItem(int groupPosition, int childPosition) {
        mLastRemovedChild = mData.get(groupPosition).second.remove(childPosition);
        mLastRemovedChildParentGroupId = mData.get(groupPosition).first.getGroupId();
        mLastRemovedChildPosition = childPosition;

        mLastRemovedGroup = null;
        mLastRemovedGroupPosition = -1;
    }


    @Override
    public long undoLastRemoval() {
        if (mLastRemovedGroup != null) {
            return undoGroupRemoval();
        } else if (mLastRemovedChild != null) {
            return undoChildRemoval();
        } else {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }
    }

    private long undoGroupRemoval() {
        int insertedPosition;
        if (mLastRemovedGroupPosition >= 0 && mLastRemovedGroupPosition < mData.size()) {
            insertedPosition = mLastRemovedGroupPosition;
        } else {
            insertedPosition = mData.size();
        }

        mData.add(insertedPosition, mLastRemovedGroup);

        mLastRemovedGroup = null;
        mLastRemovedGroupPosition = -1;

        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(insertedPosition);
    }

    private long undoChildRemoval() {
        Pair<GroupData, List<ChildData>> group = null;
        int groupPosition = -1;

        // find the group
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).first.getGroupId() == mLastRemovedChildParentGroupId) {
                group = mData.get(i);
                groupPosition = i;
                break;
            }
        }

        if (group == null) {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
        }

        int insertedPosition;
        if (mLastRemovedChildPosition >= 0 && mLastRemovedChildPosition < group.second.size()) {
            insertedPosition = mLastRemovedChildPosition;
        } else {
            insertedPosition = group.second.size();
        }

        group.second.add(insertedPosition, mLastRemovedChild);

        mLastRemovedChildParentGroupId = -1;
        mLastRemovedChildPosition = -1;
        mLastRemovedChild = null;

        return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, insertedPosition);
    }

    public static final class ConcreteGroupData extends GroupData {

        private final Integer mId;
        private final String mText;
        private final Integer mCapacity;
        private final boolean mOnPerson; // Is the character carrying this, or is it on a horse/wagon/octopus?
        private boolean mPinned;
        private Integer mNextChildId;
        private Integer mDbId;

        // id, text, weight capacity

        public ConcreteGroupData(Integer id, String text, Integer capacity, boolean onPerson, Integer nextChildId, Integer dbId) {
            mId = id;
            mText = text;
            mCapacity = capacity;
            mOnPerson = onPerson;
            mNextChildId = nextChildId;
            mDbId = dbId;
        }

        @Override
        public Integer getGroupId() {
            return mId;
        }

        @Override
        public boolean isSectionHeader() {
            return false;
        }

        @Override
        public String getText() {
            return mText;
        }

        public Integer getCapacity() {
            return mCapacity;
        }

        public String getCapacityText() {
            String s = mCapacity.toString();
            return s;
        }

        public Integer getNextChildId() {
            return mNextChildId;
        }

        public boolean isOnPerson() {
            return mOnPerson;
        }

        public Integer getDbId() {
            return mDbId;
        }

        @Override
        public void setPinned(boolean pinnedToSwipeLeft) {
            mPinned = pinnedToSwipeLeft;
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        public Integer generateNewChildId() {
            final Integer id = mNextChildId;
            mNextChildId += 1;
            return id;
        }
    }

    public static final class ConcreteChildData extends ChildData {

        private Integer mId;
        private final String mText;
        private final String mSubText;
        private final Integer mQuantity;
        private final double mWeightPerUnit;
        private boolean mPinned;
        private Integer mDbId;

        // id, main text, sub text, quantity, weight per unit

        public ConcreteChildData(Integer id, String text, String subText, Integer quantity, double weightPerUnit, Integer dbId) {
            mId = id;
            mText = text;
            mSubText = subText;
            mQuantity = quantity;
            mWeightPerUnit = weightPerUnit;
            mDbId = dbId;
        }

        @Override
        public Integer getChildId() {
            return mId;
        }

        @Override
        public String getText() {
            return mText;
        }

        public String getSubText() {
            return mSubText;
        }

        public Integer getQuantity() {
            return mQuantity;
        }

        public double getWeightPerUnit() {
            return mWeightPerUnit;
        }

        public double getTotalWeight() {
            if (mQuantity != 0 && mWeightPerUnit != 0) {
                return mQuantity * mWeightPerUnit;
            } else {
                return 0;
            }
        }

        public Integer getDbId() {
            return mDbId;
        }

        @Override
        public void setPinned(boolean pinned) {
            mPinned = pinned;
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        public void setChildId(Integer id) {
            this.mId = id;
        }
    }
}
