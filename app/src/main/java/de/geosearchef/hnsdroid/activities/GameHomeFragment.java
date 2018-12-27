package de.geosearchef.hnsdroid.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.geosearchef.hnsdroid.GameService;
import de.geosearchef.hnsdroid.HNSService;
import de.geosearchef.hnsdroid.R;

public class GameHomeFragment extends Fragment {

    private TextView gameKeyTextView;
    private ToggleButton centerOnUpdateToggleButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        gameKeyTextView = root.findViewById(R.id.gameKeyTextView);
        gameKeyTextView.setText(String.valueOf(GameService.gameKey));

        centerOnUpdateToggleButton = root.findViewById(R.id.centerOnUpdateToggleButton);
        centerOnUpdateToggleButton.setChecked(HNSService.centerOnLocationUpdate);
        centerOnUpdateToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HNSService.centerOnLocationUpdate = centerOnUpdateToggleButton.isChecked();
            }
        });

        return root;
    }
}
