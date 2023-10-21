package com.example.smocheck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Color;


public class BlackDegree extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // 计算图片的林格曼黑度值
    public static int calculateImageLingemannBlackness(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;
        double totalBlackness = 0.0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelColor = image.getPixel(x, y);

                // 获取像素的RGB分量
                int red = Color.red(pixelColor);
                int green = Color.green(pixelColor);
                int blue = Color.blue(pixelColor);

                // 计算林格曼黑度值的近似，这只是一个示例，实际的计算可能需要更复杂的算法
                double blackness = (red + green + blue) / 3.0;

                totalBlackness += blackness;
            }
        }

        // 计算平均林格曼黑度值
        double averageLingemannBlackness = totalBlackness / (totalPixels*256*3);
        if(averageLingemannBlackness<0.01)return 5;
        else if (averageLingemannBlackness<0.2) return 4;
        else if (averageLingemannBlackness<0.4) return 3;
        else if(averageLingemannBlackness<0.6)return 2;
        else if(averageLingemannBlackness<0.8)return 1;
        else return 0;
    }



}

