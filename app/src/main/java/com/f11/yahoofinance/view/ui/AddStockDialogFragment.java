package com.f11.yahoofinance.view.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.f11.yahoofinance.R;
import com.f11.yahoofinance.data.model.AppStock;
import com.f11.yahoofinance.viewmodel.StockViewModel;

public class AddStockDialogFragment extends DialogFragment {


    EditText mStockText;
    private StockViewModel mStockViewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        mStockViewModel = new ViewModelProvider(this).get(StockViewModel.class);

        mStockText = custom.findViewById(R.id.dialog_stock);

        mStockText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addStock();
                return true;
            }
        });
        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AddStockDialogFragment.this.addStock();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {
        String symbol = mStockText.getText().toString().toUpperCase();
        MainActivity.class.cast(this.getActivity()).addStock(symbol);
        dismissAllowingStateLoss();
    }


}