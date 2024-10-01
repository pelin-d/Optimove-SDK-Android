package com.optimove.android.optimovemobilesdk.ui.cart;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optimove.android.optimovemobilesdk.databinding.FragmentCartBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;
import com.optimove.android.optimovemobilesdk.ui.home.HomeViewModel;

import java.util.List;
import java.util.Objects;

public class CartFragment extends BaseFragment {

    private FragmentCartBinding binding;
    HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Cart");

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
