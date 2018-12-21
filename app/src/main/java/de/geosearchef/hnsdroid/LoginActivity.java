package de.geosearchef.hnsdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import de.geosearchef.hnsdroid.toolbox.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginActivity extends AppCompatActivity {

	EditText serverAddressTextField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		serverAddressTextField = findViewById(R.id.serverAddressTextField);
		serverAddressTextField.setText(HNSService.getSharedPreferences().getString(WebService.SERVER_ADDRESS, ""));

		WebService.init();

	}//TODO request and check permissions


	public void onConnectButtonClicked(View view) {
		CompletableFuture connectFuture = WebService.connectToServer(serverAddressTextField.getText().toString(), HNSService.getSharedPreferences().getInt(WebService.SERVER_PORT, -1));

	}
}
