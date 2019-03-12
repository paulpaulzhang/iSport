package com.zlm.run.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 项目名：   YourFate
 * 包名：     com.zlm.yourfate.bmob
 * 文件名：   Post
 * 创建者：   PaulZhang
 * 创建时间： 2018/7/3 16:43
 * 描述：     用户动态表
 */
public class Post extends BmobObject {
    private String content; //内容
    private MyUser author; //发布者
    private BmobFile image; //图片


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

}
