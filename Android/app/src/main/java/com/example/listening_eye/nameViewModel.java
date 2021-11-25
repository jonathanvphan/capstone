package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class nameViewModel extends ViewModel {
    private static final MutableLiveData<String> selectedName = new MutableLiveData<String>();
    public void setData(String name) {
        System.out.println("setData from viewmodel"+name);
        System.out.println("selectedName"+selectedName);
        selectedName.setValue(name);
    }

    public static LiveData<String> getSelectedName() {
        System.out.println("getSelectedName from viewmodel"+selectedName);
        return selectedName;
    }
}
