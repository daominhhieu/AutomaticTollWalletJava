package com.example.automatictollwalletjava.MyUtilities;


import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.macasaet.fernet.StringValidator;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.Validator;
import com.macasaet.fernet.Key;

import org.apache.commons.lang3.mutable.Mutable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.regex.Pattern;
import static android.content.ContentValues.TAG;

public class MySocketHandler {

    final String SERVER_IP = "192.168.1.17";
    final int SERVER_PORT = 2312;
    private PrintWriter output = null;
    private BufferedReader input = null;
    public Thread myMainThread;
    private Thread myListenThread;

    private boolean SocketListenStatus = false;
    public MutableLiveData<Boolean> SocketWriteStatus = new MutableLiveData<Boolean>();
    private boolean ConnectionStatus = false;
    private HashMap<String,String> BodyHash = new HashMap<String,String>();
    public MutableLiveData<HashMap<String,String>> BodyHashMutate = new MutableLiveData<HashMap<String,String>>();
    public MutableLiveData<HashMap<String,String>> HeaderHashMute = new MutableLiveData<HashMap<String,String>>();

    private static String SecreteKey = null;
    private final int VIRGIN_DEVICE = 0;
    private final int LOGIN_COMPLETE = 1;
    private final int LOGIN_ONGOING = 2;
    private static int LoginState = 0;

    public void SetLoginVirgin(){
        LoginState = VIRGIN_DEVICE;
        LogError("Set Login Virgin");
    }

    public void SetLoginStatusComplete(){
        LoginState = LOGIN_COMPLETE;
        LogError("Set Login Status Complete");
    }

    public void SetLoginOnGoing(){
        LoginState = LOGIN_ONGOING;
        LogError("Set Login On Going");
    }

    public MySocketHandler(){
        myMainThread = new Thread(new Thread1());
        myMainThread.start();
        LogError("initiate object");

    }

    public void StartListen(){
        if(ConnectionStatus && (null != input)) {
            myListenThread = new Thread(new Thread2());
            myListenThread.start();
        }
        else{
            LogError("Cannot Start listening");
            SocketListenStatus = false;
        }
    }

