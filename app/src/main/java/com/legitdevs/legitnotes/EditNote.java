package com.legitdevs.legitnotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditNote extends AppCompatActivity {

    EditText title;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        title=(EditText)findViewById(R.id.editTitle);
        text=(EditText)findViewById(R.id.editText);

        Bundle bundle= getIntent().getExtras();
        if(bundle!= null){
            text.setText(bundle.getString("bundle"));
        }





    }
}
