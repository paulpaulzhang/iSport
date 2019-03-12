package com.zlm.run.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.bmob
 * 文件名：   Article
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/15 22:06
 * 描述：     TODO
 */
public class Article extends BmobObject {
    private String name;
    private String content;
    private BmobFile image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }
}
