package com.zlm.run.tool;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   SportsUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/3 11:47
 * 描述：     TODO
 */
public class SportsUtil {
    // 公里计算公式
    public static double getDistanceByStep(long steps) {
        return steps * 0.6f / 1000;
    }

    // 千卡路里计算公式(步数)
    public static int getCalorieByStep(long steps, int weight) {
        return (int) (steps * 0.6f * weight * 1.036f / 1000);
    }

    // 千卡路里计算公式(距离)
    public static int getCalorieByDistance(double distance, int weight) {
        if (distance == 0) {
            return 0;
        }
        return (int) (distance * weight * 1.036f);
    }

    /**
     * @param step 步数
     * @param time 秒
     * @return
     */
    //配速计算公式
    public static double getSpeedByStep(long step, long time) {
        return step * 0.6f / time * 3.6;
    }

    //配速计算公式
    public static double getSpeedByDistance(double distance, long time) {
        return distance / time * 3.6;
    }


}
