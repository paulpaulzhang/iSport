package com.zlm.run.tool;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * 项目名：   Run
 * 包名：     com.zlm.run.tool
 * 文件名：   CameraUtil
 * 创建者：   PaulZhang
 * 创建时间： 2018/9/11 21:04
 * 描述：     TODO
 */
public class CameraUtil {
//    private void openCamera() {
//        //先验证手机是否有sdcard
//        String status = Environment.getExternalStorageState();
//        if (status.equals(Environment.MEDIA_MOUNTED)) {
//            //创建File对象，用于存储拍照后的照片
//            File img = new File(getExternalCacheDir(), "create_img");//SD卡的应用关联缓存目录
//            try {
//                if (img.exists()) {
//                    img.delete();
//                }
//                img.createNewFile();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    imgUri = FileProvider.getUriForFile
//                            (this, "com.zlm.run.fileprovider", img);
//                } else {
//                    imgUri = Uri.fromFile(img);
//                }
//
//                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
//                startActivityForResult(intent, Constant.RESULT_CAMERA);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "没有找到储存目录", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "没有储存卡", Toast.LENGTH_SHORT).show();
//        }
//    }
}
