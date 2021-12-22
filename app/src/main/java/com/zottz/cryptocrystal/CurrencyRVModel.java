package com.zottz.cryptocrystal;

public class CurrencyRVModel {

    String symbol, name, logoURL;
    double price, pc1h, pc24h, pc7d, cap, vol, rsi;

    public double getPc24h() {
        return pc24h;
    }

    public double getPc7d() {
        return pc7d;
    }

    public double getCap() {
        return cap;
    }

    public double getVol() {
        return vol;
    }

    public double getRsi(){
        return rsi;
    }

    //    protected CurrencyRVModel(Parcel in) {
//        symbol = in.readString();
//        name = in.readString();
//        price = in.readDouble();
//        pc1h = in.readDouble();
//        logoURL = in.readString();
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(symbol);
//        dest.writeString(name);
//        dest.writeDouble(price);
//        dest.writeDouble(pc1h);
//        dest.writeString(logoURL);
//    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }

//    public static final Creator<CurrencyRVModel> CREATOR = new Creator<CurrencyRVModel>() {
//        @Override
//        public CurrencyRVModel createFromParcel(Parcel in) {
//            return new CurrencyRVModel(in);
//        }
//
//        @Override
//        public CurrencyRVModel[] newArray(int size) {
//            return new CurrencyRVModel[size];
//        }
//    };

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPc1h() {
        return pc1h;
    }

    public void setPc1h(double pc1h) {
        this.pc1h = pc1h;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public CurrencyRVModel(String symbol, String name, double price, double pc1h) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.pc1h = pc1h;
    }

    public CurrencyRVModel(String symbol, String name, String logoURL, double price) {
        this.symbol = symbol;
        this.name = name;
        this.logoURL = logoURL;
        this.price = price;
    }

    public CurrencyRVModel(String symbol, String name, String logoURL, double price, double pc1h, double pc24h, double pc7d, double cap, double vol, double rsi) {
        this.symbol = symbol;
        this.name = name;
        this.logoURL = logoURL;
        this.price = price;
        this.pc1h = pc1h;
        this.pc24h = pc24h;
        this.pc7d = pc7d;
        this.cap = cap;
        this.vol = vol;
        this.rsi = rsi;
    }

    public CurrencyRVModel(String symbol, String name, String logoURL, double price, double pc1h, double pc24h, double pc7d, double cap, double vol) {
        this.symbol = symbol;
        this.name = name;
        this.logoURL = logoURL;
        this.price = price;
        this.pc1h = pc1h;
        this.pc24h = pc24h;
        this.pc7d = pc7d;
        this.cap = cap;
        this.vol = vol;
    }
}
