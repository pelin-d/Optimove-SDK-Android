package com.optimove.android.optimovemobilesdk.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mUserId;
    private final MutableLiveData<String> mUserEmail;
    private final MutableLiveData<String> mText;

    public ProfileViewModel() {
        mUserId = new MutableLiveData<>();
        mUserEmail = new MutableLiveData<>();
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId.setValue(userId);
    }

    public LiveData<String> getUserEmail() {
        return mUserEmail;
    }

    public void setUserEmail(String userEmail) {
        mUserEmail.setValue(userEmail);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String text) {
        mText.setValue(text);
    }
}