    public void StartWrite(HashMap<String, String> data_value_hash){
        if(ConnectionStatus && !data_value_hash.isEmpty() && (null != output)){
            new Thread(new Thread3(data_value_hash)).start();
        }
        else
        {
            LogError("Write InComplete");
            SocketWriteStatus.postValue(false);
        }

    }

    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                LogError("Connecting to Server via IP and Port:..." + SERVER_IP + ", "+ String.valueOf(SERVER_PORT));
                socket = new Socket(SERVER_IP, SERVER_PORT);
                LogError("Initiating I/O");
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                ConnectionStatus = true;
                LogError("Connection established");
                StartListen();
            } catch (IOException e) {
                ConnectionStatus = false;
                LogError("Connection IOException");
                Timer RestartConnection = new Timer();
                RestartConnection.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        myMainThread = new Thread(new Thread1());
                        myMainThread.start();
                        LogError("Restart Connection");
                    }
                }, 1000L);
            }
        }
    }

    class Thread2 implements Runnable {
        @Override
        public void run() {
            LogError("Start listening");
            while (true) {
                try {
                    String message = input.readLine();
                    if (message != null) {
                        LogError("New Message : " + message);
                        message = AES128_Decrypter(SecreteKey,message);
                        LogError("Decrypted message:..." + message);
                        InputProcessor(message);
                        SocketListenStatus= true;
                    }
                    else {
                        LogError("Listening Null message");
                        break;
                    }
                } catch (IOException e) {

                    LogError("Listening IOException");
                    break;
                }
            }
            LogError("Done listening");
            ConnectionStatus = false;
            SocketListenStatus = false;
            Timer RestartConnection = new Timer();
            RestartConnection.schedule(new TimerTask() {
                @Override
                public void run() {
                    myMainThread = new Thread(new Thread1());
                    myMainThread.start();
                    LogError("Restart Connection");
                }
            }, 1000L);

        }

        private String AES128_Decrypter(String loc_key, String loc_mess){
            String result = loc_mess;
            try{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LogError("Decrypted loc_key: " + loc_key);
                    LogError("Decrypted loc_key length: " + loc_key.length());
                    Key key = new Key(loc_key);
                    LogError("Decrypted initiate Key");
                    Token token = Token.fromString(loc_mess);
                    LogError("Decrypted initiate Token");
                    Validator<String> validator = new StringValidator() {
                        @Override
                        public Function<byte[], String> getTransformer() {
                            return String::new;
                        }
                    };
                    result = token.validateAndDecrypt(key, validator);
                    LogError("Decrypted:... "+ result);
                }
            }catch (Exception e){
                LogError("Error while decrypting:..." + e.toString());
                result = loc_mess;
            }

            return result;
        }
    }

    class Thread3 implements Runnable {
        HashMap<String, String> data_value_hash;
        Thread3(HashMap<String, String> data_value_hash) {
            this.data_value_hash = data_value_hash;
        }
        @Override
        public void run() {
            try {
                String encoded_mess = AES128_Encrypter(SecreteKey, messageProcessor(data_value_hash));
                output.write(encoded_mess);
                output.flush();
                SocketWriteStatus.postValue(true);
                LogError("Write Complete");
            }catch (Exception e)
            {
                SocketWriteStatus.postValue(false);
                LogError("Exception:..." + e);
            }
        }

        private String messageProcessor(HashMap<String, String> loc_data_value_hash){
            String body = "{";
            StringBuilder tmp_body = new StringBuilder();

            for (Map.Entry<String, String> stringStringEntry : loc_data_value_hash.entrySet()) {
                Map.Entry tmp_holder_hash = (Map.Entry) stringStringEntry;
                tmp_body.append(", \"").append(tmp_holder_hash.getKey()).append("\": \"").append(tmp_holder_hash.getValue()).append("\"");
            }
            tmp_body = new StringBuilder(tmp_body.substring(2));

            body += tmp_body +"}";
            String header = "{\"byteorder\": \"little\"," +
                        " \"content-type\": \"text/json\"," +
                        " \"content-encoding\": \"utf-8\"," +
                        " \"content-length\": "+String.valueOf(body.length())+"}";

            header = (char)header.length() + header;
            String full_mess = header + body;
            LogError("full message:..." + full_mess);
            return full_mess;
        }

        private String AES128_Encrypter(String loc_key, String loc_mess)
        {
            String result = null;
            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LogError("Encrypted loc_key: " + loc_key);
                    LogError("Encrypted loc_key length: " + loc_key.length());
                    Key key = new Key(loc_key);
                    LogError("Encrypted initiate Key");
                    final Token token = Token.generate(key, loc_mess);
                    LogError("Encrypted initiate Token");
                    result = token.serialise();
                    LogError("Encrypted:... "+ result);
                }
            }
            catch (Exception e)
            {
                LogError("Error while encrypting:..." + e.toString());
            }
            return result;
        }
    }

    private void InputProcessor(String message)
    {
        try{
            //Pre-process the message
            int hdrlength = message.charAt(message.indexOf('{')-1);
            message = message.substring(message.indexOf('{'));
            String messageHead = message.substring(0, hdrlength);
            String messageBody = message.substring(hdrlength);

            LogError("Header : "+messageHead);
            LogError("Body : "+messageBody);
            // hardcore process the message
            // save dictionary from message
            BodyHash = dictionary_processor(messageBody);
            BodyHashMutate.postValue(BodyHash);
            HashMap<String, String> headerHash = dictionary_processor(messageHead);
            HeaderHashMute.postValue(headerHash);

            LogError("Header processed:..."+ headerHash);
            LogError("Body processed:..."+BodyHash);

            String loc_action = BodyHash.get("action");
            String loc_result = BodyHash.get("result");
            if(loc_action == null || loc_result == null){
                return;
            }
            else if(loc_action.equals("virgin")){
                SecreteKey = loc_result;
            }

        }
        catch(Exception e){
            LogError("InputProcessor:..."+ e.toString());
        }

    }

    // hardcore process the message
    private HashMap<String, String> dictionary_processor(String loc_message)
    {
        HashMap<String, String> output_dict = new HashMap<String, String>();
        loc_message = loc_message.replaceAll("\\s+","");
        loc_message = loc_message.substring(1, loc_message.length()-1);
        String[] all_the_data = loc_message.split(Pattern.quote(","));
        for(String i : all_the_data){
            LogError("item:..."+i);

            if(i.charAt(i.length()-1) == '\"'){
                i = i.substring(1,i.length()-1);
                String[] data_value = i.split(Pattern.quote("\":\""),2);
                output_dict.put(data_value[0], data_value[1]);
            } else{
                i = i.substring(1);
                String[] data_value = i.split(Pattern.quote("\":"),2);
                output_dict.put(data_value[0], data_value[1]);
            }

        }

        return output_dict;
    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }
}
