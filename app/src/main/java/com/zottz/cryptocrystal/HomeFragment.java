package com.zottz.cryptocrystal;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zottz.cryptocrystal.FavoriteFragmentPkg.Favorite;
import com.zottz.cryptocrystal.SetAlertActivity.AlertDB.MainDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    //Declare RecyclerView
    RecyclerView currenciesRV;
    //Declare Adapter
    CurrencyRVAdapter currencyRVAdapter;
    //Declare data which I want to show in RV
    public ArrayList<CurrencyRVModel> currencyRVModalArrayList;

    double rsiValue;

    ArrayList<String> favCurrencyList;

    public static ArrayList<CurrencyRVModel> favRVModelArraylist;
    ProgressBar loadingPB;
    //Declare ArrayList for Alert search
    public ArrayList<String> symbolArrayList;

    MainDatabase db;
    List<Favorite> favoriteItemList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        db = MainDatabase.getInstance(getActivity().getApplicationContext());
        favoriteItemList = db.favoriteDao().getAllFavorites();

        //PickUp Coin list for AlertPage2
        symbolArrayList = new ArrayList<String>();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        currenciesRV = view.findViewById(R.id.recyclerView);
        loadingPB = view.findViewById(R.id.idPBLoading);

        getRVData();
        getCurrencyDataNomics();


        return view;
    }

