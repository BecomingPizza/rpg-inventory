package com.pizzatech.rpg_inventory.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.pizzatech.rpg_inventory.R;

/**
 * Created by Ashley on 29/01/2017.
 */


    // NOTE: AnimatedVectorDrawableCompat works on API level 11+
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class ExpandableItemIndicatorImplAnim extends ExpandableItemIndicator.Impl {
        private AppCompatImageView mImageView;

        @Override
        public void onInit(Context context, AttributeSet attrs, int defStyleAttr, ExpandableItemIndicator thiz) {
            View v = LayoutInflater.from(context).inflate(R.layout.widget_expandable_item_indicator, thiz, true);
            mImageView = (AppCompatImageView) v.findViewById(R.id.image_view);
        }

        @Override
        public void setExpandedState(boolean isExpanded, boolean animate) {
            // No idea why but animation doesn't work so.... TODO: Fix this i spose?
//            if (animate) {
//                @DrawableRes int resId = isExpanded ? R.drawable.ic_expand_more_to_expand_less : R.drawable.ic_expand_less_to_expand_more;
//                mImageView.setImageResource(resId);
//                ((Animatable) mImageView.getDrawable()).start();
//            } else {
                @DrawableRes int resId = isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more;
                mImageView.setImageResource(resId);
//            }
        }
    }
