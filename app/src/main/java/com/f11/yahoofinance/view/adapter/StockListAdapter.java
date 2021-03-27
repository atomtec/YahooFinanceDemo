package com.f11.yahoofinance.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.StockViewHolder> {

    private final Context mContext;
    private final DecimalFormat mDollarFormatWithPlus;
    private final DecimalFormat mDollarFormat;
    private final DecimalFormat mPercentageFormat;
    private boolean mShowPercentage = false;

    private List<AppStock> mStocks;


    public StockListAdapter(Context context) {
        this.mContext = context;
        mDollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus.setPositivePrefix("+$");
        mPercentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        mPercentageFormat.setMaximumFractionDigits(2);
        mPercentageFormat.setMinimumFractionDigits(2);
        mPercentageFormat.setPositivePrefix("+");
    }

    public void setStocks(List<AppStock> stocks){
        this.mStocks = stocks;
        notifyDataSetChanged();
    }

    public void toggleMode(boolean showPercentage){
        this.mShowPercentage = showPercentage;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockListAdapter.StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.list_item_quote, parent, false);
        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull StockListAdapter.StockViewHolder holder, int position) {
        AppStock stock = mStocks.get(position);

        holder.symbol.setText(stock.getSymbol());
        holder.price.setText(mDollarFormat.format(stock.getPrice()));
        holder.name.setText(stock.getStockname());


        float rawAbsoluteChange = stock.getAbsolutechange();
        float percentageChange = stock.getChange();

        if (rawAbsoluteChange > 0) {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = mDollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = mPercentageFormat.format(percentageChange / 100);

        if (mShowPercentage) {
            holder.change.setText(percentage);
        } else {
            holder.change.setText(change);
        }


    }

    @Override
    public int getItemCount() {
        if (mStocks != null){
            return mStocks.size();
        }
        return 0;
    }

    static class StockViewHolder  extends RecyclerView.ViewHolder {
        TextView symbol;
        TextView price;
        TextView change;
        TextView name;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbol);
            price =  itemView.findViewById(R.id.price);
            change = itemView.findViewById(R.id.change);
            name = itemView.findViewById(R.id.company_name);
        }
    }
}
