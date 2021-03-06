package com.myshopping.utils.keyboardutils;

import android.app.Activity;

import android.support.constraint.ConstraintLayout;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.myshopping.utils.keyboardutils.layouts.KeyboardDismissingConstraintLayout;
import com.myshopping.utils.keyboardutils.layouts.KeyboardDismissingCoordinatorLayout;
import com.myshopping.utils.keyboardutils.layouts.KeyboardDismissingLinearLayout;
import com.myshopping.utils.keyboardutils.layouts.KeyboardDismissingRelativeLayout;


public class KeyboardDismisser {

    private static String[] sSupportedClasses = new String[] {
            "LinearLayout",
            "RelativeLayout",
            "CoordinatorLayout",
            "ConstraintLayout"
    };

    public static void useWith(Fragment fragment) {
        ViewGroup viewGroup = (ViewGroup) fragment.getView();

        swapMainLayoutWithDismissingLayout(viewGroup, fragment.getActivity());
    }

    public static void useWith(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        ViewGroup viewGroup = (ViewGroup) content.getChildAt(0);

        swapMainLayoutWithDismissingLayout(viewGroup, activity);
    }

    public static void useWith(ViewGroup viewGroup, Activity activity) {
        swapMainLayoutWithDismissingLayout(viewGroup, activity);
    }

    private static void swapMainLayoutWithDismissingLayout(ViewGroup viewGroup, Activity activity) {
        if (viewGroup == null) {
            return;
        }

        String className = "";

        String viewGroupClassName = viewGroup.getClass().getSimpleName();
        for (String name : sSupportedClasses) {
            if (viewGroupClassName.equals(name)) {
                className = name;
            }
        }

        ViewGroup generatedLayout = viewGroup;

        switch (className) {
            case "LinearLayout":
                generatedLayout = new KeyboardDismissingLinearLayout(activity);
                ((KeyboardDismissingLinearLayout) generatedLayout).setActivity(activity);
                break;
            case "RelativeLayout":
                generatedLayout = new KeyboardDismissingRelativeLayout(activity);
                ((KeyboardDismissingRelativeLayout) generatedLayout).setActivity(activity);
                break;
            case "CoordinatorLayout":
                generatedLayout = new KeyboardDismissingCoordinatorLayout(activity);
                ((KeyboardDismissingCoordinatorLayout) generatedLayout).setActivity(activity);
                break;
            case "ConstraintLayout":
                generatedLayout = new KeyboardDismissingConstraintLayout(activity);
                ((KeyboardDismissingConstraintLayout) generatedLayout).setActivity(activity);
        }

        if (className.isEmpty()) {
            return;
        }

        if (viewGroup.getLayoutParams() != null) {
            generatedLayout.setLayoutParams(viewGroup.getLayoutParams());
        } else {
            generatedLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (generatedLayout instanceof KeyboardDismissingConstraintLayout) {
            int widthOfOriginalLayout = ConstraintLayout.LayoutParams.MATCH_PARENT;
            int heightOfOriginalLayout = ConstraintLayout.LayoutParams.MATCH_PARENT;

            if (viewGroup.getLayoutParams() != null) {
                widthOfOriginalLayout = viewGroup.getLayoutParams().width;
                heightOfOriginalLayout = viewGroup.getLayoutParams().height;
            }

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(widthOfOriginalLayout, heightOfOriginalLayout);
            layoutParams.validate();

            generatedLayout.setLayoutParams(layoutParams);
        }

        while (viewGroup.getChildCount() != 0) {
            View child = viewGroup.getChildAt(0);

            viewGroup.removeViewAt(0);
            generatedLayout.addView(child);
        }

        viewGroup.removeAllViews();
        viewGroup.addView(generatedLayout, 0);
    }
}