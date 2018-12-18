package edu.temple.stocktracker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class portfolioFragment extends Fragment {

    stockListAdapter sla;
    ListView stockList;
    ArrayList<String> stockStringList;
    Context con;
    private portfolio parentActivity;


    public portfolioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con = context;
        parentActivity = (portfolio) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_portfolio, container, false);

        stockStringList = getArguments().getStringArrayList("stocks");
        stockList = v.findViewById(R.id.stockList);

        sla = new stockListAdapter(con, stockStringList);
        stockList.setAdapter(sla);
        stockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parentActivity.callDetail(position);
            }
        });

              return v;
    }

}
interface portfolio {
    void callDetail(int position);
}
