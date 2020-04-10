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


public class Traitement {

    static public void InverserCouleur(Bitmap bmp) {

        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        int blue;
        int green;
        int red;
        int pixel;
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < bmp.getWidth(); i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                green = 255 - Color.green(pixel);
                red = 255 - Color.red(pixel);
                blue = 255 - Color.blue(pixel);
                pixels[i + (j * bmp.getWidth())] = Color.rgb( red,  green,blue);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    static public void convoSobel(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int pixel;
        int gray;
        int[][] gx={{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] gy={{-1,-2,-1},{0,0,0},{1,2,1}};
        double red;
        double green;
        double blue;
        double bluex=0;
        double greenx=0;
        double redx=0;
        double bluey=0;
        double greeny=0;
        double redy=0;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=1; i<bmp.getWidth()-1;i=i+3) {
            for (int j = 1; j < bmp.getHeight()-1; j=j+3) {
                for(int a=i-1;a<=i+1;a++) {
                    for(int b=j-1;b<=j+1;b++) {
                        pixel = pixels[a + (b * bmp.getWidth())];

                        redx = redx + Color.red(pixel)*gx[a-i+1][b-j+1] ;
                        bluex = bluex + Color.blue(pixel)*gx[a-i+1][b-j+1] ;
                        greenx = greenx + Color.green(pixel)*gx[a-i+1][b-j+1];

                        redy = redy + Color.red(pixel)*gy[a-i+1][b-j+1] ;
                        bluey = bluey + Color.blue(pixel)*gy[a-i+1][b-j+1] ;
                        greeny = greeny + Color.green(pixel)*gy[a-i+1][b-j+1];

                    }
                }
                red=Math.sqrt(redx*redx + redy*redy);
                green=Math.sqrt((greenx*greenx+greeny*greeny));
                blue=Math.sqrt(bluex*bluex+bluey*bluey);
                for(int a=i-1;a<=i+1;a++) {
                    for(int b=j-1;b<=j+1;b++) {

                        pixels[a + (b * bmp.getWidth())]=Color.rgb((int)red,(int)green,(int)blue);
                    }
                }

                greenx=0;
                redx=0;
                bluex=0;
                greeny=0;
                redy=0;
                bluey=0;


            }
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());


    }


    static public void convoLaplac(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int pixel;
        int gray;
        int[][] filtre={{0,1,0},{1,-4,1},{0,1,0}};
        double blue=0;
        double green=0;
        double red=0;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=1; i<bmp.getWidth()-1;i=i+3) {
            for (int j = 1; j < bmp.getHeight()-1; j=j+3) {
                for(int a=i-1;a<=i+1;a++) {
                    for(int b=j-1;b<=j+1;b++) {
                        pixel = pixels[a + (b * bmp.getWidth())];

                            red = red + Color.red(pixel)*filtre[a-i+1][b-j+1]/3 ;
                            blue = blue + Color.blue(pixel)*filtre[a-i+1][b-j+1]/3 ;
                            green = green + Color.green(pixel)*filtre[a-i+1][b-j+1]/3;

                    }
                }
                for(int a=i-1;a<=i+1;a++) {
                    for(int b=j-1;b<=j+1;b++) {

                        pixels[a + (b * bmp.getWidth())]=Color.rgb((int)red,(int)green,(int)blue);
                    }
                }

                green=0;
                red=0;
                blue=0;

            }
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

    }



    static public void convoGauss(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int pixel;
        int gray;
        int[][] filtre={{2,4,5,4,2},{4,9,12,4,9},{5,12,15,12,5},{4,9,12,4,9},{2,4,5,4,2}};
        double blue=0;
        double green=0;
        int medium=0;
        double red=0;
        double epsilon=  0.008;
        double psi=1;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=2; i<bmp.getWidth()-2;i=i+5) {
            for (int j = 2; j < bmp.getHeight()-2; j=j+5) {
                for(int a=i-2;a<=i+2;a++) {
                    for(int b=j-2;b<=j+2;b++) {
                        pixel = pixels[a + (b * bmp.getWidth())];
                        red=red+ Color.red(pixel)* filtre[a-i+2][b-j+2]/159;
                        blue=blue+ Color.blue(pixel)* filtre[a-i+2][b-j+2]/159;
                        green=green+Color.green(pixel)* filtre[a-i+2][b-j+2]/159;
                    }
                }
                for(int a=i-2;a<=i+2;a++) {
                    for(int b=j-2;b<=j+2;b++) {
                        pixels[a + (b * bmp.getWidth())]=Color.rgb( (int) red, (int) green,(int) blue);
                    }
                }

                green=0;
                red=0;
                blue=0;

            }
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

    }
    static public void convomoy(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int pixel;
        int gray;
        int blue=0;
        int green=0;
        int medium=0;
        int red=0;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=2; i<bmp.getWidth()-2;i=i+5) {
            for (int j = 2; j < bmp.getHeight()-2; j=j+5) {
                for(int a=i-2;a<=i+2;a++) {
                    for(int b=j-2;b<=j+2;b++) {
                        pixel = pixels[a + (b * bmp.getWidth())];
                        red=red+Color.red(pixel)/25;
                        blue=blue+Color.blue(pixel)/25;
                        green=green+Color.green(pixel)/25;
                    }
                }
                for(int a=i-2;a<=i+2;a++) {
                    for(int b=j-2;b<=j+2;b++) {
                        pixels[a + (b * bmp.getWidth())]=Color.rgb(red,green,blue);
                    }
                }

                green=0;
                red=0;
                blue=0;

            }
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

    }

    static public void original(Bitmap bmp,int[] pixels){
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    static public void equalize(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int pixel;
        int gray;
        int histoV[]= new int[256];
        int histoR[]= new int[256];
        int histoB[]= new int[256];
        int cumuleV[]=new int[256];
        int cumuleR[]=new int[256];
        int cumuleB[]=new int[256];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
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
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                red= cumuleR[Color.red(pixel)]*255/(bmp.getHeight()*bmp.getWidth());
                green = cumuleV[Color.green(pixel)]*255/(bmp.getHeight()*bmp.getWidth());
                blue = cumuleB[Color.blue(pixel)]*255/(bmp.getHeight()*bmp.getWidth());
                pixels[i + (j * bmp.getWidth())]=Color.rgb(red,green,blue);

            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

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
     static public void ToContrastedyn(Bitmap bmp, android.content.Context context){


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
    static public void ToGrayOp(Bitmap bmp, android.content.Context context){
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
    static public void pencil(Bitmap bmp,Context context){
        convoSobel(bmp);
        InverserCouleur(bmp);
        ToGrayOp(bmp,context);
        convoGauss(bmp);
    }
}
