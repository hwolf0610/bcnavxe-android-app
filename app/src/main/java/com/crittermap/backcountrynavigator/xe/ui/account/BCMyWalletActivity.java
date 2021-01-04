package com.crittermap.backcountrynavigator.xe.ui.account;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCBillTransaction;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.account.adapter.BCMyWalletAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by henry on 5/16/2018.
 */

public class BCMyWalletActivity extends BCBaseActivity {

    @BindView(R.id.bill_history_rcv)
    RecyclerView mBillHistoryRcv;

    @BindView(R.id.current_balance_txt)
    TextView mCurrentBalanceTxt;

    @BindView(R.id.transaction_txt)
    TextView mTransactionTxt;

    @BindView(R.id.total_spend_txt)
    TextView mTotalSpendTxt;
    List<BCBillTransaction> bcBillTransactions;
    private BCMyWalletAdapter mMyWalletAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);
        configToolbar();
        makeStatusBarNotTransparent();

        bcBillTransactions = new ArrayList<>();
        initBillTransaction();

        mMyWalletAdapter = new BCMyWalletAdapter(bcBillTransactions, getContext());

        mBillHistoryRcv.setLayoutManager(new LinearLayoutManager(this));
        mBillHistoryRcv.setAdapter(mMyWalletAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mBillHistoryRcv.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mBillHistoryRcv.addItemDecoration(dividerItemDecoration);
    }

    private void initBillTransaction() {

        BCBillTransaction transaction1 = new BCBillTransaction("1", "1", "May 09", -20, "49305", "Flattop Mountain Trail");
        bcBillTransactions.add(transaction1);
        BCBillTransaction transaction2 = new BCBillTransaction("2", "2", "May 07", -21, "49305", "Flattop Mountain Trail");
        bcBillTransactions.add(transaction2);
        BCBillTransaction transaction3 = new BCBillTransaction("3", "3", "May 06", -21, "49305", "Flattop Mountain Trail");
        bcBillTransactions.add(transaction3);
        BCBillTransaction transaction4 = new BCBillTransaction("4", "4", "May 05", -33, "49305", "Flattop Mountain Trail");
        bcBillTransactions.add(transaction4);
        BCBillTransaction transaction5 = new BCBillTransaction("5", "5", "May 04", -44, "49305", "Flattop Mountain Trail");
        bcBillTransactions.add(transaction5);
        BCBillTransaction transaction6 = new BCBillTransaction("6", "5", "May 03", -34, "49305", "Flattop Mountain Trail");
        bcBillTransactions.add(transaction6);
        BCBillTransaction transaction7 = new BCBillTransaction("7", "5", "May 02", -77, "49305", "Flattop Mountain Trail");
        bcBillTransactions.add(transaction7);

        int totalSpendCost = 0;
        for (BCBillTransaction bcBillTransaction : bcBillTransactions) {
            totalSpendCost += bcBillTransaction.getCost();
        }

        mTransactionTxt.setText(String.valueOf(bcBillTransactions.size()));
        mTotalSpendTxt.setText(String.valueOf(totalSpendCost));
    }

    private void configToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
