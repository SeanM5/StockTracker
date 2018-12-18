package edu.temple.stocktracker;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class addStockDialogFragment extends DialogFragment {

    EditText stockEdit;
    Button cancel;
    Button add;
    stockBoy parent;
    int width;
    int height;


    public addStockDialogFragment() {
        // Required empty public constructor
    }

    public static addStockDialogFragment newInstance( ) {
        addStockDialogFragment frag = new addStockDialogFragment();
        return frag;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (stockBoy) context;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parent = null;
    }

    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(width, height/2);
        window.setGravity(Gravity.CENTER);
        //TODO:
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_stock_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.add));

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.enterStock));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.fragment_add_stock_dialog, null);
        builder.setView(v);

        stockEdit = v.findViewById(R.id.stockSymbolText);

        builder.setPositiveButton(getResources().getString(R.string.ok),  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d("ok ", stockEdit.getText().toString() );

                parent.getStockSymbol(stockEdit.getText().toString());
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

}

interface stockBoy {
    void getStockSymbol(String symbol);
}
