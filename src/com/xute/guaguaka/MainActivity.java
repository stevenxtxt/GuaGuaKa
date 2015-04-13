package com.xute.guaguaka;

import com.xute.guaguaka.view.Guaguaka;
import com.xute.guaguaka.view.Guaguaka.OnGuaGuaKaCompleteListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Guaguaka mGuaguaka;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mGuaguaka = (Guaguaka) findViewById(R.id.guaguaka);
        mGuaguaka.setOnGuaGuaKaCompleteListener(new OnGuaGuaKaCompleteListener() {
            
            @Override
            public void complete() {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "Complete!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
