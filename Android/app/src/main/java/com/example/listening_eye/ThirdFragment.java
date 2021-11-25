package com.example.listening_eye;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tapadoo.alerter.Alerter;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {
    nameViewModel viewModel;

    private EditText editNameBox;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        FloatingActionButton hazardous = view.findViewById(R.id.hazardous);
        FloatingActionButton publicAnnounce = view.findViewById(R.id.public_announcement);
        FloatingActionButton pickName = view.findViewById(R.id.name_picked_up);
        FloatingActionButton hazardous_edit = view.findViewById(R.id.hazardous_edit);
        FloatingActionButton name_picked_up_edit = view.findViewById(R.id.name_picked_up_edit);
        SwitchCompat trainSwitch = view.findViewById(R.id.trainSwitch);
        SwitchCompat walkSwitch = view.findViewById(R.id.walkSwitch);
        SwitchCompat fireSwitch = view.findViewById(R.id.fireSwitch);
        SwitchCompat warningSwitch = view.findViewById(R.id.warningSwitch);
        SwitchCompat alertSwitch = view.findViewById(R.id.alertSwitch);
        SwitchCompat dangerSwitch = view.findViewById(R.id.dangerSwitch);
        editNameBox = view.findViewById(R.id.editNameBox);

        trainSwitch.setVisibility(View.INVISIBLE);
        walkSwitch.setVisibility(View.INVISIBLE);
        fireSwitch.setVisibility(View.INVISIBLE);
        warningSwitch.setVisibility(View.INVISIBLE);
        alertSwitch.setVisibility(View.INVISIBLE);
        dangerSwitch.setVisibility(View.INVISIBLE);
        editNameBox.setVisibility(View.INVISIBLE);


        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("save1", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("save2", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences("save3", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences4 = getActivity().getSharedPreferences("save4", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences5 = getActivity().getSharedPreferences("save5", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences6 = getActivity().getSharedPreferences("save6", Context.MODE_PRIVATE);
        trainSwitch.setChecked(sharedPreferences1.getBoolean("save1", false));
        trainSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trainSwitch.isChecked()) {
                    Toast.makeText(getContext(), "Alert Mode Turned On For Train.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save1", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save1", true);
                    editor.apply();
                    trainSwitch.setChecked(true);
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Train.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save1", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save1", false);
                    editor.apply();
                    trainSwitch.setChecked(false);
                }
            }
        });
        walkSwitch.setChecked(sharedPreferences2.getBoolean("save2", false));
        walkSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walkSwitch.isChecked()) {
                    Toast.makeText(getContext(), "Alert Mode Turned On For Walk.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save2", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save2", true);
                    editor.apply();
                    walkSwitch.setChecked(true);
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Walk.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save2", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save2", false);
                    editor.apply();
                    walkSwitch.setChecked(false);
                }
            }
        });
        fireSwitch.setChecked(sharedPreferences3.getBoolean("save3", false));
        fireSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fireSwitch.isChecked()) {
                    Toast.makeText(getContext(), "Alert Mode Turned On For Fire Warning.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save3", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save3", true);
                    editor.apply();
                    fireSwitch.setChecked(true);
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Fire Warning.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save3", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save3", false);
                    editor.apply();
                    fireSwitch.setChecked(false);
                }
            }
        });
        warningSwitch.setChecked(sharedPreferences4.getBoolean("save4", false));
        warningSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (warningSwitch.isChecked()) {
                    Toast.makeText(getContext(), "Alert Mode Turned On For Warning.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save4", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save4", true);
                    editor.apply();
                    warningSwitch.setChecked(true);
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Warning.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save4", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save4", false);
                    editor.apply();
                    warningSwitch.setChecked(false);
                }
            }
        });
        alertSwitch.setChecked(sharedPreferences5.getBoolean("save5", false));
        alertSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertSwitch.isChecked()) {
                    Toast.makeText(getContext(), "Alert Mode Turned On For Alert.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save5", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save5", true);
                    editor.apply();
                    alertSwitch.setChecked(true);
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Alert.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save5", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save5", false);
                    editor.apply();
                    alertSwitch.setChecked(false);
                }
            }
        });
        dangerSwitch.setChecked(sharedPreferences6.getBoolean("save6", false));
        dangerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dangerSwitch.isChecked()) {
                    Toast.makeText(getContext(), "Alert Mode Turned On For Danger.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save6", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save6", true);
                    editor.apply();
                    dangerSwitch.setChecked(true);
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Danger.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save6", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save6", false);
                    editor.apply();
                    dangerSwitch.setChecked(false);
                }
            }
        });


        hazardous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerter.Companion.create(getActivity())
                        .setTitle("Title")
                        .setText("this is description for hazardous situations")
                        .setIcon(R.drawable.ic_hazardous)
                        .setBackgroundColorRes(R.color.random)
                        .setDuration(4000)
                        .show();
            }
        });

        publicAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerter.Companion.create(getActivity())
                        .setTitle("Title")
                        .setText("this is description for public announcement")
                        .setIcon(R.drawable.ic_announce)
                        .setBackgroundColorRes(R.color.teal_200)
                        .setDuration(4000)
                        .show();
            }
        });

        pickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerter.Companion.create(getActivity())
                        .setTitle("Title")
                        .setText("this is description for picking up name from surrounding environment")
                        .setIcon(R.drawable.ic_name_picked)
                        .setBackgroundColorRes(R.color.teal_200)
                        .setDuration(4000)
                        .show();
            }
        });

        name_picked_up_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameBox.setVisibility(View.VISIBLE);
            }
        });
        hazardous_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainSwitch.setVisibility(View.VISIBLE);
                walkSwitch.setVisibility(View.VISIBLE);
                fireSwitch.setVisibility(View.VISIBLE);
                warningSwitch.setVisibility(View.VISIBLE);
                alertSwitch.setVisibility(View.VISIBLE);
                dangerSwitch.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(nameViewModel.class);
        Button nameResetConfirm = view.findViewById(R.id.nameResetConfirm);
        nameResetConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setData(editNameBox.getText().toString());
                System.out.println("from third fragment"+editNameBox.getText().toString());
            }
        });


    }

}