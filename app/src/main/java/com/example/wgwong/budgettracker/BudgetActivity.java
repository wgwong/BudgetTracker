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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static android.support.v4.view.GravityCompat.*;
import static com.example.wgwong.budgettracker.R.id.*;

public class BudgetActivity extends AppCompatActivity {
    final long dayInMilliseconds = 86400000;

    private HashMap<String, ArrayList<Transaction>> transactions;
    private BigDecimal balance;
    private BigDecimal budget;
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
                        //item.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        int id = item.getItemId();

                        switch (id) {
                            case nav_home: {
                                /*
                                don't do anything since we're already here
                                */
                                break;
                            }
                            case nav_transactions: {
                                Intent intent = new Intent(getApplicationContext(), TransactionsActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case nav_trend: {
                                break;
                            }
                            case nav_share: {
                                break;
                            }
                            case nav_settings: {
                                break;
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
        analyzeTransactions();
        redraw();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debugg", "resuming budgetactivity"); //debug
        refresh();
        analyzeTransactions();
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

                            Transaction transaction = new Transaction(Calendar.getInstance().getTime(), transactionValue, selectedCategoryText);
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
                    editable.delete(decimalIndex + 3, editable.length());
                }

                _ignore = true; //prevent infinite loop

                _ignore = false; //release
            }
        });

        return builder.create();
    }

    private void redraw() {
        ((TextView) findViewById(R.id.daily_balance)).setText("$" + balance.toString());
        ((TextView) findViewById(R.id.daily_budget)).setText("$" + (budget.subtract(balance)).toString());

        //debug
        Log.d("debugg", "budgetactivity redraw balance - " + balance.toString());
    }

    private void refresh() {
        //try and load previous balance & budget
        try {
            HashMap<String, BigDecimal> balanceMap = (HashMap<String, BigDecimal>) Utilities.loadFile(getString(R.string.balance_filename), getApplicationContext());
            if (balanceMap.containsKey("balance")) {
                balance = balanceMap.get("balance");
            } else {
                balance = new BigDecimal(0);
                balance.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            if (balanceMap.containsKey("budget")) {
                budget = balanceMap.get("budget");
            } else {
                budget = new BigDecimal(25);
                budget.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            Snackbar.make(findViewById(coordinator_layout), R.string.loaded_balance_message, Snackbar.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            //no previous balance, proceed as usual
            balance = new BigDecimal(0);
            balance.setScale(2, BigDecimal.ROUND_HALF_UP);
            budget = new BigDecimal(25);
            budget.setScale(2, BigDecimal.ROUND_HALF_UP);
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
        //persist balance & budget
        String filename = getString(R.string.balance_filename);
        HashMap<String, BigDecimal> balanceMap = new HashMap<>();
        balanceMap.put("balance", balance);
        balanceMap.put("budget", budget);
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

    private BigDecimal calculateTodayBalance(ArrayList<Transaction> todayTransactions) {
        BigDecimal balance = new BigDecimal(0);
        balance.setScale(2, BigDecimal.ROUND_HALF_UP);

        for (int i = 0; i < todayTransactions.size(); i++) {
            balance = balance.add(todayTransactions.get(i).getCost());
        }

        return balance;
    }

    private void analyzeTransactions() {
        //check if there are any old transactions in today's list (older than today 12:00 am)
        ArrayList<Transaction> todayTransactions = transactions.get("today");

        for (int i = 0; i < todayTransactions.size(); i++) {
            Log.d("debugg", "analyzeTransactions: todaysTransactions - " + todayTransactions.get(i).toString()); //debug
        }

        GregorianCalendar gc = new GregorianCalendar();
        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH);
        int day = gc.get(Calendar.DAY_OF_MONTH);
        GregorianCalendar earliestTodayCalendar = new GregorianCalendar();
        earliestTodayCalendar.set(year, month, day, 0, 0, 0);

        Date earliestTodayDate = earliestTodayCalendar.getTime();

        ArrayList<Transaction> yesterdayTransactions = new ArrayList<>();

        if (todayTransactions.size() > 0) {
            int indexToCheck = 0;
            while (true) {
                Transaction currentTransaction = todayTransactions.get(indexToCheck);
                Date transactionDate = currentTransaction.getTimestamp();
                if (transactionDate.before(earliestTodayDate)) { //found transactions not from today
                    //if transactions from yesterday, add to yesterdayTransactions to generate yesterday's progress report
                    if (earliestTodayDate.getTime() - transactionDate.getTime() <= dayInMilliseconds) {
                        yesterdayTransactions.add(currentTransaction);
                    }
                    todayTransactions.remove(indexToCheck);
                } else {
                    indexToCheck++;
                }
                if (indexToCheck >= todayTransactions.size()) { //reached end of list
                    break;
                }
            }
        }

        transactions.put("today", todayTransactions); //update today's transactions list
        balance = calculateTodayBalance(todayTransactions); //update today's balance
        persist();

        if (yesterdayTransactions.size() > 0) {
            generateProgressReport(yesterdayTransactions);
        }
    }

    private void generateProgressReport(ArrayList<Transaction> yesterdayTransactions) {
        BigDecimal yesterdayBalance = new BigDecimal(0);
        yesterdayBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
        for (int i = 0; i < yesterdayTransactions.size(); i++) {
            yesterdayBalance = yesterdayBalance.add(yesterdayTransactions.get(i).getCost());
        }
        createDailyReportDialog(yesterdayTransactions.get(0).getTimestamp(), yesterdayBalance).show();
    }

    private AlertDialog createDailyReportDialog(Date yesterdayTimestamp, BigDecimal yBalance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterdayString = dateFormat.format(yesterdayTimestamp);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dailyReportDialogContentView = inflater.inflate(R.layout.dialog_daily_report, null);
        builder.setView(dailyReportDialogContentView)
                .setTitle(getString(R.string.daily_report_title) + " (" + yesterdayString + ")")
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing, close modal
                        redraw();
                    }
                })
                .setNeutralButton(R.string.view_yesterday_transaction, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        redraw();
                        //view transactions w/ parameter yesterday
                    }
                });

        BigDecimal finalBalance = yBalance.subtract(budget);

        String dialog_content_message;
        if (finalBalance.compareTo(new BigDecimal(0)) == 1) { //final balance was positive, went over budget
            dialog_content_message = getString(R.string.daily_report_content_message_went) + " $" + finalBalance.toString() + " " + getString(R.string.daily_report_content_message_over_budget);
        } else { //saved money or met budget
            dialog_content_message = getString(R.string.daily_report_content_message_saved) + " $" + (finalBalance.multiply(new BigDecimal(-1))).toString() + getString(R.string.exclamation_mark);
        }
        ((TextView) dailyReportDialogContentView.findViewById(R.id.daily_report_content_message_textview)).setText(dialog_content_message);
        ((TextView) dailyReportDialogContentView.findViewById(R.id.daily_report_balance_number)).setText("$" + yBalance.toString());
        ((TextView) dailyReportDialogContentView.findViewById(R.id.daily_report_budget_number)).setText("$" + budget.toString());

        return builder.create();
    }
}
