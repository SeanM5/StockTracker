package edu.temple.stocktracker;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class stockDetails extends Fragment {

    TextView name;
    TextView curPrice;
    TextView openPrice;
    WebView chart ;
    String symbol;
    Button remove;
    detail parentActivity;


    public stockDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stock_details, container, false);

        name = v.findViewById(R.id.name);
        curPrice = v.findViewById(R.id.curPrice);
        openPrice = v.findViewById(R.id.openPrice);
        chart = v.findViewById(R.id.chart);
        remove = v.findViewById(R.id.remove);

        try {
            JSONObject stockInfo = new JSONObject(getArguments().getString("stock"));
            symbol = stockInfo.getString("Symbol");
            curPrice.setText(stockInfo.getString("LastPrice"));
            openPrice.setText(stockInfo.getString("Open"));
            name.setText(stockInfo.getString("Name"));

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder((Context)parentActivity)
                            .setTitle("Title")
                            .setMessage("Do you really want to whatever?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    parentActivity.removeStock(name.getText().toString());
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

        } catch (JSONException e){
            e.printStackTrace();
        }

        chart.getSettings().setJavaScriptEnabled(true);
        chart.loadUrl("https://macc.io/lab/cis3515/?symbol="+symbol);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (detail) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }
}

interface detail{
    void removeStock(String name);
}