package kenny.oh.picBookmark;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	Activity context;
	Preview preview;
	Camera camera;
	ImageView fotoButton;
	TextView detailTitle;
	LinearLayout progressLayout;

	ImageView imgResult;

	RelativeLayout relLay;

	private FrameLayout container;

	static byte[] byteArrayPic;

	// public Intent nextResultActivity;

	public PassPara p;

	public Drawable drawable;

	public static byte[] pic;

	public Bitmap realImage;

	Bundle mBundle;
	Handler mHandler;
	Intent nextResultActivity;

	static int check = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 인스턴스 생성 부분

		container = (FrameLayout) findViewById(R.id.bigview);

		imgResult = (ImageView) findViewById(R.id.imageResult);
		relLay = (RelativeLayout) findViewById(R.id.RelativeLayout1);

		context = this;

		fotoButton = (ImageView) findViewById(R.id.imageView_foto);
		detailTitle = (TextView) findViewById(R.id.detailTitle);
		// progressLayout = (LinearLayout) findViewById(R.id.progress_layout);

		preview = new Preview(this,
				(SurfaceView) findViewById(R.id.KutCameraFragment));
		FrameLayout frame = (FrameLayout) findViewById(R.id.preview);
		frame.addView(preview);
		preview.setKeepScreenOn(true);

		p = new PassPara();
		nextResultActivity = new Intent(getApplicationContext(),
				ResultActivity.class);

		fotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				takeFocusedPicture();

				mHandler = new Handler();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Log.d("Log", "2sec");

						mBundle = new Bundle();
						mBundle.putSerializable("pass", p);
						nextResultActivity.putExtras(mBundle);
						nextResultActivity.putExtra("pic", pic);
						nextResultActivity
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(nextResultActivity);
						check++;
						// nextResultActivity = null;

					}
				}, 3000);

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO Auto-generated method stub

		if (mHandler != null) {
			mHandler.removeMessages(0);
		}

		if (camera == null) {
			camera = Camera.open();
			camera.startPreview();
			camera.setErrorCallback(new ErrorCallback() {
				public void onError(int error, Camera mcamera) {

					camera.release();
					camera = Camera.open();
					Log.d("Camera died", "error camera");

				}
			});
		}
		if (camera != null) {
			if (Build.VERSION.SDK_INT >= 14)
				setCameraDisplayOrientation(context,
						CameraInfo.CAMERA_FACING_BACK, camera);
			preview.setCamera(camera);
		}

		dialogPopup();

	}

/*	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		camera.release();
	}
	*/
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		camera.release();
	}

	/*
	 * @Override protected void onDestroy(){ camera.release(); }
	 */

	private void setCameraDisplayOrientation(Activity activity, int cameraId,
			android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {

			try {
				camera.takePicture(mShutterCallback, null, jpegCallback);
			} catch (Exception e) {

			}

		}
	};

	Camera.ShutterCallback mShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub

		}
	};

	public synchronized void takeFocusedPicture() {
		camera.autoFocus(mAutoFocusCallback);

	}

	PictureCallback jpegCallback = new PictureCallback() {
		@SuppressWarnings("deprecation")
		public void onPictureTaken(byte[] data, Camera camera) {

			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 5;

			options.inPurgeable = true; // Tell to gc that whether it needs free
										// memory, the Bitmap can be cleared

			options.inInputShareable = true; // Which kind of reference will be
												// used to recover the Bitmap
												// data after being clear, when
												// it will be used in the future

			realImage = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			ExifInterface exif = null;

			drawable = new BitmapDrawable(getResources(), realImage);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			realImage.compress(Bitmap.CompressFormat.PNG, 95, baos);
			pic = baos.toByteArray();

		}
	};

	/**
	 * 
	 * PopupDialog
	 */

	public void dialogPopup() {
		// dialog output

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("test");
		alert.setMessage("Enter Your Name");

		final EditText input = new EditText(context);
		alert.setView(input);

		alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				detailTitle.setText(input.getEditableText().toString());

				p.setString(input.getEditableText().toString());

			}
		});

		AlertDialog alertDialog = alert.create();
		alertDialog.show();

	}

}
