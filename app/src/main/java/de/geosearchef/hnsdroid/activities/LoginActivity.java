package de.geosearchef.hnsdroid.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.geosearchef.hnsdroid.HNSService;
import de.geosearchef.hnsdroid.LocationService;
import de.geosearchef.hnsdroid.R;
import de.geosearchef.hnsdroid.toolbox.FXCallback;
import de.geosearchef.hnsdroid.toolbox.Toolbox;
import de.geosearchef.hnsdroid.web.WebService;
import lombok.var;

import java.util.LinkedList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

	private static String[] requiredPermissions = {
			Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
	};

	private EditText usernameTextField;
	private EditText serverAddressTextField;
	private EditText serverPortTextField;
	private Button connectButton;

	private ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		HNSService.init(getApplicationContext());

		usernameTextField = findViewById(R.id.usernameTextField);
		usernameTextField.setText(HNSService.getSharedPreferences().getString(WebService.PREVIOUS_USERNAME_KEY, ""));

		serverAddressTextField = findViewById(R.id.serverAddressTextField);
		serverAddressTextField.setText(HNSService.getSharedPreferences().getString(WebService.SERVER_ADDRESS_KEY, WebService.getDEFAULT_ADDRESS()));
		serverPortTextField = findViewById(R.id.serverPortTextField);
		serverPortTextField.setText(String.valueOf(HNSService.getSharedPreferences().getInt(WebService.SERVER_PORT_KEY, WebService.getDEFAULT_PORT())));

		connectButton = findViewById(R.id.connectButton);

		progressDialog = Toolbox.generateProgressDialog(this, "Connecting...");

		createNotification();

		WebService.init();

		requestPermissions();
	}


	public void onConnectButtonClicked(View view) {
		progressDialog.show();

		WebService.connectToServer(
				serverAddressTextField.getText().toString(),
				Integer.parseInt(serverPortTextField.getText().toString()),
				usernameTextField.getText().toString(),
				new FXCallback<Object>(this) {
					@Override
					public void onSuccessFX(Object o) {
						Intent intent = new Intent(LoginActivity.this, GameSetupActivity.class);
						startActivity(intent);
						progressDialog.hide();
					}

					@Override
					public void onFailureFX(Exception e) {
						progressDialog.hide();
//						Toast.makeText(LoginActivity.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
						Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});

	}

	private void createNotification() {
		var notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification.Builder(this)
				.setContentTitle("HNSDroid is running")
				.setContentText("I want to stay alive.")
				.setSmallIcon(R.drawable.logo)
				.setOngoing(true)
				.build();

		if (notificationManager != null) {
			notificationManager.notify(8765, notification);
		}
	}

	private void requestAppStandbyExcemption() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			String packageName = this.getPackageName();
			PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
			if (!pm.isIgnoringBatteryOptimizations(packageName)) {
				Intent intent = new Intent();
				intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse("package:" + packageName));
				this.startActivity(intent);

			}
		}
	}

	private static final int PERMISSIONS_REQUEST = 8795;
	private void requestPermissions() {
		List<String> permissionsToBeRequested = new LinkedList<>();
		for(String permission : requiredPermissions) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
				permissionsToBeRequested.add(permission);
			}
		}

		if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_GRANTED) {
			LocationService.init(this);
		}

		if(! permissionsToBeRequested.isEmpty()) {
			ActivityCompat.requestPermissions(this, permissionsToBeRequested.toArray(new String[permissionsToBeRequested.size()]), PERMISSIONS_REQUEST);
		} else {
			requestAppStandbyExcemption();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					connectButton.setEnabled(true);
				} else {
					System.exit(PERMISSIONS_REQUEST);
				}
			}
		}

		LocationService.init(this);
		requestAppStandbyExcemption();
	}
}
