package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class alertViewModel extends ViewModel {
    private static final MutableLiveData<String> selectedAlert = new MutableLiveData<String>();
    public void setData(String name) {
        selectedAlert.setValue(name);
    }

    public static LiveData<String> getSelectedAlert() {
        System.out.println("getSelectedAlert from viewmodel"+selectedAlert);
        return selectedAlert;
    }
}
