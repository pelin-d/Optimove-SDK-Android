package com.optimove.android.optimovemobilesdk.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optimove.android.optimobile.InAppInboxItem;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<HomeUiState> uiState;

    public MutableLiveData<HomeUiState> getUiState() {
        if (uiState == null) {
            uiState = new MutableLiveData<>(new HomeUiState(new ArrayList<>()));
        }
        return uiState;
    }

    public static class HomeUiState {

        private final List<MenuItem> itemList;

        public HomeUiState(List<MenuItem> itemList) {
            this.itemList = itemList;
        }

        public List<MenuItem> getItemList() {
            return itemList;
        }

    }
}
