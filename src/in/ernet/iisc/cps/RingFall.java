package in.ernet.iisc.cps;

import android.app.Activity;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class RingFall extends Activity {

	private Uri notification;
	private Ringtone r;
	private TextView myText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fallen);
		
		myText = (TextView) findViewById(R.id.ringFall);

		((AudioManager) getSystemService(AUDIO_SERVICE))
				.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		BTActivity.setCurrProfile(1);

		notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		if (notification == null)
			notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		r.play();
		
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(1000); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		myText.startAnimation(anim);

	}

	public void stopAlarm(View view) {
		r.stop();
		BTActivity.currAct=BTActivity.prevAct;
		finish();
	}
	
	@Override
	public void onDestroy() {
		r.stop();
		BTActivity.currAct=BTActivity.prevAct;
		finish();
		super.onDestroy();

	}

}
