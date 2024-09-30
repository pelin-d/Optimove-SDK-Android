package com.optimove.android.optimovemobilesdk.ui.cart;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CartViewModel extends ViewModel {

    private MutableLiveData<CartUiState> uiState;

    public MutableLiveData<CartUiState> getUiState() {
        if (uiState == null) {
            uiState = new MutableLiveData<>(new CartUiState(""));
        }
        return uiState;
    }

    public static class CartUiState {

        private final String itemName;

        public CartUiState(String itemName) {
            this.itemName = itemName;
        }

        public String getItemName() {
            return itemName;
        }

    }
}
