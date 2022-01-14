package com.zottz.cryptocrystal.SetAlertActivity.AlertDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zottz.cryptocrystal.AllCoinDB.AllCoin;
import com.zottz.cryptocrystal.AllCoinDB.AllCoinDao;
import com.zottz.cryptocrystal.FavoriteFragmentPkg.Favorite;
import com.zottz.cryptocrystal.FavoriteFragmentPkg.FavoriteDao;

@Database(entities = {Alert.class, Favorite.class, AllCoin.class}, version = 1)
public abstract class MainDatabase extends RoomDatabase {

    public abstract AlertDao alertDao();
    public abstract FavoriteDao favoriteDao();
    public abstract AllCoinDao allCoinDao();

    private static MainDatabase INSTANCE;

    public static MainDatabase getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MainDatabase.class, "Main_DB")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }


}
