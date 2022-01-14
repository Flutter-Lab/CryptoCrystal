package com.zottz.cryptocrystal.AllCoinDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AllCoinDao {

    @Query("SELECT * FROM AllCoin")
    List<AllCoin> getAllCoinList();

    @Insert
    void insertCoin(AllCoin... allCoins);


    @Delete
    void deleteFavorite(AllCoin allCoin);

    @Update
    void updateFavorite(AllCoin allCoin);

    @Query("DELETE FROM AllCoin WHERE coinID = :coinID")
    void deleteByFavId(long coinID);

    @Query("DELETE FROM AllCoin WHERE currency_symbol = :symbol")
    void deleteBySymbol(String symbol);

    @Query("UPDATE AllCoin SET currency_rsi = :rsiValue WHERE currency_symbol = :symbol")
    void updateRSIValue(double rsiValue, String symbol);

    @Query("UPDATE AllCoin SET currency_price = :currencyPrice, pc_1h = :pc1H, pc_24h = :pc24H, pc_7d = :pc_7d, currency_cap = :currency_cap, currency_vol = :currency_vol  WHERE currency_symbol = :symbol")
    void updateCoinRow(double currencyPrice, double pc1H, double pc24H, double pc_7d, double currency_cap, double currency_vol,  String symbol);
}
