package de.geosearchef.hnsdroid.activities;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import de.geosearchef.hnsdroid.GameService;
import de.geosearchef.hnsdroid.LocationService;
import de.geosearchef.hnsdroid.R;
import de.geosearchef.hnsdroid.data.TimedLocation;

public class GameMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private TextView revealTimerTextView;
    private TextView gameTimerTextView;

    private CountDownTimer timer;

    //Will be recreated each time
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        revealTimerTextView = root.findViewById(R.id.reveal_timer);
        gameTimerTextView = root.findViewById(R.id.game_timer);

        return root;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);

//        LatLng sydney = new LatLng(49, 9);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in somewhere"));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
        de.geosearchef.hnsdroid.data.Location location = LocationService.getMyLocation();
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

        GameService.gameMapFragment = this;
        if(GameService.gameEndTime != 0) {
            loadLocations();
        }
    }

    public void moveCamera(Location loc) {
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
    }

    public void loadLocations() {
        if(timer == null) {
            timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                @Override
                public void onTick(long l) {
                    updateTimers();
                }
                @Override
                public void onFinish() {

                }
            };
            timer.start();
        }

        updateTimers();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                map.clear();
                synchronized (GameService.locations) {
                    for(TimedLocation location : GameService.locations) {
                        LatLng latLng = new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude());
                        Marker marker = map.addMarker(
                                new MarkerOptions()
                                        .position(latLng)
                                        .title(location.getName())
                                        .icon(BitmapDescriptorFactory.defaultMarker(location.isSpecialColor() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED))
                        );
//                        marker.showInfoWindow();  //Will only work for one marker

                        map.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(location.getLocation().getRadius())
                                .clickable(false)
                                .strokeColor(location.isSpecialColor() ? Color.parseColor("#8800a000") : Color.parseColor("#88a00000")));
                    }
                }
            }
        });
    }

    private void updateTimers() {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long nextRevealRemaining = (GameService.nextRevealTime - GameService.serverTime()) / 1000;
                    long gameEndRemaining = (GameService.gameEndTime - GameService.serverTime()) / 1000;
                    revealTimerTextView.setText(String.format("%d:%02d", nextRevealRemaining / 60, nextRevealRemaining % 60));
                    gameTimerTextView.setText(String.format("%d:%02d", gameEndRemaining / 60, gameEndRemaining % 60));
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
