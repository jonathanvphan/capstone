package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class walkViewModel extends ViewModel {
    private static final MutableLiveData<String> selectedWalk = new MutableLiveData<String>();
    public void setData(String name) {
        selectedWalk.setValue(name);
    }

    public static LiveData<String> getSelectedWalk() {
        System.out.println("getSelectedWalk from viewmodel"+selectedWalk);
        return selectedWalk;
    }
}
