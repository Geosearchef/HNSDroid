package de.geosearchef.hnsdroid.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.geosearchef.hnsdroid.R;
import de.geosearchef.hnsdroid.data.PlayerType;
import de.geosearchef.hnsdroid.toolbox.FXCallback;
import de.geosearchef.hnsdroid.toolbox.Toolbox;
import de.geosearchef.hnsdroid.web.WebService;

public class JoinGameFragment extends Fragment {

	private EditText joinGameKey;
	private Spinner joinPlayerTypeSpinner;
	private Button joinGameButton;

	private ArrayAdapter<String> playerTypeAdapter;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_join_game, container, false);

		joinGameKey = root.findViewById(R.id.joinGameKey);

		joinPlayerTypeSpinner = root.findViewById(R.id.joinPlayerTypeSpinner);
		playerTypeAdapter = GameSetupActivity.getPlayerTypeArrayAdapter(this.getContext());
		joinPlayerTypeSpinner.setAdapter(GameSetupActivity.getPlayerTypeArrayAdapter(getContext()));
		joinPlayerTypeSpinner.setSelection(1);

		joinGameButton = root.findViewById(R.id.joinButton);
		joinGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onJoinGameButtonClicked(view);
			}
		});

		progressDialog = Toolbox.generateProgressDialog(getContext(), "Joining game...");

		return root;
	}


	public void onJoinGameButtonClicked(View view) {
		progressDialog.show();

		WebService.joinGame(
				Integer.parseInt(joinGameKey.getText().toString()),
				PlayerType.fromDisplayName((String) joinPlayerTypeSpinner.getSelectedItem()),
				new FXCallback(getActivity()) {
					@Override
					public void onSuccessFX(Object o) {
						Intent intent = new Intent(getContext(), MapsActivity.class);
						startActivity(intent);
						progressDialog.hide();
					}
					@Override
					public void onFailureFX(Exception e) {
						progressDialog.hide();
						Toast.makeText(JoinGameFragment.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}

}
