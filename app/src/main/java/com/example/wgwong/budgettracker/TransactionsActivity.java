package com.example.wgwong.budgettracker;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.solver.widgets.ConstraintHorizontalLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionsActivity extends AppCompatActivity {
    private HashMap<String, ArrayList<Transaction>> transactions;
    private BigDecimal balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refresh();
        redraw();
    }

    private void redraw() {
        ArrayList<Transaction> transactionList = transactions.get("today");
        Typeface pt_sans_typeface = Typeface.create("@font/pt_sans", Typeface.NORMAL);

        if (transactionList.size() > 0) {
            NestedScrollView outerFrame = findViewById(R.id.content_transactions_frame);
            outerFrame.removeAllViews(); //clear any previous views to allow redraw
            LinearLayout transactionsContainer = new LinearLayout(getApplicationContext());
            transactionsContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            transactionsContainer.setOrientation(LinearLayout.VERTICAL);
            outerFrame.addView(transactionsContainer);

            for (int i = 0; i < transactionList.size(); i++) {
                final Transaction transaction = transactionList.get(i);
                final CardView cd = new CardView(getApplicationContext());
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(Utilities.dpToPixels(400, getApplicationContext()), Utilities.dpToPixels(280, getApplicationContext()));
                cardParams.setMargins(0, Utilities.dpToPixels(10, getApplicationContext()), 0, 0);
                cardParams.gravity = Gravity.CENTER;
                cd.setLayoutParams(cardParams);
                cd.setRadius(Utilities.dpToPixels(2, getApplicationContext()));
                cd.setId(Utilities.generateRandomId());
                transactionsContainer.addView(cd);

                RelativeLayout rl = new RelativeLayout(getApplicationContext());
                RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utilities.dpToPixels(280, getApplicationContext()));
                rl.setLayoutParams(rlParams);
                cd.addView(rl);

                ImageView iv = new ImageView(getApplicationContext());
                RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utilities.dpToPixels(180, getApplicationContext()));
                ivParams.alignWithParent = true;
                iv.setLayoutParams(ivParams);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                String category = transaction.getCategory();
                if (category.equals(getString(R.string.transaction_category_dining))) {
                    iv.setImageResource(R.drawable.dining);
                } else if (category.equals(getString(R.string.transaction_category_groceries))) {
                    iv.setImageResource(R.drawable.groceries);
                } else if (category.equals(getString(R.string.transaction_category_retail))) {
                    iv.setImageResource(R.drawable.retail);
                } else if (category.equals(getString(R.string.transaction_category_entertainment))) {
                    iv.setImageResource(R.drawable.entertainment);
                } else if (category.equals(getString(R.string.transaction_category_transportation))) {
                    iv.setImageResource(R.drawable.transportation);
                } else if (category.equals(getString(R.string.transaction_category_travel))) {
                    iv.setImageResource(R.drawable.travel);
                } else if (category.equals(getString(R.string.transaction_category_hotel))) {
                    iv.setImageResource(R.drawable.hotel);
                } else if (category.equals(getString(R.string.transaction_category_gas))) {
                    iv.setImageResource(R.drawable.gas);
                } else {
                    iv.setImageResource(R.drawable.gradient_1);
                }
                iv.setId(Utilities.generateRandomId());
                rl.addView(iv);

                TextView costView = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams costViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                costViewParams.bottomMargin = Utilities.dpToPixels(2, getApplicationContext());
                costViewParams.rightMargin = Utilities.dpToPixels(12, getApplicationContext());
                costViewParams.addRule(RelativeLayout.ALIGN_BOTTOM, iv.getId());
                costView.setLayoutParams(costViewParams);
                costView.setGravity(Gravity.RIGHT);
                costView.setTextColor(Color.WHITE);
                costView.setTextSize(Utilities.dpToPixels(16, getApplicationContext()));
                costView.setText("$" + transaction.getCost().toString());
                costView.setTextIsSelectable(false);
                costView.setTypeface(pt_sans_typeface);
                costView.setId(Utilities.generateRandomId());
                rl.addView(costView);

                TextView categoryView = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams categoryViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                categoryViewParams.leftMargin = Utilities.dpToPixels(30, getApplicationContext());
                categoryViewParams.topMargin = Utilities.dpToPixels(8, getApplicationContext());
                categoryViewParams.addRule(RelativeLayout.BELOW, iv.getId());
                categoryView.setLayoutParams(categoryViewParams);
                categoryView.setTextColor(Color.BLACK);
                categoryView.setTextSize(Utilities.dpToPixels(4, getApplicationContext()));
                categoryView.setText(transaction.getCategory());
                categoryView.setTextIsSelectable(false);
                categoryView.setTypeface(pt_sans_typeface);
                categoryView.setId(Utilities.generateRandomId());
                rl.addView(categoryView);

                TextView timestampView = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams timestampViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                timestampViewParams.leftMargin = Utilities.dpToPixels(30, getApplicationContext());
                timestampViewParams.addRule(RelativeLayout.BELOW, categoryView.getId());
                timestampView.setLayoutParams(timestampViewParams);
                timestampView.setTextColor(Color.BLACK);
                timestampView.setTextSize(Utilities.dpToPixels(4, getApplicationContext()));
                timestampView.setText(transaction.getTimestamp().toString());
                timestampView.setTextIsSelectable(false);
                timestampView.setTypeface(pt_sans_typeface);
                timestampView.setId(Utilities.generateRandomId());
                rl.addView(timestampView);

                Button editButton = new Button(getApplicationContext());
                RelativeLayout.LayoutParams editButtonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                editButtonParams.addRule(RelativeLayout.BELOW, timestampView.getId());
                editButton.setLayoutParams(editButtonParams);
                editButton.setBackgroundColor(Color.TRANSPARENT);
                editButton.setText(R.string.edit);
                editButton.setId(Utilities.generateRandomId());
                rl.addView(editButton);

                Button deleteButton = new Button(getApplicationContext());
                RelativeLayout.LayoutParams deleteButtonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                deleteButtonParams.addRule(RelativeLayout.BELOW, timestampView.getId());
                deleteButtonParams.addRule(RelativeLayout.RIGHT_OF, editButton.getId());
                deleteButton.setLayoutParams(deleteButtonParams);
                deleteButton.setBackgroundColor(Color.TRANSPARENT);
                deleteButton.setText(R.string.delete);
                deleteButton.setTextColor(Color.parseColor(String.format("#%06x", ContextCompat.getColor(this, R.color.colorAccentCompliment) & 0xffffff)));
                deleteButton.setId(Utilities.generateRandomId());
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog deleteDialog = createDeleteDialog(transaction.getId(), cd.getId());
                        deleteDialog.show();
                    }
                });
                rl.addView(deleteButton);
            }
        } else {
            NestedScrollView outerFrame = findViewById(R.id.content_transactions_frame);
            outerFrame.removeAllViews(); //clear any previous views to allow redraw

            TextView noTransactionsView = new TextView(getApplicationContext());
            LinearLayout.LayoutParams noTransactionsViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            noTransactionsViewParams.setMargins(0, Utilities.dpToPixels(40, getApplicationContext()), 0, 0);
            noTransactionsView.setLayoutParams(noTransactionsViewParams);
            noTransactionsView.setTextColor(Color.BLACK);
            noTransactionsView.setTextSize(Utilities.dpToPixels(12, getApplicationContext()));
            noTransactionsView.setText(R.string.no_transactions_message);
            noTransactionsView.setTextIsSelectable(false);
            noTransactionsView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noTransactionsView.setTypeface(pt_sans_typeface);
            noTransactionsView.setId(Utilities.generateRandomId());
            outerFrame.addView(noTransactionsView);
        }
    }

    private void refresh() {
        //try and load previous balance
        try {
            HashMap<String, BigDecimal> balanceMap = (HashMap<String, BigDecimal>) Utilities.loadFile(getString(R.string.balance_filename), getApplicationContext());
            balance = balanceMap.get("balance");
        } catch (Exception e) {
            //no previous balance, proceed as usual
            balance = new BigDecimal(0);
            balance.setScale(2, BigDecimal.ROUND_HALF_UP);
            Log.w("warn", "No balance file found, initializing new balance");
        }

        //try and load previous transactions
        try {
            transactions = (HashMap<String, ArrayList<Transaction>>) Utilities.loadFile(getString(R.string.transactions_filename), getApplicationContext());
        } catch (Exception e) {
            //no previous transactions, proceed as usual
            //TODO handle the case where the balance exists but the transactions are missing or vice versa
            transactions = new HashMap<>();
            transactions.put("today", new ArrayList<Transaction>());
            transactions.put("weekly", new ArrayList<Transaction>());
            Log.w("warn", "No transactions file found, initializing new transactions list");
        }

        //debug
        Log.d("debugg", "transactionactivity refresh balance - " + balance.toString());
        for (int i = 0; i < transactions.get("today").size(); i++) {
            Transaction transaction = transactions.get("today").get(i);
            Log.d("debugg", "transactionactivity refresh - " + transaction.toString());
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
        Log.d("debugg", "transactionactivity persist balance - " + balance.toString());
        for (int i = 0; i < transactions.get("today").size(); i++) {
            Transaction transaction = transactions.get("today").get(i);
            Log.d("debugg", "transactionactivity persist - " + transaction.toString());
        }

        return true;
    }

    private AlertDialog createDeleteDialog(final int transactionId, final int cardId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_transaction_content_message)
                .setTitle(R.string.delete_transaction_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteTransaction(transactionId);
                        persist();
                        //TODO confirm successful delete
                        //deleteTransactionView(cardId);
                        redraw();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, just close the dialog
                    }
                });

        return builder.create();
    }

    private boolean deleteTransaction(int transactionId) {
        ArrayList<Transaction> today = transactions.get("today");
        ArrayList<Transaction> weekly = transactions.get("weekly");
        int todayIndex = -1;
        int weeklyIndex = -1;
        for (int i = 0; i < today.size(); i++) {
            Transaction transaction = today.get(i);
            if (transaction.getId() == transactionId) {
                todayIndex = i;
                balance = balance.subtract(transaction.getCost());
                break;
            }
        }
        for (int i = 0; i < weekly.size(); i++) {
            Transaction transaction = weekly.get(i);
            if (transaction.getId() == transactionId) {
                weeklyIndex = i;
                break;
            }
        }
        boolean deleted = false;
        if (todayIndex > -1) {
            transactions.get("today").remove(todayIndex);
            deleted = true;
        }
        if (weeklyIndex > -1) {
            transactions.get("weekly").remove(weeklyIndex);
            deleted = true;
        }
        return deleted;
    }

    private boolean deleteTransactionView(int cardId) {
        return true;
    }
}
