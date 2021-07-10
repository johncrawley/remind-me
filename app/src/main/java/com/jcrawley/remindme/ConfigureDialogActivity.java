package com.jcrawley.remindme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ConfigureDialogActivity  extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_dialog);
      //  findViewById(R.id.appName).setOnClickListener(this);
    //    addClickListeners();
    }



    private void addClickListener(int id){
        findViewById(id).setOnClickListener(this);
    }



    public void onClick(View view){
        finish();
    }

}
