package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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

import org.w3c.dom.Text;

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
    public void saveImageToGallery(Bitmap btmp){

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        MediaStore.Images.Media.insertImage(getContentResolver(), btmp,"title", "description");
    }
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonn =  (Button) findViewById(R.id.button8);
        final SeekBar barr2= (SeekBar) findViewById(R.id.seekBar3);
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
        Button convu = (Button) findViewById(R.id.button10);
        Button buttonnn = (Button) findViewById(R.id.button3);
        Button buttonnnn =  (Button) findViewById(R.id.button);
        Button contraste=(Button) findViewById(R.id.button5);
        Button uncontraste = (Button) findViewById(R.id.button6);
        Button back = (Button) findViewById(R.id.button7);
        Button laplac=(Button)findViewById(R.id.button11);
        Button save = (Button) findViewById(R.id.button9);
        Button gauss= (Button) findViewById(R.id.button12);
        Button pencil= (Button) findViewById(R.id.button14);
        SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
        Button sobel= (Button) findViewById(R.id.button13);
        Button cartoon = (Button) findViewById(R.id.button15);
        TextView txtt=(TextView) findViewById(R.id.textView2);

        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;
        bitmap =BitmapFactory.decodeResource(getResources(),R.drawable.colline,options);

        final int s = bitmap.getHeight();
        final int p = bitmap.getWidth();
        pixels = new int[s*p];
        bitmap.getPixels(pixels,0,p,0,0,p,s);
        txt.setText("Brightness");
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
                float mid = seekBar.getMax()/200;
                int[] pixels= new int[bitmap.getWidth()*bitmap.getHeight()];
                int pixel;
                int blue;
                int green;
                int red;
                float[] HSV = new float[3];
                bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
                for(int i=0; i<bitmap.getWidth();i++){
                    for(int j=0; j<bitmap.getHeight() ; j++){
                        pixel=pixels[i+(j*bitmap.getWidth())];
                        blue=Color.blue(pixel);
                        red=Color.red(pixel);
                        green= Color.green(pixel);
                        Color.RGBToHSV(red,green,blue,HSV);
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




        txtt.setText("Color");
        barr2.setMax(100);
        barr2.setProgress((0));
        barr2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Processing.colorize(bitmap,seekBar.getProgress());
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
        final android.content.Context context=this;
        convu.setText("moy");
        convu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Convolution.convomoy(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        cartoon.setText("cartoon");
        cartoon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Convolution.cartoon(bitmap,context);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        pencil.setText("pencil");
        pencil.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Convolution.pencil(bitmap,context);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        sobel.setText("sobel");
        sobel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Convolution.convoSobel(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        gauss.setText("gauss");
        gauss.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Convolution.convoGauss(bitmap);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        laplac.setText("Laplac");
        laplac.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Convolution.convoLaplac(bitmap);
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

                        Processing.toGrayOp(bitmap,context);
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
                        Processing.equalize(bitmap);
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
                        Processing.colorize(bitmap,30);
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
                        Processing.green(bitmap);
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
                        Processing.toContrastedyn(bitmap, context);
                        ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                        mAttacher.update();
                    }
                }
        );
        uncontraste.setText("uncontrast");
        uncontraste.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Processing.lessContraste(bitmap);
                                               ((PhotoView) findViewById(R.id.photo_view)).setImageBitmap(bitmap);
                                               mAttacher.update();
                                           }
                                       }
        );
        back.setText("Originale");
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Processing.original(bitmap,pixels);
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