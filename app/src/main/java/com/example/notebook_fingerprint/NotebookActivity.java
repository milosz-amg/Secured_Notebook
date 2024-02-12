package com.example.notebook_fingerprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NotebookActivity extends AppCompatActivity {
    public String note_text;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    private EditText edit_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        edit_note = (EditText) findViewById(R.id.edit_note);
        Button b_save_note =(Button) findViewById(R.id.b_save_note);
        Button b_change_password = (Button) findViewById(R.id.b_change_password);

        loadData();
        updateViews();

        b_save_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        b_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
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
    public void updateViews(){
        edit_note.setText(note_text);
    }
    public static void setGiven_password(String updated_password){
    }
}