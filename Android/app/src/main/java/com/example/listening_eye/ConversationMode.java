package com.example.listening_eye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class ConversationMode extends AppCompatActivity {
    //private int requestCode = 101;
    //public static final String[] BLUETOOTH_PERMISSIONS_S = { Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT} ;


    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Button convoON, alertON;
    EditText editNameBox;
    BluetoothAdapter btAdapter;
    Intent enableBluetoothIntent;
    int REQUEST_ENABLE_BT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_mode);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.firstFragment, R.id.secondFragment, R.id.thirdFragment).build();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


//        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (btAdapter.isEnabled()) {
////            SharedPreferences prefs_btdev = getSharedPreferences("btdev", 0);
////            String btdevaddr=prefs_btdev.getString("btdevaddr","?");
//            String btdevaddr = "C0:D0:12:96:6E:3C";
//            if (btdevaddr != "?")
//            {
//                BluetoothDevice device = btAdapter.getRemoteDevice(btdevaddr);
//
//                UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // bluetooth serial port service
//                //UUID SERIAL_UUID = device.getUuids()[0].getUuid(); //if you don't know the UUID of the bluetooth device service, you can get it like this from android cache
//
//                BluetoothSocket socket = null;
//
//                try {
//                    socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
//                } catch (Exception e) {Log.e("","Error creating socket");}
//
//                try {
//                    socket.connect();
//                    Log.e("","Connected");
//                } catch (IOException e) {
//                    Log.e("",e.getMessage());
//                    try {
//                        Log.e("","trying fallback...");
//
//                        socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
//                        socket.connect();
//
//                        Log.e("","Connected");
//                    }
//                    catch (Exception e2) {
//                        Log.e("", "Couldn't establish Bluetooth connection!");
//                    }
//                }
//            }
//            else
//            {
//                Log.e("","BT device not selected");
//            }
//        }
//        BluetoothAdapter btAdapter;
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (!EasyPermissions.hasPermissions(this, BLUETOOTH_PERMISSIONS_S)) {
//                EasyPermissions.requestPermissions(this, "message", yourRequestCode,BLUETOOTH_PERMISSIONS_S);
//            }
//        }

//        if (!btAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, requestCode);
//        }

//        convoON = findViewById(R.id.convoOn);
//        alertON = findViewById(R.id.alertOn);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetoothIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
        REQUEST_ENABLE_BT = 1;


//        editNameBox = findViewById(R.id.editNameBox);
//        String nameToBePicked = editNameBox.getText().toString();
//        if (nameToBePicked != "") {
//            Intent intent = new Intent(ConversationMode.this, Bluetooth.class);
//            // String keyIdentifier = null;
//            intent.putExtra(Intent.EXTRA_TEXT, nameToBePicked);
//            startActivity(intent);
//        }
//        convoOnMethod();
//        alertOnMethod();


//        System.out.println(btAdapter.getBondedDevices());
//

//        BluetoothDevice hc05 = btAdapter.getRemoteDevice("C0:D0:12:96:6E:3C");
//        //BluetoothDevice hc05 = btAdapter.getRemoteDevice("00:21:07:34:D6:55");
//        System.out.println(hc05.getName());
//
//        BluetoothSocket btSocket = null;
//        int counter = 0;
//
//        do {
//            try {
//                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
//                System.out.println(btSocket);
//                btSocket.connect();
//                System.out.println(btSocket.isConnected());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            counter++;
//        } while (!btSocket.isConnected() && counter < 3);
//
//
//
//        try {
//            OutputStream outputStream = btSocket.getOutputStream();
//            outputStream.write(48);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        InputStream inputStream = null;
//        try {
//            inputStream = btSocket.getInputStream();
//            inputStream.skip(inputStream.available());
//            for (int i = 0; i < 26; i++) {
//                byte b = (byte) inputStream.read();
//                System.out.println((char) b);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        try {
//            btSocket.close();
//            System.out.println(btSocket.isConnected());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //TextView frag1_instru;
//        Button viewConversionButton;
//        Button viewTranslationButton;

        //frag1_instru = findViewById(R.id.frag1_instruction);
//        viewConversionButton = findViewById(R.id.view_conversion_history_button);
//        viewTranslationButton = (Button) findViewById(R.id.view_record_button);

//        viewConversionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //frag1_instru.setText("   Text converted from Google API");
//                Intent intent = new Intent(ConversationMode.this, voice2text.class);
//                startActivity(intent);
//
//            }
//        });

//        viewTranslationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ConversationMode.this, translation.class);
//                startActivity(intent);
//
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                System.out.println("BT ENABLED");
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("BT ENABLING CANCELLED");
            }
        }
    }

//    private void convoOnMethod() {
//        convoON.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                convoOnMSG = "!CONVO";
//                System.out.println(convoOnMSG);
//                Toast.makeText(getApplicationContext(), "Conversation Mode Turned On!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void alertOnMethod() {
//        alertON.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                convoOnMSG = "!ALERT";
//                System.out.println(convoOnMSG);
//                Toast.makeText(getApplicationContext(), "Alert Mode Turned On!", Toast.LENGTH_SHORT).show();
//                }
//        });
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
}