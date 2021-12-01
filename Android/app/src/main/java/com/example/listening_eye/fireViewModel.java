package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class fireViewModel extends ViewModel {
    private static final MutableLiveData<String> selectedFire = new MutableLiveData<String>();
    public void setData(String name) {
        selectedFire.setValue(name);
    }

    public static LiveData<String> getSelectedFire() {
        System.out.println("getSelectedFire from viewmodel"+selectedFire);
        return selectedFire;
    }
}
