package com.optimove.android.optimovemobilesdk.ui.inbox;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.optimove.android.optimobile.InAppInboxItem;
import com.optimove.android.optimobile.OptimoveInApp;
import com.optimove.android.optimovemobilesdk.databinding.FragmentInboxBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

import java.util.List;

public class InboxFragment extends BaseFragment {

    private FragmentInboxBinding binding;

    static final String TAG = "TestAppMainActvity";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InboxViewModel inboxViewModel =
                new ViewModelProvider(this).get(InboxViewModel.class);

        binding = FragmentInboxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Inbox");

        ImageButton deleteInboxItemsButton = binding.deleteInboxItemsButton;
        ImageButton markInboxItemsReadButton = binding.markInboxItemsReadButton;

        deleteInboxItemsButton.setOnClickListener(this::deleteInbox);
        markInboxItemsReadButton.setOnClickListener(this::markInboxAsRead);

        return root;
    }

    public void readInbox(View view) {
        List<InAppInboxItem> items = OptimoveInApp.getInstance().getInboxItems();
        if (items.size() == 0) {
            Log.d(TAG, "no inbox items!");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            InAppInboxItem item = items.get(i);
            Log.d(TAG, "title: " + item.getTitle() + ", isRead: " + item.isRead());
        }
    }

    public void markInboxAsRead(View view) {
        Log.d(TAG, "mark  all inbox read");
        OptimoveInApp.getInstance().markAllInboxItemsAsRead();
    }

    public void deleteInbox(View view) {
        List<InAppInboxItem> items = OptimoveInApp.getInstance().getInboxItems();
        if (items.size() == 0) {
            Log.d(TAG, "no inbox items!");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            OptimoveInApp.getInstance().deleteMessageFromInbox(items.get(i));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
