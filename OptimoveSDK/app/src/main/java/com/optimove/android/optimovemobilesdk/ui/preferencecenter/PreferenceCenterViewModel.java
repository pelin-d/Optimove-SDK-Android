package com.optimove.android.optimovemobilesdk.ui.preferencecenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PreferenceCenterViewModel extends ViewModel {

    private MutableLiveData<Void> standardInitEvent = new MutableLiveData<>();
    private MutableLiveData<Void> delayedInitEvent = new MutableLiveData<>();

    private final MutableLiveData<String> text = new MutableLiveData<>();

    public LiveData<Void> getStandardInitEvent() {
        return standardInitEvent;
    }

    public void onStandardInitClick() {
        standardInitEvent.setValue(null);
    }

    public LiveData<Void> getDelayedInitEvent() {
        return delayedInitEvent;
    }

    public void onDelayedInitClick() {
        delayedInitEvent.setValue(null);
    }

    public LiveData<String> getText() {
        return text;
    }

    public void onTextChanged(String message) {
        text.setValue(message);
    }


}
