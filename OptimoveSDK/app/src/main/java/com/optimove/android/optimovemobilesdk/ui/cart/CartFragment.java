package com.optimove.android.optimovemobilesdk.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.EventReport;
import com.optimove.android.optimovemobilesdk.SimpleCustomEvent;
import com.optimove.android.optimovemobilesdk.databinding.FragmentCartBinding;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    CartViewModel cartViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
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
