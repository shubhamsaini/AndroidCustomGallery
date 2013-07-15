package in.ernet.iisc.cps;

import java.io.*;
import java.util.Set;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.*;
import android.media.AudioManager;
import android.os.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BTActivity extends Activity {

	public static AndroidCustomGalleryActivity picGal;
	public static MusicActivity music;

	private static final long DOUBLE_PRESS_INTERVAL = 1500; // in millis
	private static long lastPressTime;
	private static boolean mHasDoubleClicked = false;

	/*
	 * Bluetooth variable declarations
	 */

	public BluetoothAdapter mBluetoothAdapter;
	public static BluetoothDevice device = null;
	private static final UUID MY_UUID_SECURE = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB"); // RN42 UUID
	public static final int MESSAGE_READ = 2;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	public static InputStreamReader aReader = null;
	public static BufferedReader mBufferedReader = null;
	public static InputStream mmInStream = null;
	public static int btOff = 0;
	private String DName = "ring"; // Rings device name (or part of) (enter in
									// lower case ONLY)

	public static int currAct = 1;
	public static int prevAct = 1;

	public static int currProfile = 0;
	public static int switchProfile = AudioManager.RINGER_MODE_SILENT;
	public static String switchProfileMode = "Silent Mode";

	public static Thread myrunthread = null;

	BTActivity(BluetoothAdapter btAdap) {
		mBluetoothAdapter = btAdap;
		AcceptThread();
	}

	/*
	 * Get a list of paired devices and connect to the device identified by
	 * DName
	 */

	public void AcceptThread() {

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device1 : pairedDevices) {
				if (device1.getName().toLowerCase().indexOf(DName) > -1) {
					device = device1;
					break;
				}
			}
		}

		BluetoothSocket tmp = null;
		mmDevice = device;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		Method m;
		try {
			m = device.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });
			tmp = (BluetoothSocket) m.invoke(device, 1);
		} catch (SecurityException e) {

		} catch (NoSuchMethodException e) {
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
			} catch (IOException e1) {

			}
		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		} catch (InvocationTargetException e) {

		}

		mmSocket = tmp;

		run1();

	}

	/*
	 * Function to connect to the found device
	 */

	public void run1() {
		Runnable runnable = new Runnable() {
			public void run() {

				try {
					// Connect the device through the socket. This will block
					// until it succeeds or throws an exception
					mmSocket.connect();
				} catch (IOException connectException) {
					// Unable to connect; close the socket and get out
					try {
						mmSocket.close();
					} catch (IOException closeException) {
					}
					return;
				}
				ConnectedThread();

			}
		};
		Thread mythread = new Thread(runnable);
		mythread.start();
	}

	/*
	 * After connection successfull fetch the input stream and assign to
	 * mmInStream
	 */
	public void ConnectedThread() {

		InputStream tmpIn = null;

		try {
			tmpIn = mmSocket.getInputStream();

		} catch (IOException e) {

		}

		mmInStream = tmpIn;
	}

	/*
	 * Return the current profile ID
	 */

	public static int getCurrProfile() {
		return currProfile;
	}

	/*
	 * Set profile ID
	 */

	public static void setCurrProfile(int a) {
		currProfile = a;
	}

	/*
	 * Function to continuously read data from the bluetooth stream and pass
	 * received data to thread handler
	 */

	public static void runtt() {
		Runnable runnable = new Runnable() {
			public void run() {
				byte[] buffer = new byte[1024];
				int bytes;
				String aString;

				if (mmInStream == null) {

					picGal.mHandler.obtainMessage(
							AndroidCustomGalleryActivity.MESSAGE_READ, -1, -1,
							"Not Connected").sendToTarget();
					return;
				}

				aReader = new InputStreamReader(mmInStream);
				mBufferedReader = new BufferedReader(aReader);

				picGal.mHandler.obtainMessage(
						AndroidCustomGalleryActivity.MESSAGE_READ, -1, -1,
						"Bluetooth Connected").sendToTarget();

				while (true) {
					try {
						bytes = mmInStream.read(buffer);
						aString = new String(buffer);
						buffer = new byte[1024];
						aString = aString.trim();

						if (aString.indexOf("rofile") > -1) {
							Message m = new Message();
							m.obj = (Object) aString;

							long pressTime = System.currentTimeMillis();

							// If double click...
							if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
								mHasDoubleClicked = true;
								if (currAct==1) {
									picGal.mHandler
											.obtainMessage(
													AndroidCustomGalleryActivity.MESSAGE_READ,
													-1, -1, "Double")
											.sendToTarget();
								} else if(currAct==2){
									music.mHandler
											.obtainMessage(
													AndroidCustomGalleryActivity.MESSAGE_READ,
													-1, -1, "Double")
											.sendToTarget();
								}
							} else { // If not double click....
								mHasDoubleClicked = false;
								if (currAct==1) {
									picGal.mHandler.sendMessageDelayed(m,
											DOUBLE_PRESS_INTERVAL);
								} else if(currAct==2) {
									music.mHandler.sendMessageDelayed(m,
											DOUBLE_PRESS_INTERVAL);
								}
							}
							// record the last time the menu button was pressed.
							lastPressTime = pressTime;
						}

						else if (currAct==1) {
							picGal.mHandler.obtainMessage(
									AndroidCustomGalleryActivity.MESSAGE_READ,
									-1, -1, aString).sendToTarget();
						} else if (currAct==2){
							music.mHandler.obtainMessage(
									AndroidCustomGalleryActivity.MESSAGE_READ,
									-1, -1, aString).sendToTarget();
						}
					} catch (IOException e) {
						break;
					}
				}
			}
		};
		myrunthread = new Thread(runnable);
		myrunthread.start();

	}

}