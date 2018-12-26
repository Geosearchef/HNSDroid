package de.geosearchef.hnsdroid.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.geosearchef.hnsdroid.HNSService;
import de.geosearchef.hnsdroid.R;
import de.geosearchef.hnsdroid.toolbox.FXCallback;
import de.geosearchef.hnsdroid.toolbox.Toolbox;
import de.geosearchef.hnsdroid.web.WebService;

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
