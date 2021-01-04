package com.crittermap.backcountrynavigator.xe.ui.account.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCBillTransaction;
import com.crittermap.backcountrynavigator.xe.ui.account.viewHolder.BCMyWalletViewHolder;

import java.util.List;
import java.util.Locale;

/**
 * Created by henry on 5/17/2018.
 */

public class BCMyWalletAdapter extends RecyclerView.Adapter<BCMyWalletViewHolder> {

    private List<BCBillTransaction> mTransactions;
    private Context mContext;

    public BCMyWalletAdapter(List<BCBillTransaction> transactions, Context context) {
        mTransactions = transactions;
        mContext = context;
    }

    @NonNull
    @Override
    public BCMyWalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.my_wallet_item, parent, false);
        return new BCMyWalletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BCMyWalletViewHolder holder, int position) {
        BCBillTransaction transaction = mTransactions.get(position);
        holder.dateTxt.setText(transaction.getDateOfTransaction());
        String coinFormat = String.format(Locale.getDefault(), "%d coins", transaction.getCost());
        holder.costTxt.setText(coinFormat);
        String mapFormat = String.format(Locale.getDefault(), "ID: %s - %s", transaction.getMapId(), transaction.getMapName());
        holder.mapTxt.setText(mapFormat);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }
}
