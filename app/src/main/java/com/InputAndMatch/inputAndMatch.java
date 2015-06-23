package com.InputAndMatch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.liveangel.test.R;

/**
 * Created by GoLD on 2015/6/1.
 */
public class inputAndMatch extends Activity{

    String[] books = new String[] { "rollen", "rollenholt", "rollenren", "roll" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // 创建一个ArrayAdapter封装数组
        ArrayAdapter<String> av = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, books);
        AutoCompleteTextView auto = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        auto.setAdapter(av);
    }


}
