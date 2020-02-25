package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageView image ;
    private TextView txt ;
    private Button button;
    private void original(Bitmap bmp,int[] pixels){
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    private void equalize(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int pixel;
        int gris;
        int histoV[]= new int[256];
        int histoR[]= new int[256];
        int histoB[]= new int[256];
        int cumuleV[]=new int[256];
        int cumuleR[]=new int[256];
        int cumuleB[]=new int[256];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=1; i<bmp.getWidth();i++) {
            for (int j = 1; j < bmp.getHeight(); j++) {
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
        int rouge=0;
        int vert=0;
        int bleu=0;
        for(int i=1; i<bmp.getWidth();i++) {
            for (int j = 1; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                rouge= cumuleR[Color.red(pixel)]*255/(bmp.getHeight()*bmp.getWidth());
                vert = cumuleV[Color.green(pixel)]*255/(bmp.getHeight()*bmp.getWidth());
                bleu = cumuleB[Color.blue(pixel)]*255/(bmp.getHeight()*bmp.getWidth());
                pixels[i + (j * bmp.getWidth())]=Color.rgb(rouge,vert,bleu);

            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    private void lessContraste(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int bleu;
        int vert;
        int rouge;
        int medium=0;
        int mediumV=0;
        int mediumB=0;
        int mediumR=0;
        int spectre=0;
        int pixel;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=1; i<bmp.getWidth();i++) {
            for (int j = 1; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                bleu = Color.blue(pixel);
                rouge=Color.red(pixel);
                vert=Color.green(pixel);
                mediumB=mediumB+bleu;
                mediumR=mediumR+rouge;
                mediumV=mediumV+vert;
            }
        }
        mediumB=mediumB/(bmp.getWidth()*bmp.getHeight());
        mediumR=mediumR/(bmp.getWidth()*bmp.getHeight());
        mediumV=mediumV/(bmp.getWidth()*bmp.getHeight());
        medium=mediumB+mediumR+mediumV;
        for(int i=1; i<bmp.getWidth();i++) {
            for (int j = 1; j < bmp.getHeight(); j++) {
                pixel = pixels[i + (j * bmp.getWidth())];
                bleu = Color.blue(pixel);
                rouge=Color.red(pixel);
                vert=Color.green(pixel);
                if ((rouge>= bleu+20)&& (rouge>=vert+20)){
                        rouge=rouge- (rouge-mediumR)/8;
                }
                 else if ((vert>= bleu+20)&& (vert>=rouge+20)){
                        vert=vert+(mediumV -vert)/8;
                }
                 else if ((bleu>= rouge+20)&& (bleu>=vert+20)){
                        bleu=bleu+(mediumB -bleu)/8;
                }
                 else if( (Math.abs(rouge-vert)<= 1 && Math.abs(bleu-vert)<= 1 )){
                    rouge=rouge- (rouge-medium/3)/8;
                    vert=vert+(medium/3 -vert)/8;
                    bleu=bleu+(medium/3 -bleu)/8;
                }

                pixels[i + (j * bmp.getWidth())] = Color.rgb(rouge, vert, bleu);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }
    private void ToContrastedyn(Bitmap bmp){
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
    private void green(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int rouge;
        int bleu;
        int vert;
        int moyenne;
        int pixel;
        float[] HSV = new float[3];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=1; i<bmp.getWidth();i++){
            for(int j=1; j<bmp.getHeight() ; j++){
                pixel=pixels[i+(j*bmp.getWidth())];
                bleu=Color.blue(pixel);
                rouge=Color.red(pixel);
                vert= Color.green(pixel);
                Color.RGBToHSV(rouge,vert,bleu,HSV);
                if (!((vert>50) && (HSV[1]>0.3) && (bleu+rouge-28<vert))){
                    moyenne=rouge*3/10+59/100*vert+bleu*11/100;
                    pixels[i+(j*bmp.getWidth())]= Color.rgb(moyenne,moyenne,moyenne);
                }

            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    private void colorize(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int rouge;
        int bleu;
        int vert;
        float HSV[]=new float[3];
        int pixel;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=1; i<bmp.getWidth();i++){
            for(int j=1; j<bmp.getHeight() ; j++){
                pixel=pixels[i+(j*bmp.getWidth())];
                bleu=Color.blue(pixel);
                rouge=Color.red(pixel);
                vert= Color.green(pixel);
                Color.RGBToHSV(rouge,vert,bleu,HSV);
                HSV[0]=HSV[0]+ 30;
                pixels[i+(j*bmp.getWidth())]=Color.HSVToColor(HSV);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

    }
    private void ToGray(Bitmap bmp){
        int pixel;
        int rouge;
        int bleu;
        int vert;
        int moyenne;
        for(int i=1; i<bmp.getWidth();i++){
            for(int j=1; j<bmp.getHeight() ; j++){
                pixel=bmp.getPixel(i,j);
                bleu=Color.blue(pixel);
                rouge=Color.red(pixel);
                vert= Color.green(pixel);
                moyenne=rouge*3/10+59/100*vert+bleu*11/100;

                bmp.setPixel(i,j,Color.rgb(moyenne,moyenne,moyenne));
            }
        }

    }
    private void ToGrayOp(Bitmap bmp){
        int[] pixels= new int[bmp.getWidth()*bmp.getHeight()];
        int rouge;
        int bleu;
        int vert;
        int moyenne;
        int pixel;
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i=1; i<bmp.getWidth();i++){
            for(int j=1; j<bmp.getHeight() ; j++){
                pixel=pixels[i+(j*bmp.getWidth())];
                bleu=Color.blue(pixel);
                rouge=Color.red(pixel);
                vert= Color.green(pixel);
                moyenne=rouge*3/10+59/100*vert+bleu*11/100;
                pixels[i+(j*bmp.getWidth())]= Color.rgb(moyenne,moyenne,moyenne);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonn =  (Button) findViewById(R.id.button);
        image = (ImageView) findViewById(R.id.imageView);
        txt = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button2);
        Button buttonnn = (Button) findViewById(R.id.button3);
        Button buttonnnn =  (Button) findViewById(R.id.button4);
        Button contraste=(Button) findViewById(R.id.button5);
        Button uncontraste = (Button) findViewById(R.id.button6);
        Button back = (Button) findViewById(R.id.button7);
        //BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        //Bitmap btmpi = drawable.getBitmap();
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;
        final Bitmap btmp=BitmapFactory.decodeResource(getResources(),R.drawable.colline,options);
        //Bitmap btmp=btmpi.copy(Bitmap.Config.ARGB_8888, true);
        final int s = btmp.getHeight();
        final int p = btmp.getWidth();
        final int pixels[]= new int[s*p];
        btmp.getPixels(pixels,0,p,0,0,p,s);
        txt.setText("Height: " + s+ "\nWidth :" + p);
        image.setImageBitmap(btmp);
        button.setText("GrayRapide");
       button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToGrayOp(btmp);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(btmp);
                    }
                }
        );
        buttonn.setText("Equalize");
        buttonn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        equalize(btmp);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(btmp);
                    }
                }
        );
        buttonnn.setText("Colorize");
        buttonnn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        colorize(btmp);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(btmp);
                    }
                }
        );
        buttonnnn.setText("OnlyGreen");
        buttonnnn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        green(btmp);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(btmp);
                    }
                }
        );
        contraste.setText("Constrast");
        contraste.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToContrastedyn(btmp);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(btmp);
                    }
                }
        );
        uncontraste.setText("uncontrast");
        uncontraste.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               lessContraste(btmp);
                                               ((ImageView) findViewById(R.id.imageView)).setImageBitmap(btmp);
                                           }
                                       }
        );
        back.setText("Originale");
        back.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               original(btmp,pixels);
                                               ((ImageView) findViewById(R.id.imageView)).setImageBitmap(btmp);
                                           }
                                       }
        );
    }

}