//    private void showToast(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }

    private void getRVData() {
        currenciesRV.setHasFixedSize(true);
        //loadingPB = findViewById(R.id.idPBLoading);
        currencyRVModalArrayList = new ArrayList<>();
        //Send data to Adapter // After this create sample single view Layout
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList, getContext());
        //Set the Adapter with RecyclerView
        currenciesRV.setAdapter(currencyRVAdapter);
        //Set Layout Manager to RecyclerView
        currenciesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        //FavRVModel Array List
        favRVModelArraylist = new ArrayList<>();


        //Set OnClick RV Item
        currencyRVAdapter.setOnItemClickListener(position -> {
            String adapterCurrencySymbol = currencyRVModalArrayList.get(position).getSymbol();
            String adapterCurrencyName = currencyRVModalArrayList.get(position).getName();
            String adapterCurrencyURL = currencyRVModalArrayList.get(position).getLogoURL();
            double adapterCurrencyPrice = currencyRVModalArrayList.get(position).getPrice();

            double adapterCurrencyPC1H = currencyRVModalArrayList.get(position).getPc1h();
            double adapterCurrencyPC24H = currencyRVModalArrayList.get(position).getPc24h();
            double adapterCurrencyPC7D = currencyRVModalArrayList.get(position).getPc7d();
            double adapterCurrencyCap = currencyRVModalArrayList.get(position).getCap();
            double adapterCurrencyVol = currencyRVModalArrayList.get(position).getVol();
            double adapterCurrencyRSI = currencyRVModalArrayList.get(position).getRsi();


            favCurrencyList = new ArrayList<>();

            //Save coin Name to Favorite DB Table
            Favorite favorite = new Favorite();
            List<Favorite> favoriteList = db.favoriteDao().getAllFavorites();

            for (int i = 0; i < favoriteList.size(); i++) {
                favCurrencyList.add(favoriteList.get(i).currencySymbol);
            }
            if (!favCurrencyList.contains(adapterCurrencySymbol)) {
                favorite.currencyName = adapterCurrencyName;
                favorite.currencySymbol = adapterCurrencySymbol;
                favorite.currencyIconURL = adapterCurrencyURL;
                favorite.currencyPrice = (float) adapterCurrencyPrice;
                favorite.pc1H = (float) adapterCurrencyPC1H;
                favorite.pc24H = (float) adapterCurrencyPC24H;
                favorite.pc7D = (float) adapterCurrencyPC7D;
                favorite.currencyCap = (float) adapterCurrencyCap;
                favorite.currencyVol = (float) adapterCurrencyVol;
                favorite.currencyRSI = (float) adapterCurrencyRSI;

                db.favoriteDao().insertFavorite(favorite);
                Toast.makeText(getContext(), adapterCurrencySymbol + " is added to Favorite List", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), adapterCurrencySymbol + " is already in Favorite List", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCurrencyDataNomics() {

        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.nomics.com/v1/currencies/ticker?key=ecae4f8ae82014deed75f16f14d03f2c21a819b1&interval=1h,1d,7d&per-page=100&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

            loadingPB.setVisibility(View.GONE);
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject dataObj = response.getJSONObject(i);
                    String name = dataObj.getString("name");
                    String symbol = dataObj.getString("symbol");
                    double price = dataObj.getDouble("price");
                    String urlString = dataObj.getString("logo_url");
                    double marketCap = dataObj.getDouble("market_cap");

                    JSONObject pc1hObj = dataObj.getJSONObject("1h");
                    double pc1h = pc1hObj.getDouble("price_change_pct") * 100;


                    JSONObject pc24hObj = dataObj.getJSONObject("1d");
                    double pc24h = pc24hObj.getDouble("price_change_pct") * 100;
                    double volume = pc24hObj.getDouble("volume");

                    JSONObject pc7dObj = dataObj.getJSONObject("7d");
                    double pc7d = pc7dObj.getDouble("price_change_pct") * 100;







                    //Getting RSI Value
                    //double rsiValue = 0.0;
                    getRsiData(symbol);
                    //Log.i(TAG, "getRsiData in Nomics: " + symbol + ":  " + rsiValue);

                    currencyRVModalArrayList.add(new CurrencyRVModel(symbol, name, urlString, price, pc1h, pc24h, pc7d, marketCap, volume));
                    symbolArrayList.add(symbol);


                    ArrayList<String> favCurrencyList = new ArrayList<>();
                    Favorite favorite = new Favorite();
                    List<Favorite> favoriteList = db.favoriteDao().getAllFavorites();


                    //Make a list of DB Symbols
                    for (int k = 0; k < favoriteList.size(); k++) {
                        favCurrencyList.add(favoriteList.get(k).currencySymbol);
                    }
                    //If symbol is in DB then update Symbol currency info to DB
                    if (favCurrencyList.contains(symbol)) {
                        favorite.favoriteID = favCurrencyList.indexOf(symbol) + 1;
                        favorite.currencyName = name;
                        favorite.currencySymbol = symbol;
                        favorite.currencyPrice = (float) price;
                        favorite.currencyIconURL = urlString;
                        favorite.pc1H = (float) pc1h;
                        favorite.pc24H = (float)  pc24h;
                        favorite.pc7D = (float) pc7d;
                        favorite.currencyCap = (float) marketCap;
                        favorite.currencyVol = (float) volume;



                        db.favoriteDao().updateFavorite(favorite);
                    }

                }


                currencyRVAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Fail to extract json data..", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            loadingPB.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Fail to get the data..", Toast.LENGTH_SHORT).show();
        });

        requestQueue.add(jsonArrayRequest);
    }











    private void getRsiData(String symbol) {

        ArrayList<String> favCurrencyListnew = new ArrayList<>();
        //Save coin Name to Favorite DB Table
        Favorite favorite = new Favorite();
        List<Favorite> favoriteList = db.favoriteDao().getAllFavorites();

        for (int i = 0; i < favoriteList.size(); i++) {
            favCurrencyListnew.add(favoriteList.get(i).currencySymbol);
        }
        if (favCurrencyListnew.contains(symbol)) {
            try {
                String rsiUrl = "https://api.taapi.io/rsi?secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImF6dWJhaXIxMjNAZ21haWwuY29tIiwiaWF0IjoxNjM5MTY5MDg4LCJleHAiOjc5NDYzNjkwODh9.DUufwGln3wS7y_qAyaVIu_CvXVRxLczpJUT96Z6lpEc&exchange=binance&symbol=" + symbol + "/USDT&interval=1h";
                RequestQueue rsiRequestQueue = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, rsiUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "Total Response is: " + response);


                        try {
                            double rsi = response.getDouble("value");
                            //Update RSI value to database
                            db.favoriteDao().updateRSIValue(rsi, symbol);
                                    Log.i(TAG, "getRsiData in try: " + symbol+ rsiValue);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            double rsi = 1.23;
                            //finalRSI = rsi;
                            Log.i(TAG, "RSi Not Found: " + rsi);
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getContext(), "Fail to get RSI data..", Toast.LENGTH_SHORT).show();
                    }
                });
                {

                    rsiRequestQueue.add(jsonObjectRequest);
                }
            } catch (Exception e) {

            }

        }

    }

}
