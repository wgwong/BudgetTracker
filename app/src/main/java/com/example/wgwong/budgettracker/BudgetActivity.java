package com.example.wgwong.budgettracker;

import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

import static android.support.v4.view.GravityCompat.*;
import static com.example.wgwong.budgettracker.R.id.*;

public class BudgetActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

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

    public AlertDialog createTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View transactionDialogContentView = inflater.inflate(R.layout.dialog_add_transaction, null);
        builder.setView(transactionDialogContentView)
                //add informative text
                //.setMessage(R.string.new_transaction_content_message)
                .setTitle(R.string.new_transaction_title)
                //add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        String transactionCost = ((EditText) transactionDialogContentView.findViewById(new_transaction_cost_edittext)).getText().toString();
                        if (transactionCost.length() > 0) {
                            BigDecimal transactionValue = new BigDecimal(transactionCost);
                            transactionValue.setScale(2, BigDecimal.ROUND_HALF_UP);


                            TextView dailyBalanceTextView = findViewById(daily_balance);

                            BigDecimal dailyBalance = new BigDecimal(dailyBalanceTextView.getText().toString().substring(1));
                            BigDecimal newBalance = dailyBalance.add(transactionValue);

                            String newBalanceText = "$" + newBalance.toString();
                            dailyBalanceTextView.setText(newBalanceText);
                            Snackbar.make(findViewById(coordinator_layout), R.string.new_transaction_added_snackbar_message, Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
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
                if (decimalIndex > 0 && transactionString.substring(decimalIndex).length() > 2) {
                    editable.delete(decimalIndex+3,editable.length());
                }

                _ignore = true; //prevent infinite loop

                _ignore = false; //release
            }
        });

        return builder.create();
    }
}
