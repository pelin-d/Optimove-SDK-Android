package com.optimove.android.optimovemobilesdk.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.EventReport;
import com.optimove.android.optimovemobilesdk.SimpleCustomEvent;
import com.optimove.android.optimovemobilesdk.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final MaterialButton addItemButton = binding.addItemButton;
        final ImageButton cartButton = binding.cartButton;
        final TextInputEditText editText = binding.textInputEditText;

        addItemButton.setOnClickListener(v -> onAddItem(v, String.valueOf(editText.getText())));
        cartButton.setOnClickListener(this::reportCartVisit);

        homeViewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            addItemToCart(uiState.getItemName());
        });

        return root;
    }

    public void addItemToCart(String itemName) {

    }

    // TODO make custom event
    public void onAddItem(View v, String itemName) {
        homeViewModel.getUiState().setValue(new HomeViewModel.HomeUiState(itemName));
        reportEvent(v);
    }

    // TODO make custom event
    public void reportCartVisit(View v) {
        reportEvent(v);
    }

    public void reportEvent(View view) {
        if (view == null) return;
        showMessage("Reporting Custom Event for Visitor without optional value");
        EventReport.runFromWorker(() -> Optimove.getInstance().reportEvent(new SimpleCustomEvent()));
        EventReport.runFromWorker(() -> Optimove.getInstance().reportEvent("Event_No ParaMs     "));
    }

    public void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
