package com.optimove.android.optimovemobilesdk.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<HomeUiState> uiState;

    public MutableLiveData<HomeUiState> getUiState() {
        if (uiState == null) {
            uiState = new MutableLiveData<>(new HomeUiState(""));
        }
        return uiState;
    }

    public static class HomeUiState {

        private final String itemName;

        public HomeUiState(String itemName) {
            this.itemName = itemName;
        }

        public String getItemName() {
            return itemName;
        }

    }
}
