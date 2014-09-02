package kenny.oh.picBookmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ResultActivity extends Activity {

	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	
	Activity context;
	TextView detailTitle;
	
	ImageView imgResult;
	

	private FrameLayout container;
	
	static byte[] byteArrayPic;
	Bitmap bmp;
	Drawable d;
	byte[] b;
	Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_acitivity);
		
		container = (FrameLayout) findViewById(R.id.imageScreenRA);
		
		imgResult = (ImageView)findViewById(R.id.imageResultRA);
		
		context = this;
		detailTitle = (TextView)findViewById(R.id.detailTitleRA);
		// progressLayout = (LinearLayout) findViewById(R.id.progress_layout);

		i = getIntent();
		PassPara p = (PassPara)i.getSerializableExtra("pass");
		
		detailTitle.setText(p.text);
		
		Log.d("Log", "result text success");
		
		b = i.getExtras().getByteArray("pic");

		bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

		Log.d("Log", "result bmp success");
		
		d = new BitmapDrawable(getResources(),bmp);
		imgResult.setBackground(d);
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.result_acitivity, menu);
	    return true;
	  } 
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.action_save:
	      Toast.makeText(this, "Save", Toast.LENGTH_SHORT)
	          .show();
	      
	      capturePic();
	      savePic();
	      
	      finish();
	    // onBackPressed();
	      //startActivity(i);
	      break;
	  
	    default:
	      break;
	    }

	    return true;
	  } 
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		
	}
	
	public void savePic(){

		FileOutputStream outStream = null;
		Calendar c = Calendar.getInstance();
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());

		try {
			// Write to SD Card
			outStream = new FileOutputStream(mediaStorageDir.getPath()
					+ File.separator + "IMG_" + timeStamp + ".jpg");
			outStream.write(byteArrayPic);
			//outStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			

		}
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 5;

		options.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared

		options.inInputShareable = true; // Which kind of reference will be
											// used to recover the Bitmap
											// data after being clear, when
											// it will be used in the future

		Bitmap realImage = BitmapFactory.decodeByteArray(byteArrayPic, 0, byteArrayPic.length,
				options);
		ExifInterface exif = null;


		try {
			exif = new ExifInterface(mediaStorageDir.getPath()
					+ File.separator + "IMG_" + timeStamp + ".jpg");

			// p.setImg(getResources(), realImage);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Log.d("EXIF value",
					exif.getAttribute(ExifInterface.TAG_ORIENTATION));
			if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
					.equalsIgnoreCase("1")) {
				realImage = rotate(realImage, 90);
			} else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
					.equalsIgnoreCase("8")) {
				realImage = rotate(realImage, 90);
			} else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
					.equalsIgnoreCase("3")) {
				realImage = rotate(realImage, 90);
			} else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
					.equalsIgnoreCase("0")) {
				realImage = rotate(realImage, 90);
			}
		} catch (Exception e) {

		}
	}
	
	public static Bitmap rotate(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, false);
	}
	public void capturePic(){
		
		container.buildDrawingCache();
	    Bitmap captureView = container.getDrawingCache();
	    
	    byteArrayPic = bitmapToByteArray(captureView);
	    
	    container.destroyDrawingCache();
	}
	
	public byte[] bitmapToByteArray( Bitmap $bitmap ) {  
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
        $bitmap.compress( CompressFormat.JPEG, 100, stream) ;  
        byte[] byteArray = stream.toByteArray() ;  
        return byteArray;  
    }  
}
