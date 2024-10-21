package com.optimove.android.optimovemobilesdk.ui.inbox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optimove.android.optimobile.InAppInboxItem;
import com.optimove.android.optimobile.OptimoveInApp;
import com.optimove.android.optimovemobilesdk.constants.Constants;
import com.optimove.android.optimovemobilesdk.databinding.FragmentInboxBinding;
import com.optimove.android.optimovemobilesdk.BaseFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class InboxFragment extends BaseFragment implements InboxAdapter.OnItemClickListener {

    private FragmentInboxBinding binding;
    private InboxViewModel inboxViewModel;

    static final String TAG = Constants.TAG;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        inboxViewModel = new ViewModelProvider(this).get(InboxViewModel.class);

        binding = FragmentInboxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Inbox");

        getInboxItems();

        ImageButton deleteInboxItemsButton = binding.deleteInboxItemsButton;
        ImageButton markInboxItemsReadButton = binding.markInboxItemsReadButton;

        deleteInboxItemsButton.setOnClickListener(this::deleteAllMessages);
        markInboxItemsReadButton.setOnClickListener(this::markAllAsRead);

        setRecyclerView(root.getContext());

        inboxViewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            setRecyclerView(root.getContext());
        });

        return root;
    }

    private void getInboxItems() {
        List<InAppInboxItem> items = OptimoveInApp.getInstance().getInboxItems();

        if (isNullOrEmpty(items)) {
            clearUiState();
            setEmptyInboxLayout();
            Log.d(TAG, "No items in inbox");
        } else {
            inboxViewModel.getUiState().setValue(new InboxViewModel.InboxUiState(items));
            Log.d(TAG, "Fetched " + items.size() + " inbox items");
        }
    }

    private void setRecyclerView(Context context) {
        final RecyclerView recyclerView = binding.recyclerView;
        List<InAppInboxItem> items = Objects.requireNonNull(inboxViewModel.getUiState().getValue()).getItemList();
        InboxAdapter adapter = new InboxAdapter(items, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMarkAsRead(InAppInboxItem item) {
        OptimoveInApp.getInstance().markAsRead(item);
        Log.d(TAG, "Mark inbox item with title " + item.getTitle() + " as read");
        getInboxItems();
    }

    @Override
    public void onDelete(InAppInboxItem item) {
        OptimoveInApp.getInstance().deleteMessageFromInbox(item);
        Log.d(TAG, "Delete inbox item with title " + item.getTitle());
        getInboxItems();
    }

    private void setEmptyInboxLayout() {

    }

    private  void clearUiState() {
        inboxViewModel.getUiState().setValue(new InboxViewModel.InboxUiState(new ArrayList<>()));
    }

    private void markAllAsRead(View v) {
        OptimoveInApp.getInstance().markAllInboxItemsAsRead();
        Log.d(TAG, "Mark every inbox item as read");
        getInboxItems();
    }

    private void deleteAllMessages(View view) {
        List<InAppInboxItem> items = Objects.requireNonNull(inboxViewModel.getUiState().getValue()).getItemList();
        if (isNullOrEmpty(items)) {
            Log.d(TAG, "No items in inbox to delete");
            return;
        }
        for (InAppInboxItem item : items) {
            OptimoveInApp.getInstance().deleteMessageFromInbox(item);
        }
        Log.d(TAG, "Delete every inbox item");
        getInboxItems();
    }

    private static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
