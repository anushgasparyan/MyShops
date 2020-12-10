package com.example.myshops.ui.forget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForgetViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ForgetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("CHANGE PASSWORD WITH EMAIL");
    }

    public LiveData<String> getText() {
        return mText;
    }
}