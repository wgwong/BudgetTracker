package com.example.wgwong.budgettracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.support.v4.view.GravityCompat.*;
import static com.example.wgwong.budgettracker.R.id.*;

public class BudgetActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private HashMap<String, ArrayList<Transaction>> transactions;
    private BigDecimal balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        mDrawerLayout = findViewById(drawer_layout);
        NavigationView navigationView = findViewById(nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // set item as selected to persist highlight
                        item.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        int id = item.getItemId();

                        switch (id) {
                            case nav_profile: {

                            }
                            case nav_transactions: {
                                Intent intent = new Intent(getApplicationContext(), TransactionsActivity.class);
                                startActivity(intent);
                            }
                            case nav_trend: {

                            }
                            case nav_share: {

                            }
                            case nav_settings: {

                            }
                        }

                        return true;
                    }
                }
        );

        FloatingActionButton fab = findViewById(new_transaction_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog transactionDialog = createTransactionDialog();
                transactionDialog.show();
            }
        });

        refresh();
        redraw();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debugg", "resuming budgetactivity"); //debug
        refresh();
        redraw();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_budget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final int nav_drawer_button_id = 16908332;
        switch (id) {
            case nav_drawer_button_id:
                mDrawerLayout.openDrawer(START);
                return true;
            case action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog createTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View transactionDialogContentView = inflater.inflate(R.layout.dialog_add_transaction, null);
        builder.setView(transactionDialogContentView)
                .setTitle(R.string.new_transaction_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        String transactionCost = ((EditText) transactionDialogContentView.findViewById(new_transaction_cost_edittext)).getText().toString();
                        if (transactionCost.length() > 0) {
                            BigDecimal transactionValue = new BigDecimal(transactionCost);
                            transactionValue.setScale(2, BigDecimal.ROUND_HALF_UP);

                            TextView dailyBalanceTextView = findViewById(daily_balance);
                            balance = balance.add(transactionValue);
                            dailyBalanceTextView.setText("$" + balance.toString());

                            //get transaction category
                            RadioGroup rg = transactionDialogContentView.findViewById(R.id.new_transaction_category_radiogroup); //TODO find some way to not make this null
                            RadioButton selectedRadioButton = transactionDialogContentView.findViewById(rg.getCheckedRadioButtonId());
                            String selectedCategoryText = selectedRadioButton.getText().toString();

                            Transaction transaction = new Transaction(new Date(), transactionValue, selectedCategoryText);
                            transactions.get("today").add(transaction);
                            transactions.get("weekly").add(transaction);

                            persist();
                            redraw();

                            Snackbar.make(findViewById(coordinator_layout), R.string.new_transaction_added_snackbar_message, Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, just close the dialog
                    }
                });

        final EditText edit = transactionDialogContentView.findViewById(new_transaction_cost_edittext);
        edit.addTextChangedListener(new TextWatcher() {
            boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (_ignore) {
                    return;
                }
                String transactionString = editable.toString();

                int decimalIndex = transactionString.indexOf(".");
                if (decimalIndex >= 0 && transactionString.substring(decimalIndex).length() > 3) {
                    editable.delete(decimalIndex+3,editable.length());
                }

                _ignore = true; //prevent infinite loop

                _ignore = false; //release
            }
        });

        return builder.create();
    }
    private void redraw() {
        ((TextView) findViewById(R.id.daily_balance)).setText("$" + balance.toString());

        //debug
        Log.d("debugg", "budgetactivity redraw balance - " + balance.toString());
    }

    private void refresh() {
        //try and load previous balance
        try {
            HashMap<String, BigDecimal> balanceMap = (HashMap<String, BigDecimal>) Utilities.loadFile(getString(R.string.balance_filename), getApplicationContext());
            balance = balanceMap.get("balance");
            Snackbar.make(findViewById(coordinator_layout), R.string.loaded_balance_message, Snackbar.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            //no previous balance, proceed as usual
            balance = new BigDecimal(0);
            balance.setScale(2, BigDecimal.ROUND_HALF_UP);
            Log.w("warn", "No balance file found, initializing new balance");
        }

        //try and load previous transactions
        try {
            transactions = (HashMap<String, ArrayList<Transaction>>) Utilities.loadFile(getString(R.string.transactions_filename), getApplicationContext());
            Snackbar.make(findViewById(coordinator_layout), R.string.loaded_transactions_message, Snackbar.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            //no previous transactions, proceed as usual
            //TODO handle the case where the balance exists but the transactions are missing or vice versa
            transactions = new HashMap<>();
            transactions.put("today", new ArrayList<Transaction>());
            transactions.put("weekly", new ArrayList<Transaction>());
            Log.w("warn", "No transactions file found, initializing new transactions list");
        }

        //debug
        Log.d("debugg", "budgetactivity refresh balance - " + balance.toString());
        for (int i = 0; i < transactions.get("today").size(); i++) {
            Transaction transaction = transactions.get("today").get(i);
            Log.d("debugg", "budgetactivity refresh - " + transaction.toString());
        }
    }

    private boolean persist() {
        //persist balance
        String filename = getString(R.string.balance_filename);
        HashMap<String, BigDecimal> balanceMap = new HashMap<>();
        balanceMap.put("balance", balance);
        Utilities.saveFile(filename, balanceMap, getApplicationContext());

        //persist transactions
        filename = getString(R.string.transactions_filename);
        Utilities.saveFile(filename, transactions, getApplicationContext());

        //debug
        Log.d("debugg", "budgetactivity persist balance - " + balance.toString());
        for (int i = 0; i < transactions.get("today").size(); i++) {
            Transaction transaction = transactions.get("today").get(i);
            Log.d("debugg", "budgetactivity persist - " + transaction.toString());
        }
        return true;
    }
}
