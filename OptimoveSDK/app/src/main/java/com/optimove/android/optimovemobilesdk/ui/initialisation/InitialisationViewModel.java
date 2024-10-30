package com.optimove.android.optimovemobilesdk.ui.initialisation;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InitialisationViewModel extends ViewModel {

    private MutableLiveData<InitialisationUiState> uiState;

    public MutableLiveData<InitialisationUiState> getUiState() {
        if (uiState == null) {
            uiState = new MutableLiveData<>(new InitialisationUiState(false));
        }
        return uiState;
    }

    public static class InitialisationUiState {

        private final boolean isInitialised;

        public InitialisationUiState(boolean isInitialised) {
            this.isInitialised = isInitialised;
        }

        public boolean isInitialised() {
            return isInitialised;
        }

    }

}
