package edu.temple.stocktracker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class stockListAdapter extends ArrayAdapter {

    Context con;
    ArrayList<String> stockList;


    public stockListAdapter(Context context, ArrayList<String> stocks) {
        super(context, 0, stocks);
        this.con = context;
        this.stockList = stocks;
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return super.getPosition(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return stockList.get(position);
    }

    @Override
    public int getCount() {
        if(stockList != null) {
            return stockList.size();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        JSONObject stockInfo;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.portfolio_details, parent, false);
        }

        try {
            stockInfo = new JSONObject(stockList.get(position));

            if (stockInfo.getString("Change").contains("-")){
                v.setBackgroundColor(Color.RED);
            } else {
                v.setBackgroundColor(Color.GREEN);
            }

            TextView stockSymbol = v.findViewById(R.id.symbol);
            TextView price = v.findViewById(R.id.price);

            stockSymbol.setText(stockInfo.getString("Symbol"));
            price.setText(stockInfo.getString("LastPrice"));

        } catch (JSONException e){
            e.printStackTrace();
        }


        return v;
    }


}
