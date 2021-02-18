package com.example.myshops.ui.myproductsshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyProductsShowViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyProductsShowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("ADD A NEW PRODUCT");
    }

    public LiveData<String> getText() {
        return mText;
    }
}