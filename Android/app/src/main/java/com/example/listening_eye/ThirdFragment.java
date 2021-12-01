package com.example.listening_eye;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {
//    nameViewModel viewModel;
//    trainViewModel viewModelTrain;
//    walkViewModel viewModelWalk;
//    fireViewModel viewModelFire;
//    warningViewModel viewModelWarning;
//    alertViewModel viewModelAlert;
//    dangerViewModel viewModelDanger;

    Button btConnectAlert, alertBTOff, alertOnButton;
    TextView status, msg_box;
    EditText writeMsg;

    BluetoothAdapter btAdapter;
    BluetoothDevice[] btArray;
    private BluetoothSocket socket;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private EditText editNameBox;
    private SwitchCompat trainSwitch;
    private SwitchCompat walkSwitch;
    private SwitchCompat fireSwitch;
    private SwitchCompat warningSwitch;
    private SwitchCompat alertSwitch;
    private SwitchCompat dangerSwitch;
    public static final String LAST_TEXT = "";
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
        //FloatingActionButton publicAnnounce = view.findViewById(R.id.public_announcement);
        FloatingActionButton pickName = view.findViewById(R.id.name_picked_up);
        //FloatingActionButton hazardous_edit = view.findViewById(R.id.hazardous_edit);
        //FloatingActionButton name_picked_up_edit = view.findViewById(R.id.name_picked_up_edit);
        trainSwitch = view.findViewById(R.id.trainSwitch);
        walkSwitch = view.findViewById(R.id.walkSwitch);
        fireSwitch = view.findViewById(R.id.fireSwitch);
        warningSwitch = view.findViewById(R.id.warningSwitch);
        alertSwitch = view.findViewById(R.id.alertSwitch);
        dangerSwitch = view.findViewById(R.id.dangerSwitch);
        editNameBox = view.findViewById(R.id.editNameBox);
        btConnectAlert = view.findViewById(R.id.btConnectAlert);
        alertBTOff = view.findViewById(R.id.alertBTOff);
        alertOnButton = view.findViewById(R.id.alertOnButton);

        trainSwitch.setVisibility(View.INVISIBLE);
        walkSwitch.setVisibility(View.INVISIBLE);
        fireSwitch.setVisibility(View.INVISIBLE);
        warningSwitch.setVisibility(View.INVISIBLE);
        alertSwitch.setVisibility(View.INVISIBLE);
        dangerSwitch.setVisibility(View.INVISIBLE);
        editNameBox.setVisibility(View.INVISIBLE);



        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();

        hazardous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainSwitch.setVisibility(View.VISIBLE);
                walkSwitch.setVisibility(View.VISIBLE);
                fireSwitch.setVisibility(View.VISIBLE);
                warningSwitch.setVisibility(View.VISIBLE);
                alertSwitch.setVisibility(View.VISIBLE);
                dangerSwitch.setVisibility(View.VISIBLE);
                Alerter.Companion.create(getActivity())
                        .setTitle("Hazardous Switch")
                        .setText("Turn On Switch to Have Hazardous Notifications")
                        .setIcon(R.drawable.ic_hazardous)
                        .setBackgroundColorRes(R.color.random)
                        .setDuration(4000)
                        .show();
            }
        });

        pickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameBox.setVisibility(View.VISIBLE);
                Alerter.Companion.create(getActivity())
                        .setTitle("Reset Name")
                        .setText("Enter to Reset Name to Be Detected")
                        .setIcon(R.drawable.ic_name_picked)
                        .setBackgroundColorRes(R.color.teal_200)
                        .setDuration(4000)
                        .show();
            }
        });
        return view;
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        viewModel = new ViewModelProvider(requireActivity()).get(nameViewModel.class);
//        viewModelTrain = new ViewModelProvider(requireActivity()).get(trainViewModel.class);
//        viewModelWalk = new ViewModelProvider(requireActivity()).get(walkViewModel.class);
//        viewModelFire = new ViewModelProvider(requireActivity()).get(fireViewModel.class);
//        viewModelWarning = new ViewModelProvider(requireActivity()).get(warningViewModel.class);
//        viewModelAlert = new ViewModelProvider(requireActivity()).get(alertViewModel.class);
//        viewModelDanger = new ViewModelProvider(requireActivity()).get(dangerViewModel.class);
        Button nameResetConfirm = view.findViewById(R.id.nameResetConfirm);
        nameResetConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameCommand = "!CHANGE" + editNameBox.getText().toString();
                sendReceive.write(nameCommand.getBytes());
                //viewModel.setData(editNameBox.getText().toString());
                System.out.println("from third fragment"+editNameBox.getText().toString());
            }
        });


        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editNameBox.setText(pref.getString(LAST_TEXT, ""));
        editNameBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.edit().putString(LAST_TEXT, s.toString()).commit();
            }
        });


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
                    //viewModelTrain.setData("!TRAINON");
                    String trainOnCommand = "!TRAINON";
                    sendReceive.write(trainOnCommand.getBytes());
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Train.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save1", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save1", false);
                    editor.apply();
                    trainSwitch.setChecked(false);
                    //viewModelTrain.setData("!TRAINOFF");
                    String trainOffCommand = "!TRAINOFF";
                    sendReceive.write(trainOffCommand.getBytes());
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
                    //viewModelWalk.setData("!WALKON");
                    String walkOnCommand = "!WALKON";
                    sendReceive.write(walkOnCommand.getBytes());
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Walk.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save2", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save2", false);
                    editor.apply();
                    walkSwitch.setChecked(false);
                    //viewModelWalk.setData("!WALKOFF");
                    String walkOffCommand = "!WALKOFF";
                    sendReceive.write(walkOffCommand.getBytes());
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
                    //viewModelFire.setData("!FIREON");
                    String fireOnCommand = "!FIREON";
                    sendReceive.write(fireOnCommand.getBytes());
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Fire Warning.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save3", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save3", false);
                    editor.apply();
                    fireSwitch.setChecked(false);
                    //viewModelFire.setData("!FIREOFF");
                    String fireOffCommand = "!FIREOFF";
                    sendReceive.write(fireOffCommand.getBytes());
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
                    //viewModelWarning.setData("!WARNINGON");
                    String warningOnCommand = "!WARNINGON";
                    sendReceive.write(warningOnCommand.getBytes());
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Warning.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save4", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save4", false);
                    editor.apply();
                    warningSwitch.setChecked(false);
                    //viewModelWarning.setData("!WARNINGOFF");
                    String warningOffCommand = "!WARNINGOFF";
                    sendReceive.write(warningOffCommand.getBytes());
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
                    //viewModelAlert.setData("!ALERTON");
                    String alertOnCommand = "!ALERTON";
                    sendReceive.write(alertOnCommand.getBytes());
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Alert.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save5", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save5", false);
                    editor.apply();
                    alertSwitch.setChecked(false);
                    //viewModelAlert.setData("!ALERTOFF");
                    String alertOffCommand = "!ALERTOFF";
                    sendReceive.write(alertOffCommand.getBytes());
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
                    //viewModelDanger.setData("!DANGERON");
                    String dangerOnCommand = "!DANGERON";
                    sendReceive.write(dangerOnCommand.getBytes());
                } else {
                    Toast.makeText(getContext(), "Alert Mode Turned Off For Danger.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("save6", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("save6", false);
                    editor.apply();
                    dangerSwitch.setChecked(false);
                    //viewModelDanger.setData("!DANGEROFF");
                    String dangerOffCommand = "!DANGEROFF";
                    sendReceive.write(dangerOffCommand.getBytes());
                }
            }
        });
    }

    private void implementListeners() {
        btConnectAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<BluetoothDevice> bt = btAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strings);
                    //listView.setAdapter(arrayAdapter);
                }

                ServerClass serverClass = new ServerClass();
                serverClass.start();

                //"00:21:07:34:D8:E2"
                BluetoothDevice device = btAdapter.getRemoteDevice("9C:B6:D0:0C:F5:A2");
                ClientClass clientClass = new ClientClass(device);
                clientClass.start();
                System.out.println("this is the device connected" + device);

                //status.setText("Connecting");
            }
        });

        alertOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Turning On Alert Mode", Toast.LENGTH_SHORT);
                String string = "!ALERTMODE";
                sendReceive.write(string.getBytes());
            }
        });

        alertBTOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    //status.setText("Listening");
                    System.out.println("Listening");
                    break;
                case STATE_CONNECTING:
                    //status.setText("Connecting");
                    System.out.println("Connecting");
                    break;
                case STATE_CONNECTED:
                    //status.setText("Connected");
                    System.out.println("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    //status.setText("Connection Failed");
                    System.out.println("Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    //msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = btAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;

        public ClientClass(BluetoothDevice device1) {
            device = device1;

            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket btSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            btSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = btSocket.getInputStream();
                tempOut = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}