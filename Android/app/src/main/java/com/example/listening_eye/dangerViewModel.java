package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class dangerViewModel extends ViewModel {
    private static final MutableLiveData<String> selectedDanger = new MutableLiveData<String>();
    public void setData(String name) {
        selectedDanger.setValue(name);
    }

    public static LiveData<String> getSelectedDanger() {
        System.out.println("getSelectedDanger from viewmodel"+selectedDanger);
        return selectedDanger;
    }
}
