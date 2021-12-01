package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class trainViewModel extends ViewModel {
    private static final MutableLiveData<String> selectedTrain = new MutableLiveData<String>();
    public void setData(String name) {
        selectedTrain.setValue(name);
        System.out.println(name);
    }

    public static LiveData<String> getSelectedTrain() {
        System.out.println("getSelectedTrain from viewmodel"+selectedTrain);
        return selectedTrain;
    }
}
