package com.zlm.run.tool;

import android.content.Context;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   ResouceUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/23 0:08
 * 描述：     TODO 通过资源名获取文件id
 */
public class ResourcesUtils {

    private static final String RES_ID = "id";
    private static final String RES_STRING = "string";
    private static final String RES_DRAWABLE = "drawable";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STYLE = "style";
    private static final String RES_COLOR = "color";
    private static final String RES_DIMEN = "dimen";
    private static final String RES_ANIM = "anim";
    private static final String RES_MENU = "menu";


    /**
     * 获取资源文件的id
     * @param context
     * @param resName
     * @return
     */
    public static int getId(Context context,String resName){
        return getResId(context,resName,RES_ID);
    }

    /**
     * 获取资源文件string的id
     * @param context
     * @param resName
     * @return
     */
    public static int getStringId(Context context,String resName){
        return getResId(context,resName,RES_STRING);
    }

    /**
     * 获取资源文件drawable的id
     * @param context
     * @param resName
     * @return
     */
    public static int getDrawableId(Context context,String resName){
        return getResId(context,resName, RES_DRAWABLE);
    }

    /**
     * 获取资源文件layout的id
     * @param context
     * @param resName
     * @return
     */
    public static int getLayoutId(Context context,String resName){
        return getResId(context,resName,RES_LAYOUT);
    }

    /**
     * 获取资源文件style的id
     * @param context
     * @param resName
     * @return
     */
    public static int getStyleId(Context context,String resName){
        return getResId(context,resName,RES_STYLE);
    }

    /**
     * 获取资源文件color的id
     * @param context
     * @param resName
     * @return
     */
    public static int getColorId(Context context,String resName){
        return getResId(context,resName,RES_COLOR);
    }

    /**
     * 获取资源文件dimen的id
     * @param context
     * @param resName
     * @return
     */
    public static int getDimenId(Context context,String resName){
        return getResId(context,resName,RES_DIMEN);
    }

    /**
     * 获取资源文件ainm的id
     * @param context
     * @param resName
     * @return
     */
    public static int getAnimId(Context context,String resName){
        return getResId(context,resName,RES_ANIM);
    }

    /**
     * 获取资源文件menu的id
     */
    public static int getMenuId(Context context,String resName){
        return getResId(context,resName,RES_MENU);
    }

    /**
     * 获取资源文件ID
     * @param context
     * @param resName
     * @param defType
     * @return
     */
    public static int getResId(Context context, String resName, String defType){
        return context.getResources().getIdentifier(resName, defType, context.getPackageName());
    }

}