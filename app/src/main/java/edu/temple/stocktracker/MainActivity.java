package edu.temple.stocktracker;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements stockBoy, portfolio, detail {

    FloatingActionButton fab;
    final String fileName = "stockDataList";
    final String NOMATCH = "No symbol matches";
    static ArrayList<String> stockStringList;
    FragmentManager fm = getSupportFragmentManager();
    private static Thread stockSymbolThread;
    boolean singlePane;
    TextView newb;
    long delay = 1000*60*5;
    private Handler handler = new Handler();
    private ScheduledExecutorService scheduleTaskExecutor;


private Runnable runnable = new Runnable() {

        @Override
        public void run() {

            for(int i=0; i<stockStringList.size(); i++){
                try {
                    JSONObject  stock = new JSONObject(stockStringList.get(i));
                    String urlString = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol="+stock.getString("Symbol");

                    String queryResult = getStockData(urlString);

                    if(queryResult != stockStringList.get(i)){
                        stockStringList.set(i, queryResult);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                writer.write("");
                writer.close();

                FileOutputStream fos2 = openFileOutput("stockDataList", MODE_APPEND | MODE_PRIVATE);
                BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(fos2));

                for (int i = 0; i < stockStringList.size(); i++) {
                    writer2.write(stockStringList.get(i));
                }
                writer2.close();

                for (int i = 0; i < stockStringList.size(); i++) {
                    Log.d("data !", stockStringList.get(i));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            stockStringList = readFile();

            handler.postDelayed(this, 10000);
        }
    };



    final Handler stockHandler = new Handler(){

        public void handleMessage(Message msg) {
            Bundle b;
            if(msg.what==1){

                b=msg.getData();

                StringBuilder sb = new StringBuilder(b.getCharSequence("Key").length());
                sb.append(b.getCharSequence("Key"));

                String data = sb.toString();

                if(data.contains(NOMATCH) || !data.contains("SUCCESS")){
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.noMatch), Toast.LENGTH_LONG).show();
                } else {
                    if(data.contains("SUCCESS")) {
                        try {

                            FileOutputStream fos = openFileOutput(fileName, MODE_APPEND | MODE_PRIVATE);
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                            writer.write(data);
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stockStringList = readFile();
                        stockListFrag();
                    }
                }
                Log.d("data !", data);

            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newb = findViewById(R.id.newb);
        singlePane = findViewById(R.id.detail) == null;
        fab = findViewById(R.id.fab);
        fab.setAlpha(0.5f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStock();
            }

        });
        stockListFrag();
        if(stockStringList.size() == 0){
            newb.setText(getResources().getString(R.string.newUser));
            newb.setTextSize(32);
        }

        scheduleTaskExecutor= Executors.newScheduledThreadPool(5);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

                for(int i=0; i<stockStringList.size(); i++){
                    try {
                        JSONObject  stock = new JSONObject(stockStringList.get(i));
                        String urlString = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol="+stock.getString("Symbol");

                        String queryResult = getStockData(urlString);

                        if(queryResult != stockStringList.get(i)){
                            stockStringList.set(i, queryResult);
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                    loadRewrite();

                // If you need update UI, simply do this:
                runOnUiThread(new Runnable() {
                    public void run() {
                        // update your UI component here.
                    stockListFrag();
                    }
                });
            }
        }, 0, delay, TimeUnit.MILLISECONDS);



//        handler.postDelayed(runnable, 10000);



    } // end of onCreate()


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(stockSymbolThread!=null){
            stockSymbolThread.interrupt();
            stockSymbolThread = null;
        }
//        if(timerThread!=null){
//            timerThread.interrupt();
//            timerThread = null;
//        }

//        handler.removeCallbacks(runnable);
    }

    public void stockListFrag(){
        stockStringList = readFile();
        Bundle b = new Bundle();
        b.putStringArrayList("stocks" ,stockStringList);
        portfolioFragment pf = new portfolioFragment();
        pf.setArguments(b);
        fm.beginTransaction()
                .replace(R.id.port, pf)
                .commitAllowingStateLoss();

        if(!singlePane){
            if(stockStringList.size() != 0) {
                Bundle b2 = new Bundle();
                b2.putString("stock", stockStringList.get(0));
                stockDetails sd = new stockDetails();
                sd.setArguments(b2);
                fm.beginTransaction()
                        .replace(R.id.detail, sd)
                        .commitAllowingStateLoss();
            }
        }
    }

    public void loadRewrite(){
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write("");
            writer.close();

            FileOutputStream fos2 = openFileOutput("stockDataList", MODE_APPEND | MODE_PRIVATE);
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(fos2));

            for (int i = 0; i < stockStringList.size(); i++) {
                writer2.write(stockStringList.get(i));
            }
            writer2.close();

//                    for (int i = 0; i < stockStringList.size(); i++) {
//                        Log.d("data !", stockStringList.get(i));
//                    }

        } catch (Exception e) {
            e.printStackTrace();
        }
        stockStringList = readFile();
    }


    public ArrayList<String> readFile(){

        ArrayList<String> stocks = new ArrayList<>();
        try {
            BufferedReader input = null;
            input = new BufferedReader(
                    new InputStreamReader(openFileInput(fileName)));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                stocks.add(line);
                buffer.append(line + "\n");
            }
            String text = buffer.toString();
//            Log.d("MEEEE2222", stocks.get(1));

        } catch (Exception e){
            e.printStackTrace();
        }finally {
            return stocks;
        }

    }

    public void addStock() {
        addStockDialogFragment stockDialog = new addStockDialogFragment().newInstance();
        stockDialog.show(fm, "meh");
    }

    @Override
    public void getStockSymbol(String symbol) {
        final String urlString = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol;

        boolean match = false;
        if (stockStringList.size() != 0){
            for (int i = 0; i < stockStringList.size(); i++) {
                if (stockStringList.get(i).contains(symbol.toUpperCase())) {
                    match = true;
                }
            }
    }
        if(match){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.exists), Toast.LENGTH_LONG).show();
        } else {
            newb.setVisibility(View.INVISIBLE);
            stockSymbolThread = new Thread() {
                @Override
                public void run() {
                    Bundle b = new Bundle();
                    b.putCharSequence("Key", getStockData(urlString));
                    Message msg = stockHandler.obtainMessage();
                    msg.what = 1;
                    msg.setData(b);
                    stockHandler.sendMessage(msg);
                }
            };
            stockSymbolThread.start();
        }

            }


    public String getStockData(String urlString){

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
//                Log.d("Response: ", "> " + line);

            }
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    @Override
    public void callDetail(int position) {
        Bundle b = new Bundle();
        b.putString("stock" ,stockStringList.get(position));
        stockDetails sd = new stockDetails();
        sd.setArguments(b);

        if(!singlePane){
            fm.beginTransaction()
                    .replace(R.id.detail, sd)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.port, sd)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void removeStock(String name) {
        for (int i = 0; i < stockStringList.size(); i++) {
            if (stockStringList.get(i).contains(name)) {
                stockStringList.remove(i);
                loadRewrite();
                stockListFrag();
            }
            }
    }
}
