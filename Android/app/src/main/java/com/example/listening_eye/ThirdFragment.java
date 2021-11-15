package com.example.listening_eye;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

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


    boolean clicked = false;

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
        Switch earthquakeSwitch = view.findViewById(R.id.earthquakeSwitch);
        Switch floodSwitch = view.findViewById(R.id.floodSwitch);
        Switch snowstormSwitch = view.findViewById(R.id.snowstormSwitch);
        EditText editNameBox = view.findViewById(R.id.editNameBox);
        earthquakeSwitch.setVisibility(View.INVISIBLE);
        floodSwitch.setVisibility(View.INVISIBLE);
        snowstormSwitch.setVisibility(View.INVISIBLE);
        editNameBox.setVisibility(View.INVISIBLE);

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
                earthquakeSwitch.setVisibility(View.VISIBLE);
                floodSwitch.setVisibility(View.VISIBLE);
                snowstormSwitch.setVisibility(View.VISIBLE);
            }
        });
        earthquakeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Alert Mode Turned On For Earthquake.", Toast.LENGTH_SHORT).show();
            }
        });
        floodSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Alert Mode Turned On For Flood.", Toast.LENGTH_SHORT).show();
            }
        });
        snowstormSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Alert Mode Turned On For Snowstorm.", Toast.LENGTH_SHORT).show();
            }
        });
//        Button hazardous = view.findViewById(R.id.hazardous);
//        hazardous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        return view;
    }

//    private void onHazardousclicked() {
//        setVisibility(clicked);
//        setAnimation(clicked);
//    }
//
//    private void setAnimation(boolean clicked) {
//        Button hazardous =  findViewById(R.id.hazardous);
//        if(clicked) {
//
//        }
//    }
//
//    private void setVisibility(boolean clicked) {
//    }

}