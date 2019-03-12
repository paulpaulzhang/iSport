package com.zlm.run.tool;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   NumberPickerUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/2 9:35
 * 描述：     TODO
 */
public class NumberPickerUtil {

    /**
     * 设置NumberPicker分割线颜色
     *
     * @param numberPicker：NumberPicker
     * @param color：int
     */
    public static void setNumberPickerDividerColor(NumberPicker numberPicker, int color, Context mContext) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field SelectionDividerField : pickerFields) {
            if (SelectionDividerField.getName().equals("mSelectionDivider")) {
                SelectionDividerField.setAccessible(true);
                try {
                    SelectionDividerField.set(numberPicker, new ColorDrawable(ContextCompat.getColor(mContext, color)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
