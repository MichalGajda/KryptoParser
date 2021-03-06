package com.example.gajda.kryptoparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class AboutAuthors extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_authors);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.menu_a, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.calculator:
                Toast.makeText(this, "Calculator", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, CalculatorActivity.class);
                startActivity(intent);
                return true;
            case R.id.wallet:
                Toast.makeText(this, "Wallet", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, Wallet.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
