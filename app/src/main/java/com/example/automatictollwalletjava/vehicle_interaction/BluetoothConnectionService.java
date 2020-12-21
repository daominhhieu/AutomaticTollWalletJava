package com.example.automatictollwalletjava.vehicle_interaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {

    private  static final String TAG = "BluetoothConnectionServ";
    private static final String appname = "vehicle communication";

    private static final UUID MY_UUID_INSECURE = UUID.randomUUID();

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private ConnectThread mConnectThread;
    private AcceptThread mInsecureAcceptThread;
    private BluetoothDevice mVehicle;
    private UUID VehicleUUID;

    private ConnectedThread mConnectedThread;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread{
        //The local server socket
        private final BluetoothServerSocket mServerSocket;


        private AcceptThread() {
            BluetoothServerSocket tmp = null;

            //Create na new listening server socket
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appname, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }catch (IOException e){
                Log.d(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            mServerSocket = tmp;
        }

        public void run()
        {
            Log.d(TAG, "run: AcceptThread Running");
            BluetoothSocket mSocket = null;


            try {
                Log.d(TAG, "run: RFCOM server socket start...");
                mSocket = mServerSocket.accept();
                Log.d(TAG, "run: RFCOM server socket accepted connection");
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            if(mSocket != null)
            {
                connected(mSocket, mVehicle);
            }
            Log.d(TAG, "END mAcceptedThread ");

        }

        public void connected(BluetoothSocket loc_socket, BluetoothDevice loc_vehicle)
        {

        }

        public void cancel()
        {
            Log.d(TAG, "cancel: Canceling AcceptThread");
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: IOException: " + e.getMessage());
            }
        }
    }

    private class ConnectThread extends Thread
    {
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice loc_vehicle, UUID loc_uuid) {
            mVehicle = loc_vehicle;
            VehicleUUID = loc_uuid;
        }

        public void run()
        {
            Log.d(TAG, "run: ConnectThread Running");
            BluetoothSocket tmp = null;
            try {
                tmp = mVehicle.createInsecureRfcommSocketToServiceRecord(VehicleUUID);
            } catch (IOException e) {
                Log.d(TAG, "ConnectThread: IOException: " + e.getMessage());
            }
            mSocket = tmp;

            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
                Log.d(TAG, "run: ConnectThread Connected");
            } catch (IOException e) {
                try {
                    mSocket.close();
                    Log.d(TAG, "run: ConnectThread Closed");
                } catch (IOException ioException) {
                    Log.d(TAG, "ConnectThread: IOException: " + e.getMessage());
                }
                Log.d(TAG, "ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE);
            }

            connected(mSocket, mVehicle);
        }

        public void cancel()
        {
            Log.d(TAG, "cancel: Canceling AcceptThread");
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "ConnectThread: cancel connection: IOException: " + e.getMessage());
            }
        }
    }

    public synchronized void start ()
    {
        Log.d(TAG, "start");
        if(mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mInsecureAcceptThread == null)
        {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice loc_vehicle, UUID loc_uuid)
    {
        mConnectThread = new ConnectThread(loc_vehicle, loc_uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        private ConnectedThread(BluetoothSocket Socket) {
            mSocket = Socket;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                tmpInputStream = mSocket.getInputStream();
                tmpOutputStream = mSocket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, "ConnectedThread: initiate not complete: IOException: " + e.getMessage());
            }

            mInputStream = tmpInputStream;
            mOutputStream = tmpOutputStream;
        }

        public void run()
        {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mInputStream.read(buffer);
                    String incomingMess = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    Log.d(TAG, "Read input stream error: IOException:" + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes)
        {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to output stream: "+ text);
            try {
                mOutputStream.write(bytes);
            } catch (IOException e) {
                Log.d(TAG, "write error to output stream: IOException: " + e.getMessage());
            }
        }

        public void cancel()
        {
            Log.d(TAG, "cancel: Canceling AcceptThread");
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "ConnectedThread: cancel connection: IOException: " + e.getMessage());
            }
        }
    }

    private void connected (BluetoothSocket loc_socket, BluetoothDevice loc_vehicle)
    {
        Log.d(TAG, "Connected method starting...");
        mConnectedThread = new ConnectedThread(loc_socket);
        mConnectedThread.start();
    }

    public void write(byte[] bytes)
    {
        mConnectedThread.write(bytes);
    }

}
