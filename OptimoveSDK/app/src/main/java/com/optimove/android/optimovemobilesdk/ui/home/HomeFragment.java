package com.optimove.android.optimovemobilesdk.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optimove.android.optimovemobilesdk.R;
import com.optimove.android.optimovemobilesdk.databinding.FragmentHomeBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements HomeAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Home");

        setMenu(root.getContext());

        return root;
    }

    private void setMenu(Context context) {
        final RecyclerView recyclerView = binding.recyclerView;

        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("QA Screen", R.drawable.baseline_dashboard_24, R.id.action_navigation_home_to_navigation_dashboard));
        items.add(new MenuItem("User Info", R.drawable.baseline_person_24, R.id.action_navigation_home_to_navigation_profile));
        items.add(new MenuItem("Geofencing", R.drawable.baseline_location_pin_24, R.id.action_navigation_home_to_navigation_location));
        items.add(new MenuItem("Preference Center", R.drawable.baseline_settings_24, R.id.action_navigation_home_to_navigation_preference_center));

        HomeAdapter adapter = new HomeAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(MenuItem item) {
        int actionId = item.getActionId();
        if (actionId != 0) {
            Navigation.findNavController(getView()).navigate(actionId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
