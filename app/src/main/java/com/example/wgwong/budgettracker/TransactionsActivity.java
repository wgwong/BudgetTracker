package com.example.wgwong.budgettracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionsActivity extends AppCompatActivity {
    private HashMap<String, ArrayList<Transaction>> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        transactions = (HashMap<String, ArrayList<Transaction>>) intent.getSerializableExtra(BudgetActivity.TRANSACTION_MESSAGE);

        //debug
        ArrayList<Transaction> transactionList = transactions.get("today");
        for (int i = 0; i < transactionList.size(); i++) {
            Log.d("debugg", "transactionactivity - " + transactionList.get(i).toString());
        }
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }
}
