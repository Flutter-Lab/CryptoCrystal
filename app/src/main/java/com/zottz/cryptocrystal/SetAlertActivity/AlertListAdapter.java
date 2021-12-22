package com.zottz.cryptocrystal.SetAlertActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.zottz.cryptocrystal.MainActivity;
import com.zottz.cryptocrystal.R;
import com.zottz.cryptocrystal.SetAlertActivity.AlertDB.Alert;

import java.util.List;

public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.MyViewHolder> {



   private OnItemClickListener mListener;
    private Context context;
    private List<Alert> alertList;
    public AlertListAdapter(Context context){
        this.context = context;
    }

    //Swipe related variable
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public void setAlertList(List<Alert> alertList){
        this.alertList = alertList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;

    }

    public Alert getAlertAt(int position){
        return alertList.get(position);
    }


    @NonNull
    @Override
    public AlertListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.alert_rv_sample, parent, false);

        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertListAdapter.MyViewHolder holder, int position) {

        //Swipe Related code
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(alertList.get(position).currencySymbol));
        viewBinderHelper.closeLayout(String.valueOf(alertList.get(position).currencySymbol));

        holder.symbolTV.setText(this.alertList.get(position).currencySymbol);
        //double alertValueDouble = (double)this.alertList.get(position).alertValue;
        double alertValueDouble = this.alertList.get(position).alertValue;


        if (this.alertList.get(position).alertTypeCode < 2){
            holder.alertTypeNameTV.setText(this.alertList.get(position).alertType + " $" + MainActivity.dynDF(alertValueDouble).format(alertValueDouble));
        } else {
            holder.alertTypeNameTV.setText(this.alertList.get(position).alertType + " " + MainActivity.df2.format(alertValueDouble)+"%");
        }

        if (this.alertList.get(position).alertTypeCode %2 == 0){
            holder.alertIndicator.setImageResource(R.drawable.ic_alert_up);
        } else {
            holder.alertIndicator.setImageResource(R.drawable.ic_alert_down);
        }

        if (this.alertList.get(position).isLoudAlert){
            holder.loudAlertText.setText("Loud Alert");
        }





    }

    @Override
    public int getItemCount() {
        return this.alertList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView symbolTV;
        TextView alertTypeNameTV;
        ImageView alertIndicator;
        TextView loudAlertText;

        private SwipeRevealLayout swipeRevealLayout;
        private TextView removeFromAlertTxt;

        public MyViewHolder(View view, OnItemClickListener listener){
            super(view);
            symbolTV = view.findViewById(R.id.symbolTextView);
            alertTypeNameTV = view.findViewById(R.id.alertTypeNameTV);
            alertIndicator = view.findViewById(R.id.alertIndicator);
            loudAlertText = view.findViewById(R.id.loudAlertText);

            swipeRevealLayout = view.findViewById(R.id.swipe_layout_alert);
            removeFromAlertTxt = view.findViewById(R.id.txtToRemoveAlert);
            

            removeFromAlertTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
//                            String symbol = currencyRVModelArrayList.get(position).getSymbol();
                            try {
                                viewBinderHelper.closeLayout(String.valueOf(alertList.get(position).currencySymbol));
                            } catch (Exception e){

                            }
                        }
                    }
                }
            });


        }
    }
}
