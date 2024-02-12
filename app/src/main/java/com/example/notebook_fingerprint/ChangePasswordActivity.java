package com.example.notebook_fingerprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_confirm_new_password;
    private String old_password;
    private  String new_password;
    private String confirm_new_password;
    private Button b_confirm;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PASSWORD = "password";
    public static final String SALT ="salt";

    public String shp_password;
    public String salt_s;
    public byte[] salt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_confirm_new_password = (EditText) findViewById(R.id.et_confirm_new_password);

        b_confirm = (Button) findViewById(R.id.b_confirm);

        loadData();
        b_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                old_password = et_old_password.getText().toString();
                new_password = et_new_password.getText().toString();
                confirm_new_password = et_confirm_new_password.getText().toString();
                String old_password_hash;
                try {
                    old_password_hash = Cryptography.hashPassword(old_password,salt);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }

                if(old_password_hash.equals(shp_password)){
                    if(new_password.equals(confirm_new_password)){
                        try {
                            if(checkPasswordRequirements(new_password)){
                                updateData();
                                //NotebookActivity.setGiven_password(new_password);   //tekst hasła używany do szyfrowania notatki
                                //musi byc zaktualizowany
                                //save data notatki recznie :((

                            }
                            else {
                                Toast.makeText(ChangePasswordActivity.this,"Password dont match requirements",Toast.LENGTH_SHORT).show();
                            }
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeySpecException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        System.out.println("n_ps!=c_n_ps");
                    }
                }
                else {
//                    Toast.makeText(this, "er",Toast.LENGTH_SHORT).show();
                    System.out.println("old_ps_error");
                }
            }
        });
    }
    public boolean checkPasswordRequirements(String password){
        System.out.println("test");
        if(password.length()<8){    //WYMAGANIA WG WYKLADU, NAJLEPSZE DLUGIE HASLO Z KILKU SLOW....
            return false;
        }
        return true;
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        shp_password = sharedPreferences.getString(PASSWORD, "AA0bmfcKC9opGRK362q7regVwNWwJSUDE5EzOhe5nxM=");
        salt_s = sharedPreferences.getString(SALT,"9NfgJm0Sx5Y0i/9MisTktg==");
        salt = Base64.decode(salt_s,Base64.DEFAULT);
    }
    public void updateData() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        byte[] new_salt = Cryptography.generateSalt();
        String new_password_hash = Cryptography.hashPassword(new_password,new_salt);
        String new_salt_s = Base64.encodeToString(new_salt,Base64.DEFAULT);

        editor.putString(PASSWORD,new_password_hash);
        editor.putString(SALT, new_salt_s);
        editor.apply();

        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
    }
}
