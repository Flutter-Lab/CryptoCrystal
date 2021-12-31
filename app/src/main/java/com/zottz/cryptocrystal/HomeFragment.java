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

                    currencyRVModalArrayList.add(new CurrencyRVModel(symbol, name, urlString, price, pc1h, pc24h, pc7d, marketCap, volume));
                    symbolArrayList.add(symbol);
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

}
