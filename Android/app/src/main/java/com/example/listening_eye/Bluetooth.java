package com.example.listening_eye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Bluetooth extends AppCompatActivity {

    private static final String FILE_NAME = "audioText.txt";
    private String nameToBePicked;

    private nameViewModel viewModel;
    private trainViewModel viewModelTrain;
    private walkViewModel viewModelWalk;
    private fireViewModel viewModelFire;
    private warningViewModel viewModelWarning;
    private alertViewModel viewModelAlert;
    private dangerViewModel viewModelDanger;

    TextView testNameChange, testSwitchCommand;

    Button listen, listDevices, send, font1, font2, font3, convoOn, alertOn, testNavi;
    ListView listView;
    TextView status, msg_box;
    EditText writeMsg;

    BluetoothAdapter btAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        findViewByIdes();

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();

        testNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Bluetooth.this, ConversationMode.class);
                // getApplicationContext()
                startActivity(intent);
            }
        });

        //ViewModel logic
        viewModel = new ViewModelProvider(this).get(nameViewModel.class);
        viewModel.getSelectedName().observe(this, item -> {
            System.out.println("from bluetooth.java" + item);
            String nameMsg = "!CHANGE" + item;
            //sendReceive.write(nameMsg.getBytes());
            System.out.println(nameMsg);
            testNameChange.setText(nameMsg);
        });
        viewModelTrain = new ViewModelProvider(this).get(trainViewModel.class);
        viewModelTrain.getSelectedTrain().observe(this, train -> {
            //sendReceive.write(train.getBytes());
            testSwitchCommand.setText(train);
        });
        viewModelWalk = new ViewModelProvider(this).get(walkViewModel.class);
        viewModelWalk.getSelectedWalk().observe(this, walk -> {
            sendReceive.write(walk.getBytes());
            //testSwitchCommand.setText(walk);
        });
        viewModelFire = new ViewModelProvider(this).get(fireViewModel.class);
        viewModelFire.getSelectedFire().observe(this, fire -> {
            sendReceive.write(fire.getBytes());
            //testSwitchCommand.setText(fire);
        });
        viewModelWarning = new ViewModelProvider(this).get(warningViewModel.class);
        viewModelWarning.getSelectedWarning().observe(this, warning -> {
            sendReceive.write(warning.getBytes());
            //testSwitchCommand.setText(warning);
        });
        viewModelAlert = new ViewModelProvider(this).get(alertViewModel.class);
        viewModelAlert.getSelectedAlert().observe(this, alert -> {
            sendReceive.write(alert.getBytes());
            //testSwitchCommand.setText(alert);
        });
        viewModelDanger = new ViewModelProvider(this).get(dangerViewModel.class);
        viewModelDanger.getSelectedDanger().observe(this, danger -> {
            sendReceive.write(danger.getBytes());
            //testSwitchCommand.setText(danger);
        });
    }


    private void implementListeners() {
        listDevices.setOnClickListener(new View.OnClickListener() {
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

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientClass clientClass = new ClientClass(btArray[position]);
                clientClass.start();
                System.out.println("this is the device connected" + btArray[position]);

                status.setText("Connecting");
            }
        });

        font1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Changing to Font 1", Toast.LENGTH_SHORT);
                String string = "FONT1";
                sendReceive.write(string.getBytes());
            }
        });

        font2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Changing to Font 2", Toast.LENGTH_SHORT);
                String string = "FONT2";
                sendReceive.write(string.getBytes());
            }
        });

        font3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Changing to Font 3", Toast.LENGTH_SHORT);
                String string = "FONT3";
                sendReceive.write(string.getBytes());
            }
        });

        convoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Turning On Conversation Mode", Toast.LENGTH_SHORT);
                String string = "!CONVO";
                sendReceive.write(string.getBytes());
            }
        });

        alertOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Turning On Alert Mode", Toast.LENGTH_SHORT);
                String string = "!ALERT";
                sendReceive.write(string.getBytes());
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = String.valueOf(writeMsg.getText());
                sendReceive.write(string.getBytes());
            }
        });


    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    private void findViewByIdes() {
        listen = findViewById(R.id.listenButton);
        listDevices = findViewById(R.id.listDeviceButton);
        send = findViewById(R.id.sendButton);
        listView = findViewById(R.id.listview);
        status = findViewById(R.id.status);
        msg_box = findViewById(R.id.messageDisplay);
        writeMsg = findViewById(R.id.messageWrite);
        //writeMsg.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(writeMsg, InputMethodManager.SHOW_IMPLICIT);
        font1 = findViewById(R.id.font1);
        font2 = findViewById(R.id.font2);
        font3 = findViewById(R.id.font3);
        convoOn = findViewById(R.id.convoOn);
        alertOn = findViewById(R.id.alertOn);
        testNameChange = findViewById(R.id.testNameChange);
        testSwitchCommand = findViewById(R.id.testSwitchCommand);
        testNavi = findViewById(R.id.testNavi);
    }

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
        private BluetoothSocket socket;

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