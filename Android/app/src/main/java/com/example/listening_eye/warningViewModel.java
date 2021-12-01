package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class warningViewModel extends ViewModel {
    private static final MutableLiveData<String> selectedWarning = new MutableLiveData<String>();
    public void setData(String name) {
        selectedWarning.setValue(name);
    }

    public static LiveData<String> getSelectedWarning() {
        System.out.println("getSelectedWarning from viewmodel"+selectedWarning);
        return selectedWarning;
    }
}
