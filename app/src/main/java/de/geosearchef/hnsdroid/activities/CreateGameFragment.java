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
import de.geosearchef.hnsdroid.data.GameOptions;
import de.geosearchef.hnsdroid.data.PlayerType;
import de.geosearchef.hnsdroid.toolbox.FXCallback;
import de.geosearchef.hnsdroid.toolbox.Toolbox;
import de.geosearchef.hnsdroid.web.WebService;

public class CreateGameFragment extends Fragment {

	private EditText createGameTitleTextField;
	private Spinner createPlayerTypeSpinner;
	private EditText createTotalTime;
	private EditText createRevealInterval;
	private Button createGameButton;

	private ArrayAdapter<String> playerTypeAdapter;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_create_game, container, false);

		createGameTitleTextField = root.findViewById(R.id.createGameTitle);

		createPlayerTypeSpinner = root.findViewById(R.id.createPlayerTypeSpinner);
		playerTypeAdapter = GameSetupActivity.getPlayerTypeArrayAdapter(this.getContext());
		createPlayerTypeSpinner.setAdapter(GameSetupActivity.getPlayerTypeArrayAdapter(getContext()));
		createPlayerTypeSpinner.setSelection(0);

		createTotalTime = root.findViewById(R.id.createTotalTime);
		createRevealInterval = root.findViewById(R.id.createHiderRevealInterval);

		createGameButton = root.findViewById(R.id.createButton);
		createGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onCreateGameButtonClicked(view);
			}
		});

		progressDialog = Toolbox.generateProgressDialog(getContext(), "Creating game...");

		return root;
	}

	public void onCreateGameButtonClicked(View view) {
		progressDialog.show();

		WebService.createGame(
				createGameTitleTextField.getText().toString(),
				PlayerType.fromDisplayName((String) createPlayerTypeSpinner.getSelectedItem()),
				new GameOptions(Integer.parseInt(createTotalTime.getText().toString()), Integer.parseInt(createRevealInterval.getText().toString())),
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
						Toast.makeText(CreateGameFragment.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}

}
