package de.geosearchef.hnsdroid.activities;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.geosearchef.hnsdroid.GameService;
import de.geosearchef.hnsdroid.HNSService;
import de.geosearchef.hnsdroid.R;
import de.geosearchef.hnsdroid.data.PlayerType;

public class GameHomeFragment extends Fragment {

    private TextView gameKeyTextView;
    private ToggleButton centerOnUpdateToggleButton;

    private Button setPhantomNameButton;

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

        setPhantomNameButton = root.findViewById(R.id.phantomNameButton);
        setPhantomNameButton.setOnClickListener(setPhantomNameButtonListener);


        if(GameService.playerType != PlayerType.PHANTOM) {
            setPhantomNameButton.setEnabled(false);
        }

        return root;
    }

    View.OnClickListener setPhantomNameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose a phantom name");

            final EditText input = new EditText(getContext());

            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GameService.phantomName = input.getText().toString();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    };
}
