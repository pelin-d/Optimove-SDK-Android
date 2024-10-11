package com.optimove.android.optimovemobilesdk.ui.inbox;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optimove.android.optimobile.InAppInboxItem;

import java.util.ArrayList;
import java.util.List;

public class InboxViewModel extends ViewModel {

    private MutableLiveData<InboxUiState> uiState;

    public MutableLiveData<InboxUiState> getUiState() {
        if (uiState == null) {
            uiState = new MutableLiveData<>(new InboxUiState(new ArrayList<>()));
        }
        return uiState;
    }

    public static class InboxUiState {

        private final List<InAppInboxItem> itemList;

        public InboxUiState(List<InAppInboxItem> itemList) {
            this.itemList = itemList;
        }

        public List<InAppInboxItem> getItemList() {
            return itemList;
        }

    }

}
