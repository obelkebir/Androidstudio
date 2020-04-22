package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

public class Convolution extends Processing {
    static public int[]applyConvo(Bitmap bmp , int[][] mask, int taille, int cof, boolean option){
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] pixels2 = new int[width * height];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, width, height);
        bmp.getPixels(pixels2, 0, bmp.getWidth(), 0, 0, width, height);
        int blue=0;
        int green=0;
        int red=0;
        int pixel;
        int pixel2;
        for(int i=taille/2; i<width- taille/2 ;i++) {
            for (int j = taille/2; j < height-taille/2 ; j++) {
                pixel = pixels[i + (j * width)];
                for(int a=i-taille/2;a<=i+taille/2;a++) {
                    for (int b = j - taille / 2; b <= j + taille / 2; b++) {
                        pixel2 = pixels2[a + (b * width)];

                        red = red + Color.red(pixel2) * mask[a - i + taille / 2][b - j + taille / 2];
                        blue = blue + Color.blue(pixel2) * mask[a - i + taille / 2][b - j + taille / 2];
                        green = green + Color.green(pixel2) * mask[a - i + taille / 2][b - j + taille / 2];
                    }
                }
                if (option){
                    if (red/cof>255){
                        red=255*cof;
                    }
                    if (red<0){
                        red=0;
                    }
                    if (blue/cof>255){
                        blue=255*cof;
                    }
                    if (blue<0){
                        blue=0;
                    }
                    if (green/cof>255){
                        green=255*cof;
                    }
                    if(green<0){
                        green=0;
                    }
                }
                pixels[i + (j * bmp.getWidth())]=Color.rgb(red/cof,green/cof,blue/cof);
                red=0;
                green=0;
                blue=0;
            }
        }
        return pixels;
    }


    /**
     *Apply Laplace's convolution on the image
     *
     * @param bmp
     *           Bitmap representing the image
     *
     * @see #applyConvo(Bitmap, int[][], int, int, boolean)
     */
    static public void convoLaplac(Bitmap bmp){
        int[][] filtre={{1,1,1},{1,-8,1},{1,1,1}};
        int[] pixels=applyConvo(bmp,filtre,3,1,false);
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    /**
     *Apply Gauss's convolution on the image
     *
     * @param bmp
     *           Bitmap representing the image
     *
     * @see #applyConvo(Bitmap, int[][], int, int, boolean)
     */
    static public void convoGauss(Bitmap bmp){

        int[][] filtre={{2,4,5,4,2},{4,9,12,4,9},{5,12,15,12,5},{4,9,12,4,9},{2,4,5,4,2}};
        int[]pixels=applyConvo(bmp,filtre,5,159,true);
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }
    /**
     *Apply moyenner's convolution on the image
     *
     * @param bmp
     *           Bitmap representing the image
     *
     * @see #applyConvo(Bitmap, int[][], int, int, boolean)
     */
    static public void convomoy(Bitmap bmp){
        int[][] filtre={{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1}};
        int[]pixels=applyConvo(bmp,filtre,5,25,true);
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    /**
     *Apply sobel's convolution on the image
     *
     * @param bmp
     *           Bitmap representing the image
     *
     * @see #applyConvo(Bitmap, int[][], int, int, boolean)
     */
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
    /**
     *Apply Pencil effect on the image
     *
     * @param bmp
     *           Bitmap representing the image
     * @param context
     *           Context
     * @see #applyConvo(Bitmap, int[][], int, int, boolean) {@link #convoSobel(Bitmap)}{@link #toGrayOp(Bitmap, Context)}
     * {@link #inverserCouleur(Bitmap)}{@link #convoGauss(Bitmap)}
     */

    static public void pencil(Bitmap bmp, Context context){
        convoGauss(bmp);
        convoSobel(bmp);
        inverserCouleur(bmp);
        toGrayOp(bmp,context);

    }

    static public void cartoon(Bitmap bmp, Context context){
        int blue;
        int green;
        int red;
        int pixel;
        int pixel2;
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int[] pixels2= new int[bmp.getWidth()*bmp.getHeight()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        pencil(bmp,context);
        bmp.getPixels(pixels2,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                pixel2 = pixels2[i + (j * bmp.getWidth())];
                if (Color.red(pixel2)<220){
                    pixels[i + (j * bmp.getWidth())]=Color.BLACK;
                }


            }
            bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        }
    }
}
