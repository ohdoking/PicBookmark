package kenny.oh.picBookmark;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class PassPara implements Serializable{

	String text;
	byte[] img;
	Resources re;
	
	
	public void setString(String text){
		this.text = text;
	}
	
	public void setImg(Resources re, Bitmap img){
		//Drawable drawable = new BitmapDrawable(re, img);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.PNG, 100, baos); 
		byte[] b = baos.toByteArray();
		this.img = b;
		this.re = re;
	}

}
