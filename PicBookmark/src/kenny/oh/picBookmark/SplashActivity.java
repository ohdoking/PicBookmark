package kenny.oh.picBookmark;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_splash);

		TextView v2 = (TextView)findViewById(R.id.splash);
		
		ObjectAnimator mover = ObjectAnimator.ofFloat(v2, "alpha",
				0f, 1f);
		mover.setDuration(2000);
		
		
		ObjectAnimator animation2 = ObjectAnimator.ofFloat(v2, "y", 400f, 600f);
		animation2.setDuration(2000);
		animation2.setRepeatCount(ObjectAnimator.INFINITE);
		
		
		AnimatorSet animatorSet1 = new AnimatorSet();
		animatorSet1.playTogether(mover,animation2);
		
		animatorSet1.start();

		new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 1800);
		
		
	}

}
