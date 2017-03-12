package com.pizzatech.rpg_inventory.objects;

/**
 * Created by Ashley on 29/01/2017.
 */

public abstract class AbstractInventoryData {
    public static abstract class BaseData {

        public abstract String getText();

        public abstract void setPinned(boolean pinned);

        public abstract boolean isPinned();
    }

    public static abstract class GroupData extends BaseData {
        public abstract boolean isSectionHeader();
        public abstract Integer getGroupId();
    }

    public static abstract class ChildData extends BaseData {
        public abstract Integer getChildId();
    }

    public abstract int getGroupCount();
    public abstract int getChildCount(int groupPosition);

    public abstract GroupData getGroupItem(int groupPosition);
    public abstract ChildData getChildItem(int groupPosition, int childPosition);

    public abstract void moveGroupItem(int fromGroupPosition, int toGroupPosition);
    public abstract void moveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition);

    public abstract void removeGroupItem(int groupPosition);
    public abstract void removeChildItem(int groupPosition, int childPosition);

    public abstract long undoLastRemoval();
}
