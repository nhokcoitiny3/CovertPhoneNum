package com.example.tiny.covertphonenum.presenter.adapter;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhokc.convertphonenum.databinding.ItemRcvBinding;

import com.example.nhokc.convertphonenum.R;
import com.example.tiny.covertphonenum.model.models.Prefix;
import com.example.tiny.covertphonenum.presenter.impl.IOnRecyclerViewItemClickListener;
import com.example.tiny.covertphonenum.presenter.impl.IOnRecyclerViewItemLongClickListener;

public class AdapterRcvPrefix extends RecyclerView.Adapter<AdapterRcvPrefix.ViewHolder> {
    private IOnRecyclerViewItemClickListener listener;
    private IOnRecyclerViewItemLongClickListener longClickListener;
    private IPrefix iPrefix;


    public void setIdata(IPrefix iPrefix) {
        this.iPrefix = iPrefix;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRcvBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_rcv, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Prefix prefix = iPrefix.gePrefix(position);
        holder.binding.txtOld.setText(prefix.getOldPRe());
        holder.binding.txtNew.setText(prefix.getNewPre());
        holder.itemView.setLongClickable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecyclerViewItemClicked(position, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.iPrefix.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRcvBinding binding;

        ViewHolder(ItemRcvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public interface IPrefix {
        int getCount();

        Prefix gePrefix(int position);
    }

    public void setOnItemClickListener(IOnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }


}
