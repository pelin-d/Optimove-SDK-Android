package com.optimove.android.optimovemobilesdk.ui.location;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.databinding.FragmentLocationBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

import java.util.Objects;

public class LocationFragment extends BaseFragment {

    private FragmentLocationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocationViewModel locationViewModel =
                new ViewModelProvider(this).get(LocationViewModel.class);

        binding = FragmentLocationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.updateButton.setOnClickListener(this::getLocation);

        setScreenInfo("Location");

        return root;
    }

    public Location getLocationFromLatLng(String latitudeString, String longitudeString) {
        double latitude = Double.parseDouble(latitudeString);
        double longitude = Double.parseDouble(longitudeString);

        Location location = new Location("GPS"); // "provider" can be any string, typically you use "GPS" or "Network"
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    public void getLocation(View v) {
        String latitude = Objects.requireNonNull(binding.latitudeEditText.getText()).toString();
        String longitude = Objects.requireNonNull(binding.longitudeEditText.getText()).toString();

        if (latitude.isEmpty()) {
            latitude = "0.0";
            binding.latitudeEditText.setText("0.0");
        }
        if (longitude.isEmpty()) {
            longitude = "0.0";
            binding.longitudeEditText.setText("0.0");
        }

        Optimove instance = Optimove.getInstance();
        Location location = getLocationFromLatLng(latitude, longitude);
        instance.sendLocationUpdate(location);
        showMessage(v, "Sent location update");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
