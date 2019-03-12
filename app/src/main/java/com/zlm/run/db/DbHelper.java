package com.zlm.run.db;

import com.zlm.run.entity.LaunchTimeData;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.DbHelper
 * 文件名：   DbHelper
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/21 22:11
 * 描述：     TODO
 */
public class DbHelper {
    private static LaunchTimeData launchTimeData = new LaunchTimeData();

    public static LaunchTimeData getInstance() {
        return launchTimeData;
    }
}
