package com.wscubetech.seovideotutorials.utils;

import android.support.v7.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by wscubetech on 22/4/17.
 */

public class ForceShowPopUpIcon {
    public static void forceShowNow(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
