/*
 * The input strings to be sent from the ring are as follows:
 * 
 * profile - toggles between silent,vibrate and loud modes
 * left - displays previous image
 * right - displays next image
 * 
 */

package in.ernet.iisc.cps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ToggleButton;

public class AndroidCustomGalleryActivity extends Activity {

	/*
	 * Variable Declarations for image gallery and Bluetooth adapter
	 */

	ViewFlipper imageViewFlipper;
	Handler handlerT;
	Runnable runT;
	ArrayList<String> fileList = new ArrayList<String>();
	ImageView imageView[];
	int imgCount;
	int whichChild = 0;
	String IMG_PATH = "/mnt/sdcard/dew/"; // whr to look for stored images
	public static final int MESSAGE_READ = 2;
	public static BTActivity global = null;
	private BluetoothAdapter mBluetoothAdapter;
	public int REQUEST_CODE = 1;
	private Animation slide_in_left, slide_in_right, slide_out_left,
			slide_out_right; // image transition animation
	public int currImageCount;
	private Bitmap bmp[];
	private int flipIndex = 0;
	private ToggleButton toggle;
	//TextView text;

	private static long lastPressTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		BTActivity.picGal = this;
		
		// = (TextView) findViewById(R.id.textView);
		
		//if the current mode is silent, turn the toggle button OFF, else turn it ON
		toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		if (BTActivity.switchProfile == AudioManager.RINGER_MODE_SILENT)
			toggle.setChecked(false);
		else
			toggle.setChecked(true);

		//declare the image flipper which can contain multiple ImageViews
		imageViewFlipper = (ViewFlipper) findViewById(R.id.main_flipper);
		bmp = new Bitmap[2];
		imageView = new ImageView[3];
		imageView[0] = new ImageView(this);
		imageView[1] = new ImageView(this);
		imageView[2] = new ImageView(this);

		/* Setup the image flipper */

		setImagesToFlipper(imageViewFlipper, IMG_PATH); //load all image paths
		imgCount = fileList.size();		//find the number of images found

