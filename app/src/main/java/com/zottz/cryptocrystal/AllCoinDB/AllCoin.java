package com.zottz.cryptocrystal.AllCoinDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AllCoin {
    @PrimaryKey(autoGenerate = true)
    public int coinID;

    @ColumnInfo(name = "currency_symbol")
    public String currencySymbol;

    @ColumnInfo(name = "currency_name")
    public String currencyName;

    @ColumnInfo(name = "currency_price")
    public float currencyPrice;

    @ColumnInfo(name = "currency_icon_url")
    public String currencyIconURL;


    //New Items
    @ColumnInfo(name = "pc_1h")
    public float pc1H;

    @ColumnInfo(name = "pc_24h")
    public float pc24H;

    @ColumnInfo(name = "pc_7d")
    public float pc7D;

    @ColumnInfo(name = "currency_cap")
    public float currencyCap;

    @ColumnInfo(name = "currency_vol")
    public float currencyVol;

    @ColumnInfo(name = "currency_rsi")
    public float currencyRSI;



}
