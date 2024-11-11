package com.optimove.android.optimovemobilesdk.ui.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.databinding.FragmentLocationBinding;
import com.optimove.android.optimovemobilesdk.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationFragment extends BaseFragment implements LocationAdapter.OnCoordinateClickListener {

    private FragmentLocationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocationViewModel locationViewModel =
                new ViewModelProvider(this).get(LocationViewModel.class);

        binding = FragmentLocationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.updateButton.setOnClickListener(this::getLocation);

        setScreenInfo("Location");

        setCoordinateButtons(getContext());

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

    private void setCoordinateButtons(Context context) {
        final RecyclerView recyclerView = binding.recyclerView;

        List<CoordinatesItem> items = new ArrayList<>();
        items.add(new CoordinatesItem("Dundee Test 09/08", 56.462018, -2.97072));
        items.add(new CoordinatesItem("Dundee Test 2", 56.458104, -2.973927));
        items.add(new CoordinatesItem("TLV office", 32.061711, 34.788543));
        items.add(new CoordinatesItem("Mall of America", 44.854700, -93.241600));

        LocationAdapter adapter = new LocationAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
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

    @Override
    public void onClick(CoordinatesItem item) {
        binding.latitudeEditText.setText(item.getLatitude());
        binding.longitudeEditText.setText(item.getLongitude());
    }
}
