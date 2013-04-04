package com.example.cameratesting;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ShootAndCropActivity extends Activity implements OnClickListener {
	
	//keep track of camera capture intent
	final int CAMERA_CAPTURE = 1;
	//captured picture uri
	private Uri picUri;
	//keep track of cropping intent
	final int PIC_CROP = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//retrieve a reference to the UI button
		
		Button captureBtn = (Button)findViewById(R.id.capture_btn);
		//handle button clicks
		captureBtn.setOnClickListener(this);
	}
	
	/**
     * Click method to handle user pressing button to launch camera
     */
	public void onClick(View v) {
	    if (v.getId() == R.id.capture_btn) {
	    	
	    	try {
	    	    //use standard intent to capture an image
	    	    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	    //we will handle the returned data in onActivityResult
	    	    startActivityForResult(captureIntent, CAMERA_CAPTURE);
	    	
	    	}catch(ActivityNotFoundException anfe){
	    	    //display an error message
	    	    String errorMessage = "Whoops - your device doesn't support capturing images!";
	    	    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
	    	    toast.show();
	    	}
	    	
	    }
	}
	
	/**
     * Handle user returning from both capturing and cropping the image
     */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	    	//user is returning from capturing an image using the camera
	    	if(requestCode == CAMERA_CAPTURE){
	    		//get the Uri for the captured image
	    		picUri = data.getData();
	    		//carry out the crop operation
	    		
	    		performCrop();
	    	}
	    	//user is returning from cropping the image
	    	else if(requestCode == PIC_CROP){
	    		//get the returned data
	    		Bundle extras = data.getExtras();
	    		//get the cropped bitmap
	    		Bitmap thePic = extras.getParcelable("data");
	    		//retrieve a reference to the ImageView
	    		ImageView picView = (ImageView)findViewById(R.id.picture);
	    		//display the returned cropped image
	    		
	    		Bitmap newPic = preformColorCorrection(thePic);
	    		
	    		picView.setImageBitmap(newPic);
	    	}
	    }
	}
	
	
	
	private Bitmap preformColorCorrection(Bitmap thePic) {
		// TODO Auto-generated method stub
		
		int width, height;
		height = thePic.getHeight();
        width = thePic.getWidth();
        Bitmap newBitmapImage = Bitmap.createBitmap(width, height, thePic.getConfig());
        
     // color information
	    int A, R, G, B;
	    int pixel;
	    int type = 1;
	    int percent = 150;
	    int contrastValue = 10;
        
        /**
         * 0 = no effect
         * 1 = greyscale
         * 2 = contrast
         * 3 = color boost
         */
        int currentEffect = 4;
		
		switch(currentEffect){
			case 0:
				newBitmapImage = thePic;
				break;
				
			case 1:// greyscale
				// working greyscale
				// bitmap do greyscale
				
				//Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		        //Bitmap newBitmapImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
				
				Canvas c = new Canvas(newBitmapImage);
		        Paint paint = new Paint();
		        ColorMatrix cm = new ColorMatrix();
		        cm.setSaturation(0);
		        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		        paint.setColorFilter(f);
		        c.drawBitmap(thePic, 0, 0, paint);
				break;
			
			case 2:// contrast
				// contrast
				//Bitmap newBitmapImage = Bitmap.createBitmap(width, height, thePic.getConfig());
				
			    
			    // get contrast value
			    double contrast = Math.pow((100 + contrastValue) / 100, 2);
			 
			    // scan through all pixels
			    for(int x = 0; x < width; ++x) {
			        for(int y = 0; y < height; ++y) {
			            // get pixel color
			            pixel = thePic.getPixel(x, y);
			            A = Color.alpha(pixel);
			            // apply filter contrast for every channel R, G, B
			            R = Color.red(pixel);
			            R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
			            if(R < 0) { R = 0; }
			            else if(R > 255) { R = 255; }
			 
			            G = Color.red(pixel);
			            G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
			            if(G < 0) { G = 0; }
			            else if(G > 255) { G = 255; }
			 
			            B = Color.red(pixel);
			            B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
			            if(B < 0) { B = 0; }
			            else if(B > 255) { B = 255; }
			 
			            // set new pixel color to output bitmap
			            newBitmapImage.setPixel(x, y, Color.argb(A, R, G, B));
			        }
			    }
				break;
				
			case 3:// color boost
//				int A, R, G, B;
//			    int pixel;
			 
			    for(int x = 0; x < width; ++x) {
			        for(int y = 0; y < height; ++y) {
			            pixel = thePic.getPixel(x, y);
			            A = Color.alpha(pixel);
			            R = Color.red(pixel);
			            G = Color.green(pixel);
			            B = Color.blue(pixel);
			            if(type == 1) {
			                R = (int)(R * (1 + percent));
			                if(R > 255) R = 255;
			            }
			            else if(type == 2) {
			                G = (int)(G * (1 + percent));
			                if(G > 255) G = 255;
			            }
			            else if(type == 3) {
			                B = (int)(B * (1 + percent));
			                if(B > 255) B = 255;
			            }
			            newBitmapImage.setPixel(x, y, Color.argb(A, R, G, B));
			        }
			    }
				break;
				
			case 4: //engraving
				int depth = 125;
				double red = 1.5,
					green = 0.6,
					blue = 0.12;
				// The top sepia (1.5, 0.6, 0.12)
				// Green-majored toning: (R, G, B) = (0.88, 2.45, 1.43)
				// Blue-majored toning: (R, G, B) = (1.2, 0.87, 2.1)
				
				// constant grayscale
			    final double GS_RED = 0.3;
			    final double GS_GREEN = 0.59;
			    final double GS_BLUE = 0.11;
			    
			 
			    // scan through all pixels
			    for(int x = 0; x < width; ++x) {
			        for(int y = 0; y < height; ++y) {
			            // get pixel color
			            pixel = thePic.getPixel(x, y);
			            // get color on each channel
			            A = Color.alpha(pixel);
			            R = Color.red(pixel);
			            G = Color.green(pixel);
			            B = Color.blue(pixel);
			            // apply grayscale sample
			            B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
			 
			            // apply intensity level for sepid-toning on each channel
			            R += (depth * red);
			            if(R > 255) { R = 255; }
			 
			            G += (depth * green);
			            if(G > 255) { G = 255; }
			 
			            B += (depth * blue);
			            if(B > 255) { B = 255; }
			 
			            // set new pixel color to output image
			            newBitmapImage.setPixel(x, y, Color.argb(A, R, G, B));
			        }
			    }
				break;
		}
		
	    
	    // saving to pictures
	    try {
	    	
			String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString(); 
			
			System.out.println(path); // console.log for java
			System.out.println(System.currentTimeMillis());
    		
			OutputStream fOut = null;
    		File file = new File(path, "testingCropImage_"+ System.currentTimeMillis() +".jpg");
    		fOut = new FileOutputStream(file);
    		newBitmapImage.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
    		fOut.flush();
    		fOut.close();
    		MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
		} catch (Exception e) {
    		e.printStackTrace();
    	}
	    
	    return newBitmapImage;
		
	}

	
	
	
	
	
	
	/**
     * Helper method to carry out crop operation
     */
	private void performCrop(){
		try {
		    //call the standard crop action intent (the user device may not support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			    //indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			    //set crop properties
			cropIntent.putExtra("crop", "true");
			    //indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			    //indicate output X and Y
			cropIntent.putExtra("outputX", 256);
			cropIntent.putExtra("outputY", 256);
			    //retrieve data on return
			cropIntent.putExtra("return-data", true);
			
			
			//start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);
		}
		catch(ActivityNotFoundException anfe){
		    //display an error message
		    String errorMessage = "Whoops - your device doesn't support the crop action!";
		    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
		    toast.show();
		}
	}
	
	
	
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shoot_and_crop, menu);
		return true;
	}

}