		/* Show the first image in the Flipper */
		imageViewFlipper.removeAllViews();		//clear the flipper
		slide_in_left = AnimationUtils
				.loadAnimation(this, R.anim.slide_in_left);
		slide_in_right = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_right);
		slide_out_left = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_left);
		slide_out_right = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_right);
		bmp[0] = BitmapFactory.decodeFile(fileList.get(whichChild));		//decode the first file into BMP
		imageView[0].setImageBitmap(bmp[0]);		//set the bmp file to the imageview
		imageViewFlipper.addView(imageView[0]);		//add the imageview to the flipper
		currImageCount = 1;

		/* Initializing Swipe Detection */
		ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(
				this);
		RelativeLayout lowestLayout = (RelativeLayout) this
				.findViewById(R.id.lowestLayout);
		lowestLayout.setOnTouchListener(activitySwipeDetector);

		/*
		 * Calling the Bluetooth Activity Intent and requesting to turn ON
		 */

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			BTActivity.btOff = 1;
			return;
		}
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivityForResult(discoverableIntent, REQUEST_CODE);

	}

	/* clicking the silent -> vibrate toggle button */

	public void onToggleClicked(View view) {

		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			BTActivity.switchProfile = AudioManager.RINGER_MODE_VIBRATE;
			BTActivity.switchProfileMode = "Vibrate Mode";
		} else {
			BTActivity.switchProfile = AudioManager.RINGER_MODE_SILENT;
			BTActivity.switchProfileMode = "Silent Mode";
		}
	}

	/* Storing the path of all images in the filesList */

	private void setImagesToFlipper(ViewFlipper flipper, String path) {
		File home = new File(path);
		for (File file : home.listFiles()) {

			if (file.isDirectory()) {
				if (file.canRead())
					setImagesToFlipper(flipper, file.getPath());
			} else {
				String filename = file.getName();
				String ext = filename.substring(filename.lastIndexOf('.') + 1,
						filename.length());
				if (ext.toLowerCase().equals("jpg")
						|| ext.toLowerCase().equals("png")
						|| ext.toLowerCase().equals("bmp")
						|| ext.toLowerCase().equals("jpeg")) {
					fileList.add(file.getAbsolutePath());
				}
			}
		}
	}

	/*
	 * ResultData received from BT intent and performing apt actions: If enabled
	 * - initialise BTActivity object and start reading for data If denied - set
	 * btOff identifier to 1 and run the app without bluetooth support
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 300:											//if you choose to turn BT on
			while (!mBluetoothAdapter.isEnabled())		//till the bluetoooth adapter is turned on, prog is stalled
				;
			global = new BTActivity(mBluetoothAdapter);
			BTActivity.runtt();
			break;

		case RESULT_CANCELED:		//if the BT option is cancelled
			Toast.makeText(getApplicationContext(), "Bluetooth cancelled",
					Toast.LENGTH_SHORT).show();
			BTActivity.btOff = 1;
			break;

		case RESULT_OK:						//		when returned from music player, this piece of code is executed
			
			Toast.makeText(getApplicationContext(),
					"Returned from Music Player", Toast.LENGTH_SHORT).show();

			if (BTActivity.btOff == 0) {
				BTActivity.currAct = 1;
			}
			if (BTActivity.switchProfile == AudioManager.RINGER_MODE_SILENT)
				toggle.setChecked(false);
			else
				toggle.setChecked(true);
			break;

		}
	}

	/* Function to flip to next image */

	public void onRight(View view) {
		showNextImage();
	}

	/* Function to flip to previous image */

	public void onLeft(View view) {
		showPrevImage();
	}

	/*
	 * Switching to the music activity
	 */
	public void onClick(View view) {
		BTActivity.currAct = 2;
		Intent nextScreen = new Intent(getApplicationContext(),
				MusicActivity.class);
		startActivityForResult(nextScreen, REQUEST_CODE);

	}

	/*
	 * Thread handler for taking appropriate actions after receiving a known
	 * input
	 */

	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String readMessage = (String) msg.obj;

			
			//text.setText(readMessage);
			
			if (readMessage.indexOf("rofile") > -1) {
				long pressTime = System.currentTimeMillis();

				if (pressTime - lastPressTime <= 1500) {
					// do nothing
				} else {

					if (BTActivity.getCurrProfile() == 0) {
						((AudioManager) getSystemService(AUDIO_SERVICE))
								.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						BTActivity.setCurrProfile(1);
						Toast.makeText(getApplicationContext(), "Loud Mode",
								Toast.LENGTH_SHORT).show();
					} else if (BTActivity.getCurrProfile() == 1) {
						((AudioManager) getSystemService(AUDIO_SERVICE))
								.setRingerMode(BTActivity.switchProfile);
						BTActivity.setCurrProfile(0);
						Toast.makeText(getApplicationContext(),
								BTActivity.switchProfileMode,
								Toast.LENGTH_SHORT).show();
					}
				}
			} else if (readMessage.indexOf("Double") > -1) {
				Toast.makeText(getApplicationContext(), "Emergency",
						Toast.LENGTH_SHORT).show();
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:09958840601"));
				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				callIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
				startActivity(callIntent);
				lastPressTime = System.currentTimeMillis();
			}

			else if (readMessage.indexOf("left") > -1) {
				Toast.makeText(getApplicationContext(), "Next Image",
						Toast.LENGTH_SHORT).show();
				showNextImage();
			} else if (readMessage.indexOf("right") > -1) {
				Toast.makeText(getApplicationContext(), "Previous Image",
						Toast.LENGTH_SHORT).show();
				showPrevImage();
			} /*else if (readMessage.indexOf("fall") > -1) {
				BTActivity.currAct=3;
				BTActivity.prevAct=1;
				Intent nextScreen = new Intent(getApplicationContext(),
						RingFall.class);
				
				nextScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(nextScreen);
			}else if (readMessage.indexOf("Bluetooth") > -1) {
				Toast.makeText(getApplicationContext(), readMessage,
						Toast.LENGTH_SHORT).show();
			}*/

			else if (readMessage.indexOf("Not Connected") > -1) {
				BTActivity.runtt();
			}

		}
	};

	/* Class for swiping gesture detection */

	//i dunno how it works... its a very common piece of code !!
	
	public class ActivitySwipeDetector implements View.OnTouchListener {

		static final String logTag = "ActivitySwipeDetector";
		private Activity activity;
		static final int MIN_DISTANCE = 100;
		private float downX, downY, upX, upY;

		public ActivitySwipeDetector(Activity activity) {
			this.activity = activity;
		}

		public void onRightToLeftSwipe() {
			showNextImage();
		}

		public void onLeftToRightSwipe() {
			showPrevImage();
		}

		public void onTopToBottomSwipe() {
		}

		public void onBottomToTopSwipe() {
		}

		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				downX = event.getX();
				downY = event.getY();
				return true;
			}
			case MotionEvent.ACTION_UP: {
				upX = event.getX();
				upY = event.getY();

				float deltaX = downX - upX;
				float deltaY = downY - upY;

				// swipe horizontal?
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					// left or right
					if (deltaX < 0) {
						this.onLeftToRightSwipe();
						return true;
					}
					if (deltaX > 0) {
						this.onRightToLeftSwipe();
						return true;
					}
				} else {
					return false; // We don't consume the event
				}
				if (Math.abs(deltaY) > MIN_DISTANCE) {
					// top or down
					if (deltaY < 0) {
						this.onTopToBottomSwipe();
						return true;
					}
					if (deltaY > 0) {
						this.onBottomToTopSwipe();
						return true;
					}
				} else {

					return false; // We don't consume the event
				}
				return true;
			}
			}
			return false;
		}
	}

	/* Close the bluetooth socket before exitting the app */

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (BTActivity.btOff == 0) {			//only close BT socket if BT was used at all !
			try {
				global.mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	// function to show next image
	public void showNextImage() {
		if (currImageCount == 5) {
			imageViewFlipper.removeAllViews();
			currImageCount = 0;
		}

		whichChild = (whichChild + 1) % imgCount;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		bmp[0] = BitmapFactory.decodeFile(fileList.get(whichChild), options);
		imageView[(flipIndex + 1) % 3] = new ImageView(this);
		imageView[(flipIndex + 1) % 3].setImageBitmap(bmp[0]);
		imageViewFlipper.addView(imageView[(flipIndex + 1) % 3]);
		currImageCount++;
		imageViewFlipper.setInAnimation(slide_in_right);
		imageViewFlipper.setOutAnimation(slide_out_left);
		imageViewFlipper.showNext();
		imageView[(flipIndex + 2) % 3].setImageBitmap(null);
		imageView[(flipIndex + 2) % 3] = null;
		flipIndex++;

	}

	// function to show previous image
	public void showPrevImage() {
		if (currImageCount == 5) {
			imageViewFlipper.removeAllViews();
			currImageCount = 0;
		}

		if (whichChild != 0)
			whichChild = (whichChild - 1) % imgCount;
		else
			whichChild = imgCount - 1;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		bmp[0] = BitmapFactory.decodeFile(fileList.get(whichChild), options);
		imageView[(flipIndex + 1) % 3] = new ImageView(this);
		imageView[(flipIndex + 1) % 3].setImageBitmap(bmp[0]);
		imageViewFlipper.addView(imageView[(flipIndex + 1) % 3]);
		currImageCount++;
		imageViewFlipper.setInAnimation(slide_in_left);
		imageViewFlipper.setOutAnimation(slide_out_right);
		imageViewFlipper.showNext();

		imageView[(flipIndex + 2) % 3].setImageBitmap(null);
		imageView[(flipIndex + 2) % 3] = null;
		flipIndex++;
	}
}