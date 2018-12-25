package de.geosearchef.hnsdroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.geosearchef.hnsdroid.toolbox.Callback;
import de.geosearchef.hnsdroid.web.WebService;

import java.util.LinkedList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

	private static String[] requiredPermissions = {
			Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
	};

	EditText usernameTextField;
	EditText serverAddressTextField;
	EditText serverPortTextField;
	Button connectButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		HNSService.init(getApplicationContext());

		usernameTextField = findViewById(R.id.usernameTextField);
		usernameTextField.setText(HNSService.getSharedPreferences().getString(WebService.PREVIOUS_USERNAME_KEY, ""));

		serverAddressTextField = findViewById(R.id.serverAddressTextField);
		serverAddressTextField.setText(HNSService.getSharedPreferences().getString(WebService.SERVER_ADDRESS_KEY, "geosearchef.de"));
		serverPortTextField = findViewById(R.id.serverPortTextField);
		serverPortTextField.setText(String.valueOf(HNSService.getSharedPreferences().getInt(WebService.SERVER_PORT_KEY, WebService.getDEFAULT_PORT())));

		connectButton = findViewById(R.id.connectButton);

		WebService.init();

		requestPermissions();
	}


	public void onConnectButtonClicked(View view) {
		WebService.connectToServer(
				serverAddressTextField.getText().toString(),
				Integer.parseInt(serverPortTextField.getText().toString()),
				usernameTextField.getText().toString(),
				new Callback<Object>() {
					@Override
					public void onSuccess(Object o) {
						//TODO:
					}

					@Override
					public void onFailure(RuntimeException e) {
						//TODO:
					}
				});

	}

	private static final int PERMISSIONS_REQUEST = 8795;
	public void requestPermissions() {
		List<String> permissionsToBeRequested = new LinkedList<>();
		for(String permission : requiredPermissions) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
				permissionsToBeRequested.add(permission);
			}
		}

		ActivityCompat.requestPermissions(this, permissionsToBeRequested.toArray(new String[permissionsToBeRequested.size()]), PERMISSIONS_REQUEST);
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
	}
}
