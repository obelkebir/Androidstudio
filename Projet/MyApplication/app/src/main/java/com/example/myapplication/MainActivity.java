package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import android.widget.SeekBar;
import android.widget.TextView;

import com.android.rssample.ScriptC_gris;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private PhotoView image ;
    private TextView txt ;
    private Button button;
    private Bitmap bitmap;
    private int[] pixels;
    Uri imageUri;
    PhotoViewAttacher mAttacher;

    public MainActivity() {

    }



    private void original(Bitmap bmp,int[] pixels){
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }
    private void saveImageToGallery(Bitmap btmp){

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        MediaStore.Images.Media.insertImage(getContentResolver(), btmp,"title", "description");
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
        int rouge=0;
        int vert=0;
        int bleu=0;
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
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
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
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
        for(int i=0; i<bmp.getWidth();i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
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

        RenderScript rs = RenderScript.create(this);
        Allocation input = Allocation.createFromBitmap (rs , bmp ) ;
        Allocation output = Allocation.createTyped (rs , input.getType()) ;
        ScriptC_gris grayScript = new ScriptC_gris(rs);
        grayScript.forEach_maxmin(input);
        grayScript.forEach_contrast(input,output);
        output.copyTo(bmp);
        input.destroy();
        output.destroy();
        grayScript.destroy();
        rs.destroy();
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
        for(int i=0; i<bmp.getWidth();i++){
            for(int j=0; j<bmp.getHeight() ; j++){
                pixel=pixels[i+(j*bmp.getWidth())];
                bleu=Color.blue(pixel);
                rouge=Color.red(pixel);
                vert= Color.green(pixel);
                Color.RGBToHSV(rouge,vert,bleu,HSV);
                if (!((vert>50) && (HSV[1]>0.3) && (bleu+rouge-28<vert))){
                    moyenne= (int) ((float) ((float) rouge*0.33) + (float) (0.59*vert) + (float) bleu*0.11);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        pixels[i+(j*bmp.getWidth())]= Color.rgb(moyenne,moyenne,moyenne);
                    }
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
        for(int i=0; i<bmp.getWidth();i++){
            for(int j=0; j<bmp.getHeight() ; j++){
                pixel=pixels[i+(j*bmp.getWidth())];
                bleu=Color.blue(pixel);
                rouge=Color.red(pixel);
                vert= Color.green(pixel);
                Color.RGBToHSV(rouge,vert,bleu,HSV);
                HSV[0]=30;
                pixels[i+(j*bmp.getWidth())]=Color.HSVToColor(HSV);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

    }
    private void ToGrayOp(Bitmap bmp){
        RenderScript rs = RenderScript.create(this);
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
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonn =  (Button) findViewById(R.id.button8);
        Button upload= (Button)findViewById(R.id.button4);
        image = (PhotoView) findViewById(R.id.photo_view);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        mAttacher = new PhotoViewAttacher((PhotoView) findViewById(R.id.photo_view));

        final int PICK_IMAGE =1 ;
        upload.setText("Upload");
        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery,"Select picture"),1);


            }
        });
        txt = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button2);
        Button buttonnn = (Button) findViewById(R.id.button3);
        Button buttonnnn =  (Button) findViewById(R.id.button);
        Button contraste=(Button) findViewById(R.id.button5);
        Button uncontraste = (Button) findViewById(R.id.button6);
        Button back = (Button) findViewById(R.id.button7);
        Button save = (Button) findViewById(R.id.button9);
        SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
        //BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        //Bitmap btmpi = drawable.getBitmap();
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;
        bitmap =BitmapFactory.decodeResource(getResources(),R.drawable.colline,options);
        //Bitmap btmp=btmpi.copy(Bitmap.Config.ARGB_8888, true);
        final int s = bitmap.getHeight();
        final int p = bitmap.getWidth();
        pixels = new int[s*p];
        bitmap.getPixels(pixels,0,p,0,0,p,s);
        txt.setText("Luminusoité");
        image.setImageBitmap(bitmap);
        save.setText("Save");
        bar.setMax(50);
        bar.setProgress(25);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float prog= seekBar.getProgress() - seekBar.getMax()/2;
                float milieu = seekBar.getMax()/200;
                int[] pixels= new int[bitmap.getWidth()*bitmap.getHeight()];
                int pixel;
                int bleu;
                int vert;
                int rouge;
                float[] HSV = new float[3];
                bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
                for(int i=0; i<bitmap.getWidth();i++){
                    for(int j=0; j<bitmap.getHeight() ; j++){
                        pixel=pixels[i+(j*bitmap.getWidth())];
                        bleu=Color.blue(pixel);
                        rouge=Color.red(pixel);
                        vert= Color.green(pixel);
                        Color.RGBToHSV(rouge,vert,bleu,HSV);
                        HSV[2]= (float) (HSV[2]+ prog/100);
                        if (HSV[2]>1)
                            HSV[2]=1;
                        if (HSV[2]<0)
                            HSV[2]=0;
                        pixels[i+(j*bitmap.getWidth())]= Color.HSVToColor(HSV);
                    }
                }
                bitmap.setPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
                ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                mAttacher.update();
            }
        });
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        saveImageToGallery(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        button.setText("GrayRapide");
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ToGrayOp(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        buttonn.setText("Equalize");
        buttonn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        equalize(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        buttonnn.setText("Colorize");
        buttonnn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        colorize(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        buttonnnn.setText("OnlyGreen");
        buttonnnn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        green(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        contraste.setText("Constrast");
        contraste.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToContrastedyn(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        uncontraste.setText("uncontrast");
        uncontraste.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               lessContraste(bitmap);
                                               ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                                               mAttacher.update();
                                           }
                                       }
        );
        back.setText("Originale");
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        original(bitmap,pixels);
                                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                                        mAttacher.update();
                                    }
                                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            imageUri=data.getData();
            try {
                Bitmap btmpi = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                bitmap=btmpi.copy(Bitmap.Config.ARGB_8888, true);
                image.setImageBitmap(bitmap);
                mAttacher.update();
                int s = bitmap.getHeight();
                int p = bitmap.getWidth();
                pixels = new int[s*p];
                bitmap.getPixels(pixels,0,p,0,0,p,s);


            }catch(IOException e){
                e.printStackTrace();
            }

        }
    }
}