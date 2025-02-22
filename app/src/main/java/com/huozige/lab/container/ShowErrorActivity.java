package com.huozige.lab.container;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.huozige.lab.container.utilities.LifecycleUtility;

public class ShowErrorActivity extends AppCompatActivity {

    public final static String EXTRA_KEY_MESSAGE ="message";

    TextView _text;
    Button _btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_error);

        _text = this.findViewById(R.id.txtErrorDetail);
        _btn = this.findViewById(R.id.btnCloseApp);

        _btn.setOnClickListener((l)-> LifecycleUtility.close());
    }

    @Override
    protected void onResume(){
        super.onResume();

        Intent i = this.getIntent();

       String message =  i.getStringExtra(EXTRA_KEY_MESSAGE);

        _text.setText(message);

    }
}