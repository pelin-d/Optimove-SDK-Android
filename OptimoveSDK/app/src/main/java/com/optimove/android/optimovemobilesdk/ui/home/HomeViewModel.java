package com.optimove.android.optimovemobilesdk.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<HomeUiState> uiState;

    public MutableLiveData<HomeUiState> getUiState() {
        if (uiState == null) {
            uiState = new MutableLiveData<>(new HomeUiState("", new ArrayList<>()));
        }
        return uiState;
    }

    public static class HomeUiState {

        private final String lastItemName;
        private final List<String> itemList;

        public HomeUiState(String lastItemName, List<String> itemList) {
            this.lastItemName = lastItemName;
            this.itemList = itemList;
        }

        public String getLastItemName() {
            return lastItemName;
        }

        public List<String> getItemList() {
            return itemList;
        }

    }
}
