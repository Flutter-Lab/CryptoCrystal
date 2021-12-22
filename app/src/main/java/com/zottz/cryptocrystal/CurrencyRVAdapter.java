package com.zottz.cryptocrystal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.zottz.cryptocrystal.SetAlertActivity.AlertDB.MainDatabase;

import java.util.ArrayList;


public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyRVAdapter.MyViewHolder> {

    MainActivity mainActivity = new MainActivity();



    private OnItemClickListener mListener;

    //Mention data which I want to show in RV
    CurrencyRVModel currencyRVModel;
    ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    Context context;

    //Swipe related variable
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    //Generated Constructor for all Data
    public CurrencyRVAdapter(ArrayList<CurrencyRVModel> currencyRVModalArrayList, Context context) {
        this.currencyRVModelArrayList = currencyRVModalArrayList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate sample layout
        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout, parent, false);

        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MainDatabase db = MainDatabase.getInstance(context.getApplicationContext());

        // Set all data into views // Which data we got from MyViewHolder
        currencyRVModel = currencyRVModelArrayList.get(position);


        //Swipe Related code
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(currencyRVModelArrayList.get(position).getSymbol()));
        viewBinderHelper.closeLayout(String.valueOf(currencyRVModelArrayList.get(position).getSymbol()));

        holder.symbolTextView.setText(currencyRVModel.getSymbol());
        holder.nameTextView.setText(currencyRVModel.getName());
        holder.priceTextView.setText(String.format("$%s", mainActivity.dynDF(currencyRVModel.getPrice()).format(currencyRVModel.getPrice())));

        holder.pc1hTextView.setText(String.format("%s%%", MainActivity.df2.format(currencyRVModel.getPc1h())));
        setTVcolor(holder.pc1hTextView, currencyRVModel.getPc1h());

        holder.pc1dTextView.setText(String.format("%s%%", MainActivity.df2.format(currencyRVModel.getPc24h())));
        setTVcolor(holder.pc1dTextView, currencyRVModel.getPc24h());

        holder.pc7dTextView.setText(String.format("%s%%", MainActivity.df2.format(currencyRVModel.getPc7d())));
        setTVcolor(holder.pc7dTextView, currencyRVModel.getPc7d());

        holder.capTextView.setText(mainActivity.bigValueCurrency(currencyRVModel.getCap()));

        holder.volumeTextView.setText(mainActivity.bigValueCurrency(currencyRVModel.getVol()));

        if (currencyRVModelArrayList.size() < 80){
            holder.rsiValueTextView.setVisibility(View.VISIBLE);
            holder.rsiTextView.setVisibility(View.VISIBLE);
            holder.rsiValueTextView.setText(MainActivity.df2.format(currencyRVModel.getRsi()));
        }






        //Load Coin Logo Image to RecycleView
        String urlString = currencyRVModel.getLogoURL();
        Glide.with(context).load(urlString).into(holder.logoImage);


    }

    @Override
    public int getItemCount() {
        return currencyRVModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        //Declare variable fro Views // Which we will see on sample_layout
        TextView symbolTextView, nameTextView, priceTextView, pc1hTextView, pc1dTextView, pc7dTextView, capTextView, volumeTextView, rsiValueTextView, rsiTextView;
        ImageView logoImage;

        private SwipeRevealLayout swipeRevealLayout;
        private TextView addToFavTextview;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            // Find all views from given itemView / We got sample_layout as itemView from onCreateViewHolder using Inflater
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            logoImage = itemView.findViewById(R.id.logo_imageView);
            pc1hTextView = itemView.findViewById(R.id.pc1HTextView);
            pc1dTextView = itemView.findViewById(R.id.pc1DTextView);
            pc7dTextView = itemView.findViewById(R.id.pc7dTextView);
            capTextView = itemView.findViewById(R.id.capTextView);
            volumeTextView = itemView.findViewById(R.id.volumeTextView);
            rsiValueTextView = itemView.findViewById(R.id.rsiValueTextView);
            rsiTextView = itemView.findViewById(R.id.rsiTextView);


            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            addToFavTextview = itemView.findViewById(R.id.txtAddToFav);

            if (currencyRVModelArrayList.size() < 95) {
                addToFavTextview.setText("Remove favorite");
                addToFavTextview.setBackgroundResource(R.color.red);
            } else {
                addToFavTextview.setBackgroundResource(R.color.green);
            }


            //Swipe button click listener
            addToFavTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
//                            String symbol = currencyRVModelArrayList.get(position).getSymbol();
                            try {
                                viewBinderHelper.closeLayout(String.valueOf(currencyRVModelArrayList.get(position).getSymbol()));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            });


        }
    }

    private void setTVcolor(TextView textView, double value ){
        if (value < 0){
            //textView.setTextColor(Color.parseColor("#FFF"));
            textView.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
    }





}
