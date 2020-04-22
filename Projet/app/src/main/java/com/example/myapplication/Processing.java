package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import com.android.rssample.ScriptC_gris;

/**
 *
 */
public class Processing {


    /**
     * Invert the colors of the bitmap
     *
     * @param bmp
     *          Bitmap representing the image
     *
     */
    static public void inverserCouleur(Bitmap bmp) {

        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        int blue;
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int green;
        int red;
        int pixel;
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixel = pixels[i + (j * width)];
                green = 255 - Color.green(pixel);
                red = 255 - Color.red(pixel);
                blue = 255 - Color.blue(pixel);
                pixels[i + (j * bmp.getWidth())] = Color.rgb( red,  green,blue);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

    }


    /**
     * Cancel all the modifications on the image
     * @param bmp
     *          Bitmap representing the image
     * @param pixels
     *          table representing the image in its initial state
     */
    static public void original(Bitmap bmp,int[] pixels){
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }


    /**
     *
     * Equalizes the histogram of the image
     * @param bmp
     *          Bitmap representing the image
     */
    static public void equalize(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int pixel;
        int gray;
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int histoV[]= new int[256];
        int histoR[]= new int[256];
        int histoB[]= new int[256];
        int cumuleV[]=new int[256];
        int cumuleR[]=new int[256];
        int cumuleB[]=new int[256];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * width)];
                histoV[Color.green(pixel)]++;
                histoR[Color.red(pixel)]++;
                histoB[Color.blue(pixel)]++;
            }
        }
        cumuleR[0]=0;
        cumuleB[0]=0;
        cumuleV[0]=0;
        for(int i=1; i<255;i++) {
            cumuleV[i]=cumuleV[i-1]+histoV[i];
            cumuleR[i]=cumuleR[i-1]+histoR[i];
            cumuleB[i]=cumuleB[i-1]+histoB[i];
        }
        int red=0;
        int green=0;
        int blue=0;
        for(int i=0; i<width;i++) {
            for (int j = 0; j < height; j++) {
                pixel = pixels[i + (j * width)];
                red= cumuleR[Color.red(pixel)]*255/(height*width);
                green = cumuleV[Color.green(pixel)]*255/(height*width);
                blue = cumuleB[Color.blue(pixel)]*255/(height*width);
                pixels[i + (j * width)]=Color.rgb(red,green,blue);

            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    /**
     * Reduces contrast of the image
     *
     * @param bmp
     *          Bitmap representing the image
     * @see #toContrastedyn(Bitmap, Context)
     *
     */
    static public void lessContraste(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int blue;
        int green;
        int red;
        int medium=0;
        int mediumV=0;
        int mediumB=0;
        int mediumR=0;
        int spectre=0;
        int pixel;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                blue = Color.blue(pixel);
                red=Color.red(pixel);
                green=Color.green(pixel);
                mediumB=mediumB+blue;
                mediumR=mediumR+red;
                mediumV=mediumV+green;
            }
        }
        mediumB=mediumB/(bmp.getWidth()*bmp.getHeight());
        mediumR=mediumR/(bmp.getWidth()*bmp.getHeight());
        mediumV=mediumV/(bmp.getWidth()*bmp.getHeight());
        medium=mediumB+mediumR+mediumV;
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                blue = Color.blue(pixel);
                red=Color.red(pixel);
                green=Color.green(pixel);
                if ((red>= blue+20)&& (red>=green+20)){
                    red=red- (red-mediumR)/8;
                }
                else if ((green>= blue+20)&& (green>=red+20)){
                    green=green+(mediumV -green)/8;
                }
                else if ((blue>= red+20)&& (blue>=green+20)){
                    blue=blue+(mediumB -blue)/8;
                }
                else if( (Math.abs(red-green)<= 1 && Math.abs(blue-green)<= 1 )){
                    red=red- (red-medium/3)/8;
                    green=green+(medium/3 -green)/8;
                    blue=blue+(medium/3 -blue)/8;
                }

                pixels[i + (j * bmp.getWidth())] = Color.rgb(red, green, blue);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    /**
     * improves contrast of the image
     *
     * @param bmp
     *          Bitmap representing the image
     * @see #lessContraste(Bitmap)
     */
     static public void toContrastedyn(Bitmap bmp, android.content.Context context){


             int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
             int min=255;
             int max=0;
             int pixel;
             int gris;
             bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
             for(int i=1; i<bmp.getWidth();i++) {
                 for (int j = 1; j < bmp.getHeight(); j++) {
                     pixel = pixels[i + (j * bmp.getWidth())];
                     gris = Color.blue(pixel);
                     if (gris > max) {
                         max = gris;
                     }
                     if (gris < min) {
                         min = gris;
                     }
                 }
             }

             for(int i=1; i<bmp.getWidth();i++) {
                 for (int j = 1; j < bmp.getHeight(); j++) {
                     pixel = pixels[i + (j * bmp.getWidth())];
                     gris = Color.blue(pixel);
                     gris=255*(gris-min)/(max-min);
                     pixels[i + (j * bmp.getWidth())] = Color.rgb(gris, gris, gris);
                 }
             }
             bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
         }
    /**
     * gray the whole image except the green parts
     *
     * @param bmp
     *          Bitmap representing the image
     * @see #colorize(Bitmap, int)
     */
    static public void green(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int red;
        int blue;
        int green;
        int medium;
        int pixel;
        float[] HSV = new float[3];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=0; i<bmp.getWidth();i++){
            for(int j=0; j<bmp.getHeight() ; j++){
                pixel=pixels[i+(j*bmp.getWidth())];
                blue=Color.blue(pixel);
                red=Color.red(pixel);
                green= Color.green(pixel);
                Color.RGBToHSV(red,green,blue,HSV);
                if (!((green>50) && (HSV[1]>0.3) && (blue+red-28<green))){
                    medium= (int) ((float) ((float) red*0.33) + (float) (0.59*green) + (float) blue*0.11);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        pixels[i+(j*bmp.getWidth())]= Color.rgb(medium,medium,medium);
                    }
                }

            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    /**
     *Colorize the image with the same tint
     *
     * @param bmp
     *          Bitmap representing the image
     *
     * @param color
     *          Integer that define the tint
     *
     * @see #green(Bitmap) {@link #toGrayOp(Bitmap, Context)}
     */
    static public void colorize(Bitmap bmp, int color){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int red;
        int blue;
        int green;
        float HSV[]=new float[3];
        int pixel;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=0; i<bmp.getWidth();i++){
            for(int j=0; j<bmp.getHeight() ; j++){
                pixel=pixels[i+(j*bmp.getWidth())];
                blue=Color.blue(pixel);
                red=Color.red(pixel);
                green= Color.green(pixel);
                Color.RGBToHSV(red,green,blue,HSV);
                HSV[0]=color;
                pixels[i+(j*bmp.getWidth())]=Color.HSVToColor(HSV);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

    }

    /**
     * Grays the image
     *
     * @param bmp
     *          Bitmap representing the image
     * @param context
     *          Context
     */

    static public void toGrayOp(Bitmap bmp, android.content.Context context){
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap (rs , bmp ) ;
        Allocation output = Allocation.createTyped (rs , input.getType()) ;
        ScriptC_gris grayScript = new ScriptC_gris(rs);
        grayScript.forEach_toGray(input,output);
        output.copyTo(bmp);
        input.destroy();
        output.destroy();
        grayScript.destroy();
        rs.destroy();
    }

}
