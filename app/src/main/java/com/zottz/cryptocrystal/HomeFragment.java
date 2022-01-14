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
import com.zottz.cryptocrystal.AllCoinDB.AllCoin;
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

    List<AllCoin> allCoinList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        getAllCoinListFromDB();
        getCurrencyDataNomics();


        return view;
    }


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
//        currencyRVAdapter.setOnItemClickListener(position -> {
//            String adapterCurrencySymbol = currencyRVModalArrayList.get(position).getSymbol();
//            String adapterCurrencyName = currencyRVModalArrayList.get(position).getName();
//            String adapterCurrencyURL = currencyRVModalArrayList.get(position).getLogoURL();
//            double adapterCurrencyPrice = currencyRVModalArrayList.get(position).getPrice();
//            double adapterCurrencyPC1H = currencyRVModalArrayList.get(position).getPc1h();
//            double adapterCurrencyPC24H = currencyRVModalArrayList.get(position).getPc24h();
//            double adapterCurrencyPC7D = currencyRVModalArrayList.get(position).getPc7d();
//            double adapterCurrencyCap = currencyRVModalArrayList.get(position).getCap();
//            double adapterCurrencyVol = currencyRVModalArrayList.get(position).getVol();
//            double adapterCurrencyRSI = currencyRVModalArrayList.get(position).getRsi();
//
//
//            favCurrencyList = new ArrayList<>();
//
//            //Save coin Name to Favorite DB Table
//            Favorite favorite = new Favorite();
//            List<Favorite> favoriteList = db.favoriteDao().getAllFavorites();
//
//            for (int i = 0; i < favoriteList.size(); i++) {
//                favCurrencyList.add(favoriteList.get(i).currencySymbol);
//            }
//            if (!favCurrencyList.contains(adapterCurrencySymbol)) {
//                favorite.currencyName = adapterCurrencyName;
//                favorite.currencySymbol = adapterCurrencySymbol;
//                favorite.currencyIconURL = adapterCurrencyURL;
//                favorite.currencyPrice = (float) adapterCurrencyPrice;
//                favorite.pc1H = (float) adapterCurrencyPC1H;
//                favorite.pc24H = (float) adapterCurrencyPC24H;
//                favorite.pc7D = (float) adapterCurrencyPC7D;
//                favorite.currencyCap = (float) adapterCurrencyCap;
//                favorite.currencyVol = (float) adapterCurrencyVol;
//                favorite.currencyRSI = (float) adapterCurrencyRSI;
//
//                db.favoriteDao().insertFavorite(favorite);
//                Toast.makeText(getContext(), adapterCurrencySymbol + " is added to Favorite List", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getContext(), adapterCurrencySymbol + " is already in Favorite List", Toast.LENGTH_SHORT).show();
//            }
//        });
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
                    //getRsiData(symbol);
                    //Log.i(TAG, "getRsiData in Nomics: " + symbol + ":  " + rsiValue);

                    currencyRVModalArrayList.add(new CurrencyRVModel(symbol, name, urlString, price, pc1h, pc24h, pc7d, marketCap, volume));
                    symbolArrayList.add(symbol);



                    //Insert / Upate data to All Coin List Table (Database)
                    ArrayList<String> allCoinList = new ArrayList<>();
                    AllCoin allCoin = new AllCoin();
                    List<AllCoin> allCoinListDB = db.allCoinDao().getAllCoinList();
                    for(int l = 0; l < allCoinListDB.size(); l++){
                        allCoinList.add(allCoinListDB.get(l).currencySymbol);
                    }

                    if (allCoinList.contains(symbol)){
                        db.allCoinDao().updateCoinRow(price, pc1h, pc24h, pc7d, marketCap, volume, symbol);
                    } else{
                        allCoin.currencyName = name;
                        allCoin.currencySymbol = symbol;
                        allCoin.currencyPrice = (float) price;
                        allCoin.currencyIconURL = urlString;
                        allCoin.pc1H = (float) pc1h;
                        allCoin.pc24H = (float)  pc24h;
                        allCoin.pc7D = (float) pc7d;
                        allCoin.currencyCap = (float) marketCap;
                        allCoin.currencyVol = (float) volume;
                        
                        db.allCoinDao().insertCoin(allCoin);
                    }

                    getAllCoinListFromDB();







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
                Toast.makeText(getActivity(), "Fail to extract json data..", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            loadingPB.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "Fail to get the data..", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "getCurrencyDataNomics: Fail to get the data..");
        });

        requestQueue.add(jsonArrayRequest);
    }



    private void getAllCoinListFromDB(){
        allCoinList = db.allCoinDao().getAllCoinList();
        currencyRVModalArrayList = new ArrayList<>();

        for (int i = 0; i < allCoinList.size(); i++){
            String symbol = allCoinList.get(i).currencySymbol;
            String name = allCoinList.get(i).currencyName;
            String urlString = allCoinList.get(i).currencyIconURL;
            float price = allCoinList.get(i).currencyPrice;
            float pc1h = allCoinList.get(i).pc1H;
            float pc24h = allCoinList.get(i).pc24H;
            float pc7d = allCoinList.get(i).pc7D;
            float curCap = allCoinList.get(i).currencyCap;
            float curVol = allCoinList.get(i).currencyVol;
            float curRsi = allCoinList.get(i).currencyRSI;

            currencyRVModalArrayList.add(new CurrencyRVModel(symbol, name, urlString, price, pc1h, pc24h, pc7d, curCap, curVol, curRsi));
        }

        //Set RV Data
        currenciesRV.setHasFixedSize(true);
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList, getContext());
        currenciesRV.setAdapter(currencyRVAdapter);
        currenciesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        currencyRVAdapter.notifyDataSetChanged();

        currencyRVAdapter.setOnItemClickListener(position -> {
          setOnClick(position);
        });
    }

    private void setOnClick(int position){
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

    }

}
