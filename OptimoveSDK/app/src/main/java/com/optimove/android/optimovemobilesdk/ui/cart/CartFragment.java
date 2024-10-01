package com.optimove.android.optimovemobilesdk.ui.cart;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.EventReport;
import com.optimove.android.optimovemobilesdk.SimpleCustomEvent;
import com.optimove.android.optimovemobilesdk.databinding.FragmentCartBinding;
import com.optimove.android.optimovemobilesdk.ui.home.HomeViewModel;

import java.util.List;
import java.util.Objects;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setRecyclerView(root.getContext());

        return root;
    }

    private void setRecyclerView(Context context) {
        final RecyclerView recyclerView = binding.recyclerView;

        List<String> itemList = Objects.requireNonNull(viewModel.getUiState().getValue()).getItemList();

        CartAdapter adapter = new CartAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO make custom event for visiting the cart page
        reportEvent(getView());
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
