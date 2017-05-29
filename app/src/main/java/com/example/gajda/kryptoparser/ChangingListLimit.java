package com.example.gajda.kryptoparser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangingListLimit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changing_list_limit);
    }

    public void setNewLimit (View view) {
        int newLimit;
        EditText etNewLimit = (EditText) findViewById(R.id.new_limit);
        try {
            String newValueString = etNewLimit.getText().toString();
            newLimit = Integer.valueOf(newValueString);
            if (newLimit > 0) {
                setListLimit(newLimit);
                Toast.makeText(ChangingListLimit.this, "Limit changed successfully ", Toast.LENGTH_SHORT).show();
                switchToMain();
            } else {
                Toast.makeText(ChangingListLimit.this, "Value to low", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(ChangingListLimit.this, "wrong value detected in text field\nonly Integers are accepted here", Toast.LENGTH_LONG).show();
        }
    }
    public void cancelLimitChange (View view) {
        switchToMain();
    }
    private void switchToMain () {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    protected void setListLimit (int newLimit) {
        SharedPreferences preferences = getSharedPreferences(MainActivity.LIST_PREFFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(MainActivity.KEY_PREFERENCES_LIST_LIMIT, newLimit);
        editor.apply();
    }
}
