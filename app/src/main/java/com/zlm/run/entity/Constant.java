package com.zlm.run.entity;

import com.zlm.run.R;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   Constant
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/21 15:15
 * 描述：     常量
 */
public class Constant {
    //bmob APP ID
    public static final String APP_ID = "c9d05da10a4e410f68c3d4dbcafd028f";

    //SharePreference key
    public static final String PREF_IS_FIRST = "IS_FIRST";

    public static final String PREF_LOGIN_DAY = "login_day";

    public static final String PREF_LOGIN_TIME = "login_time";

    public static final String USERNAME_KEY = "HAS_USERNAME";
    public static final String PASSWORD_KEY = "HAS_PASSWORD";

    //闪屏页
    public static final int HANDLER_SPLASH = 100;

    public static final int HANDLER_SPLASH_TV = 101;

    //用户名正则
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9_-]{6,16}$";

    //密码正则
    public static final String REGEX_PASSWORD = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

    //邮箱正则
    public static final String REGEX_EMAIL = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$";

    // 下拉刷新
    public static final int STATE_REFRESH = 0;

    // 加载更多
    public static final int STATE_MORE = 1;

    //返回码，相机
    public static final int RESULT_CAMERA = 200;

    //返回码，本地图库
    public static final int RESULT_IMAGE = 201;

    //返回码，Matisse
    public static final int REQUEST_CODE_CHOOSE = 202;

    //默认头像
    public static final String AVATAR_URL = "http://bmob-cdn-20382.b0.upaiyun.com/2018/07/16/03c6ec374096da6a803d54598712bd03.jpg";

    //轮播视频url
    public static final String RUN_URL = "android.resource://com.zlm.run/raw/" + R.raw.run;

    //轮播视频url
    public static final String RIDE_URL = "android.resource://com.zlm.run/raw/" + R.raw.ride;

    //轮播视频url
    public static final String WALK_URL = "android.resource://com.zlm.run/raw/" + R.raw.walk;

    //轮播视频url
    public static final String YOGA_URL = "android.resource://com.zlm.run/raw/" + R.raw.yoga;

    //步数刷新
    public static final int REFRESH_STEP_WHAT = 301;

    //最后一次定位信息
    public static final String LAST_LOCATION = "last_location";

    //实时定位间隔(单位:秒)
    public static final int LOC_INTERVAL = 2;

    //位置采集周期 (s)
    public static final int GATHER_INTERVAL = 2;

    //打包周期 (s)
    public static final int PACK_INTERVAL = 4;

    //鹰眼服务id
    public static final long SERVICE_ID = 203523;

    public static final int DISTANCE_MSG = 501;
    public static final int TIME_MSG = 502;
    public static final int SPEED_MSG = 503;
    public static final int TIME_INDOOR_MSG = 504;
    public static final int YOGA_TIME = 505;

    public static final int RUNNING_TIME = 60 * 1000;

    public static final int OUT_READY_ONE = 10000;
    public static final int OUT_READY_TWO = 10001;
    public static final int OUT_READY_THREE = 10002;
    public static final int OUT_READY_GO = 10003;

    public static final int IN_READY_ONE = 10004;
    public static final int IN_READY_TWO = 10005;
    public static final int IN_READY_THREE = 10006;
    public static final int IN_READY_GO = 10007;

    public static final String INTENT_POST_URL = "post_img_url";

    public static final String INTENT_ARTICLE_URL = "article_img_url";

    public static final String INTENT_ARTICLE_NAME = "article_name";

    public static final String INTENT_ARTICLE_CONTENT = "article_content";

    public static final String INTENT_WEB_URL = "web_url";


}
