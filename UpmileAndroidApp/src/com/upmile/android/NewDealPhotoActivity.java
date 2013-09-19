package com.upmile.android;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.upmile.photoutil.AlbumStorageDirFactory;
import com.upmile.photoutil.BaseAlbumDirFactory;
import com.upmile.photoutil.FroyoAlbumDirFactory;

public class NewDealPhotoActivity extends Activity {

	private static final int ACTION_TAKE_PHOTO = 1;
	public static final String PICS_ARRAY = "picsarray";

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private LinearLayout mLinerLt;
	private Bitmap mImageBitmap;

	private Map<String, Boolean>  mapPhotos;
	private Uri currentUri = null;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	// Standard storage location for digital camera files
	private static final String DEAL_ALBUM = "dealsphotos";

	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	
	/* Photo album for this application */
//	private String getAlbumName() {
//		return getString(R.string.album_name);
//	}
	private File getAlbumStorageDir() {
		return mAlbumStorageDirFactory.getAlbumStorageDir(DEAL_ALBUM);
	}

	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = getAlbumStorageDir();
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private void setPic(final Uri uri) {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
		/* Get the size of the ImageView */
		//int targetW = mHorScrlView.getWidth();
		//int targetH = mHorScrlView.getHeight();
		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(uri.getPath(), bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		//if ((targetW > 0) || (targetH > 0)) {
		//	scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		//}
		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;
		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), bmOptions);
		ImageView iView = new ImageView(this);
		iView.setImageBitmap(bitmap);
		iView.setVisibility(View.VISIBLE);
		mapPhotos.put(uri.getPath(), Boolean.FALSE);
		iView.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(v.isSelected()){
							v.setSelected(false);
							v.setBackgroundColor(Color.BLACK);
							mapPhotos.put(uri.getPath(), Boolean.FALSE);
						}else{
							v.setSelected(true);
							v.setBackgroundColor(Color.WHITE);
							mapPhotos.put(uri.getPath(), Boolean.TRUE);
						}
					}
				});
		iView.setPadding(10, 10, 10, 10);
		mLinerLt.addView(iView);
	}

	private void galleryAddPic(Uri uri) {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(uri.getPath());
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			currentUri = Uri.fromFile(createImageFile());
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentUri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
	}

	Button.OnClickListener mTakePicOnClickListener = 
		new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			dispatchTakePictureIntent();
		}
	};

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_deal_photo);
		mLinerLt = (LinearLayout) findViewById(R.id.newDealPhotoLinearLayout);
		Resources res = getResources();
		Drawable divider = res. getDrawable(R.drawable.image_divider);
		mLinerLt.setDividerDrawable(divider);
		mLinerLt.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
		mImageBitmap = null;
		Button okBtn = (Button) findViewById(R.id.newdeal_photo_ok_btn);
		mapPhotos = new HashMap<String, Boolean>();
		okBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Bundle bnd = new Bundle();
						Intent res = new Intent();
						ArrayList<String> picsArr = new ArrayList<String>(); 
						for(Entry<String, Boolean> en : mapPhotos.entrySet()){
							if(en.getValue() == true)
								picsArr.add(en.getKey());
						}
						bnd.putStringArrayList(PICS_ARRAY, picsArr);
						res.putExtras(bnd);
						setResult(Activity.RESULT_OK, res);
						finish();
					}
		});
		Button picBtn = (Button) findViewById(R.id.btnTakePic);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setPic(currentUri);
			galleryAddPic(currentUri);
		}
	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		//mImageView.setImageBitmap(mImageBitmap);
		//mImageView.setVisibility(savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
		//				ImageView.VISIBLE : ImageView.INVISIBLE
		//);
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable(Button btn, Button.OnClickListener onClickListener, String intentName) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}

}
