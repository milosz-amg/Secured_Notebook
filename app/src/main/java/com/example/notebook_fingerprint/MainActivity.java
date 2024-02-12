    //ODDANIE:
//haslo: 123123123
//fingerprint 1


package com.example.notebook_fingerprint;
import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;


import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    ConstraintLayout mMainActivity;
    public String note_text;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    private EditText edit_note;
    public Boolean okUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainActivity = findViewById(R.id.main_layout);
        edit_note = (EditText) findViewById(R.id.edit_note);
        Button b_save_note =(Button) findViewById(R.id.b_save_note);
        Button b_password = (Button) findViewById(R.id.b_password);
        okUser = false;

        //sprawdzam jak urzadzenie stoi z biometria
        BiometricManager biometricManager = BiometricManager.from(this);
        switch(biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("FP", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("FP", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("FP", "App can authenticate using biometrics.");
                break;
        }

        //biometric prompt
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            //jak poprwanie zweryfikowano - loadData(){decode}
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),"fingerprint accepted", Toast.LENGTH_SHORT).show();
                mMainActivity.setVisibility(View.VISIBLE);
                okUser = true;
                loadData();


            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Notebook_fingerprint")
                .setDescription("Use fingerprint to login").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);


        b_save_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        b_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("awdgb");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    public void saveData(){
        if(okUser == true){
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Cryptography.genNewKey();

            note_text=edit_note.getText().toString();
            System.out.println(note_text);
            //encrypt plain text
            String note_text_encrypted =Cryptography.encryptNote(note_text);
            editor.putString(TEXT,note_text_encrypted);
            //editor.putString(TEXT,note_text);
            editor.apply();
            Toast.makeText(getApplicationContext(),"note saved", Toast.LENGTH_SHORT).show();
        }
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        note_text = sharedPreferences.getString(TEXT,"Tu będzie twoja notatka").toString();
        System.out.println(note_text);
        //decrypt note
        note_text = Cryptography.decryptNote(note_text);
        edit_note.setText(note_text);
    }

    public void setOkUser(boolean isOk){
        okUser = isOk;
    }
}