package com.example.listening_eye;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class translationViewModel extends ViewModel {
    private static final MutableLiveData<ArrayList<String>> selectedName = new MutableLiveData<ArrayList<String>>();
    public void setData(ArrayList<String> name) {
        System.out.println("setData from viewmodel"+name);
        System.out.println("selectedName"+selectedName);
        selectedName.setValue(name);
    }

    public static LiveData<ArrayList<String>> getSelectedName() {
        System.out.println("getSelectedName from viewmodel"+selectedName);
        return selectedName;
    }
}
