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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zottz.cryptocrystal.FavoriteFragmentPkg.Favorite;
import com.zottz.cryptocrystal.SetAlertActivity.AlertDB.MainDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    RecyclerView favoriteRV;
    CurrencyRVAdapter favoriteRVAdapter;
    ProgressBar favPB;

    public ArrayList<CurrencyRVModel> favArrayList;

    MainDatabase db;
    List<Favorite> favoriteItemList;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favPB = view.findViewById(R.id.favPBLoading);
        favPB.setVisibility(View.VISIBLE);
        favArrayList = new ArrayList<>();


        db = MainDatabase.getInstance(getActivity().getApplicationContext());
        favoriteItemList = db.favoriteDao().getAllFavorites();
        for (int j = 0; j<favoriteItemList.size(); j++){
            String symbol = favoriteItemList.get(j).currencySymbol;
            sendRSIvalue(symbol);
            favPB.setVisibility(View.GONE);
        }

        favoriteRV = view.findViewById(R.id.favRecyclerView);

        //Ready Favorite currency model arraylist
        for (int i = 0; i < favoriteItemList.size(); i++){
            String symbol = favoriteItemList.get(i).currencySymbol;
            String name = favoriteItemList.get(i).currencyName;
            String urlString = favoriteItemList.get(i).currencyIconURL;
            float price = favoriteItemList.get(i).currencyPrice;
            float pc1h = favoriteItemList.get(i).pc1H;
            float pc24h = favoriteItemList.get(i).pc24H;
            float pc7d = favoriteItemList.get(i).pc7D;
            float curCap = favoriteItemList.get(i).currencyCap;
            float curVol = favoriteItemList.get(i).currencyVol;

            float rsiCur = db.favoriteDao().getRSIvalueFromDB(symbol);

            favArrayList.add(new CurrencyRVModel(symbol, name, urlString, price, pc1h, pc24h, pc7d, curCap, curVol, rsiCur));

        }

        //Set RV Data
        favoriteRV.setHasFixedSize(true);
        //homeFragment.favRVModelArraylist = new ArrayList<CurrencyRVModel>();
        favoriteRVAdapter = new CurrencyRVAdapter(favArrayList, getContext());
        favoriteRV.setAdapter(favoriteRVAdapter);
        favoriteRV.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteRVAdapter.notifyDataSetChanged();



        //Set On Click >> Delete
        favoriteRVAdapter.setOnItemClickListener(position -> {

            String symbol = favArrayList.get(position).getSymbol();
            db.favoriteDao().deleteBySymbol(symbol);
            favArrayList.remove(position);
            favoriteRVAdapter.notifyDataSetChanged();

            Toast.makeText(getContext(), symbol+" is Deleted From Favorite", Toast.LENGTH_SHORT).show();

        });

        return view;
    }

    private void sendRSIvalue(String symbol){
        String rsiUrl = "https://api.taapi.io/rsi?secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImF6dWJhaXIxMjNAZ21haWwuY29tIiwiaWF0IjoxNjQwNjI3MDE3LCJleHAiOjc5NDc4MjcwMTd9.Tmi0_jfrGIfqKBCRvI14Pozg1IP52cY66lqtzDR2MG0&exchange=binance&symbol="+symbol+"/USDT&interval=1h";
        RequestQueue rsiRequestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, rsiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i(TAG, "Total Response is: " + response);


                try {
                    double rsi = response.getDouble("value");
                    //Update RSI value to database
                    db.favoriteDao().updateRSIValue(rsi, symbol);
                    Log.i(TAG, "getRsiData in try: " + symbol+": "+ rsi);
                    //favPB.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "RSi Not Found: ");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        {

            rsiRequestQueue.add(jsonObjectRequest);
        }

    }


}
