package com.example.smocheck;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Camera extends Activity {
    private ImageView cameraPicture;
    public static final int TAKE_PHOTO = 1;
    private Button blackDection=null;//黑度检测
    private Button pictureSave=null;//保存图片
    private TextView tv2=null;//文本显示框
    private Intent intent3;
    private Uri imageUri;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);
        blackDection=super.findViewById(R.id.blackDetection);//关联起来
        pictureSave=super.findViewById(R.id.pictureSave);
        cameraPicture = super.findViewById(R.id.picture);
        tv2=super.findViewById(R.id.tv2);

        // 创建一个File对象，用于保存摄像头拍下的图片，这里把图片命名为output_image.jpg
        // 并将它存放在手机SD卡的应用关联缓存目录下
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        // 对照片的更换设置
        try {
            // 如果上一次的照片存在，就删除
            if (outputImage.exists()) {
                outputImage.delete();
            }
            // 创建一个新的文件
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 如果Android版本大于等于7.0
        if (Build.VERSION.SDK_INT >= 24) {
            // 将File对象转换成一个封装过的Uri对象
            imageUri = FileProvider.getUriForFile(this, "com.example.lenovo.cameraalbumtest.fileprovider", outputImage);
            Log.d("MainActivity", outputImage.toString() + "手机系统版本高于Android7.0");
        } else {
            // 将File对象转换为Uri对象，这个Uri标识着output_image.jpg这张图片的本地真实路径
            Log.d("MainActivity", outputImage.toString() + "手机系统版本低于Android7.0");
            imageUri = Uri.fromFile(outputImage);
        }
        // 动态申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
        } else {
            // 启动相机程序
            startCamera();
        }

        intent3 = new Intent(getApplicationContext(),MainActivity.class);
        blackDection.setOnClickListener(new Camera.blackDectionFuntion());
        pictureSave.setOnClickListener(new Camera.pictureSaveFunction());

    }
    private void startCamera() {
        Intent intent4 = new Intent("android.media.action.IMAGE_CAPTURE");
        // 指定图片的输出地址为imageUri
        intent4.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent4, TAKE_PHOTO);
    }

    private class blackDectionFuntion implements View.OnClickListener {
        public void onClick(View view){
            Toast.makeText(getApplicationContext(),"黑度检测",Toast.LENGTH_SHORT).show();

            //在这里调用黑度检测方法
            BitmapDrawable bmpDrawable = (BitmapDrawable) cameraPicture.getDrawable();
            Bitmap bitmap = bmpDrawable.getBitmap();
            int blackDegree=BlackDegree.calculateImageLingemannBlackness(bitmap);
            String text=String.valueOf(blackDegree);
            tv2.setText("林格曼黑度值为："+text);
        }
    }
    private class pictureSaveFunction implements View.OnClickListener {
        public void onClick(View view){
            BitmapDrawable bmpDrawable = (BitmapDrawable) cameraPicture.getDrawable();
            Bitmap bitmap = bmpDrawable.getBitmap();
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, System.currentTimeMillis()+".jpg", "");
            Toast.makeText(getApplicationContext(),"图片保存成功！",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK ) {
                    try {
                        // 将图片解析成Bitmap对象
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        cameraPicture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    /*
    //保存位图到本地
    public void SavaImage(Bitmap bitmap, String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
        FileOutputStream fileOutputStream = null;
        String filePhth;
        String fileName;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            filePhth=path + "/" + System.currentTimeMillis() + ".png";
            fileName=System.currentTimeMillis()+"";
            File file1=new File(filePhth);
            fileOutputStream = new FileOutputStream(file1.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            //图片路径
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    filePhth,fileName , null);
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file1.getAbsolutePath())));
            Log.d("aaa",file1.getAbsolutePath()+"-----"+path);
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"保存失败",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
     */

}