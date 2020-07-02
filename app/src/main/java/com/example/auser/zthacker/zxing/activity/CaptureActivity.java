package com.example.auser.zthacker.zxing.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.example.auser.zthacker.R;
import com.example.auser.zthacker.base.BaseActivity;
import com.example.auser.zthacker.base.Config;
import com.example.auser.zthacker.bean.NormalObjData;
import com.example.auser.zthacker.bean.VideoInfoBean;
import com.example.auser.zthacker.http.ApiRequestData;
import com.example.auser.zthacker.ui.activity.mine.ScanInputActivity;
import com.example.auser.zthacker.ui.activity.mine.VideoManagerListActivity;
import com.example.auser.zthacker.utils.SPUtils;
import com.example.auser.zthacker.utils.TextUtil;
import com.example.auser.zthacker.utils.ToastUtil;
import com.example.auser.zthacker.zxing.camera.CameraManager;
import com.example.auser.zthacker.zxing.decoding.CaptureActivityHandler;
import com.example.auser.zthacker.zxing.decoding.InactivityTimer;
import com.example.auser.zthacker.zxing.view.ViewfinderView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback {
	@BindView(R.id.tv_input_scan)
	TextView tv_input_scan;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private String userId;
	private GsonBuilder gsonBuilder;
	//private Button cancelScanButton;

	/** Called when the activity is first created. */

	public static void startActivity(Context context){
		Intent intent = new Intent(context,CaptureActivity.class);
		context.startActivity(intent);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);
		ButterKnife.bind(this);
		setTop(R.color.black);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		//cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		initView();
	}

	private void initView() {
		setTop(R.color.black);
		setCentreText("扫一扫");
		userId = SPUtils.getSharedStringData(this, "userId");
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			/**
			 * SURFACE_TYPE_PUSH_BUFFERS表明该Surface不包含原生数据，Surface用到的数据由其他对象提供，
			 * 在Camera图像预览中就使用该类型的Surface，有Camera负责提供给预览Surface数据，这样图像预览会比较流畅。
			 */
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		Log.e("start","time::" + (new Date()).getTime());
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		final String resultString = result.getText();
        String[] split = resultString.split("=");
		int length = split.length;
		Log.e("gsgdhf",split[length-1]+"--------");
		//FIXME
		if (!TextUtil.isNull(resultString)){
			ApiRequestData.getInstance(this).ShowDialog(null);
			OkGo.post(ApiRequestData.getInstance(this).MineScanCode)
					.tag(this)
					.params("appUserId",userId)
					.params("scancode",split[length-1])
					.execute(new StringCallback() {
						@Override
						public void onSuccess(String s, Call call, Response response) {
							if (gsonBuilder==null){
								gsonBuilder = new GsonBuilder();
							}
							ApiRequestData.getInstance(CaptureActivity.this).getDialogDismiss();
							NormalObjData<String> mData = gsonBuilder
									.setPrettyPrinting()
									.disableHtmlEscaping()
									.create().fromJson(s, new TypeToken<NormalObjData<String>>(){}.getType());

							if (!TextUtil.isNull(mData.getCode())&&mData.getCode().equals("0")){
								ToastUtil.show(CaptureActivity.this,mData.getMsg());

								SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
								SurfaceHolder surfaceHolder = surfaceView.getHolder();
								initCamera(surfaceHolder);
								// 恢复活动监控器
								//inactivityTimer.onActivity();
								if (handler != null){
									handler.restartPreviewAndDecode();
								}
								return;
							}
							if (!TextUtil.isNull(mData.getCode())&&mData.getCode().equals("1")&&!TextUtil.isNull(mData.getData())){
								EventBus.getDefault().post(Config.SCAN_SUCCESS);
								VideoManagerListActivity.startActivity(CaptureActivity.this,mData.getData());
								finish();
							}
						}
						@Override
						public void onError(Call call, Response response, Exception e) {
							super.onError(call, response, e);
							ToastUtil.show(CaptureActivity.this,e.getMessage());
							ApiRequestData.getInstance(CaptureActivity.this).getDialogDismiss();
						}
					});
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};


	@OnClick({R.id.image_back,R.id.tv_input_scan})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.image_back:
				finish();
				break;
			case R.id.tv_input_scan:
				ScanInputActivity.startActivity(this);
				break;
		}
	}
}