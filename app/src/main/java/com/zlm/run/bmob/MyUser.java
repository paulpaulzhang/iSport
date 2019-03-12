package com.zlm.run.bmob;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.bmob
 * 文件名：   MyUser
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/3 16:30
 * 描述：     用户表
 */
public class MyUser extends BmobUser {
    private String nickName; //昵称
    private String talking; //说说
    private String birthday; //出生日期
    private String gender; //true为男  false为女
    private BmobFile avatar; //用户头像
    private Integer weight; //体重
    private Integer height; //身高
    private Double running_kilometers; //跑步公里数
    private Integer running_calorie; //跑步卡路里
    private Double riding_kilometers; //骑行公里数
    private Integer riding_calorie; //骑行卡路里
    private Integer walk_number; //步数
    private Integer walk_calorie; //步行卡路里
    private Double walk_kilometers; //步行公里
    private Integer yoga_time; //瑜伽时间
    private Integer sports_energy; //运动能量

    public Integer getSports_energy() {
        return sports_energy;
    }

    public void setSports_energy(Integer sports_energy) {
        this.sports_energy = sports_energy;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTalking() {
        return talking;
    }

    public void setTalking(String talking) {
        this.talking = talking;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getRunning_kilometers() {
        return running_kilometers;
    }

    public void setRunning_kilometers(Double running_kilometers) {
        this.running_kilometers = running_kilometers;
    }

    public Integer getRunning_calorie() {
        return running_calorie;
    }

    public void setRunning_calorie(Integer running_calorie) {
        this.running_calorie = running_calorie;
    }

    public Double getRiding_kilometers() {
        return riding_kilometers;
    }

    public void setRiding_kilometers(Double riding_kilometers) {
        this.riding_kilometers = riding_kilometers;
    }

    public Integer getRiding_calorie() {
        return riding_calorie;
    }

    public void setRiding_calorie(Integer riding_calorie) {
        this.riding_calorie = riding_calorie;
    }

    public Integer getWalk_number() {
        return walk_number;
    }

    public void setWalk_number(Integer walk_number) {
        this.walk_number = walk_number;
    }

    public Integer getWalk_calorie() {
        return walk_calorie;
    }

    public void setWalk_calorie(Integer walk_calorie) {
        this.walk_calorie = walk_calorie;
    }

    public Double getWalk_kilometers() {
        return walk_kilometers;
    }

    public void setWalk_kilometers(Double walk_kilometers) {
        this.walk_kilometers = walk_kilometers;
    }

    public Integer getYoga_time() {
        return yoga_time;
    }

    public void setYoga_time(Integer yoga_time) {
        this.yoga_time = yoga_time;
    }


}
