package com.optimove.android.optimovemobilesdk.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.optimove.android.optimovemobilesdk.R;
import com.optimove.android.optimovemobilesdk.databinding.FragmentHomeBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Home");

        final MaterialButton addItemButton = binding.addItemButton;
        final ImageButton cartButton = binding.cartButton;
        final TextInputEditText editText = binding.textInputEditText;

        addItemButton.setOnClickListener(v -> onAddItem(v, String.valueOf(editText.getText())));
        cartButton.setOnClickListener(this::visitCart);

//        homeViewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
//
//        });

        return root;
    }

    public void onAddItem(View v, String itemName) {
        List<String> itemList = Objects.requireNonNull(homeViewModel.getUiState().getValue()).getItemList();
        itemList.add(itemName);

        homeViewModel.getUiState().setValue(new HomeViewModel.HomeUiState(itemName, itemList));

        // TODO make custom event
        reportEvent(v);
    }

    public void visitCart(View v) {
        Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_navigation_cart);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
