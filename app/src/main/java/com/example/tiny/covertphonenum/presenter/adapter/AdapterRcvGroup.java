package com.example.tiny.covertphonenum.presenter.adapter;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.nhokc.convertphonenum.R;
import com.example.nhokc.convertphonenum.databinding.ItemGroupBinding;
import com.example.tiny.covertphonenum.model.models.Group;
import com.example.tiny.covertphonenum.presenter.impl.IOnRecyclerViewItemClickListener;
import com.example.tiny.covertphonenum.presenter.impl.IOnRecyclerViewItemLongClickListener;

public class AdapterRcvGroup extends RecyclerView.Adapter<AdapterRcvGroup.ViewHolder> {
    private IOnRecyclerViewItemClickListener listener;
    private ItemGroupBinding binding;
    private IOnRecyclerViewItemLongClickListener longClickListener;
    private IGroup iGroup;


    public void setIdataGroup(IGroup iGroup) {
        this.iGroup = iGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_group, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Group group = iGroup.geGroup(position);
        switch (group.getId()) {
            case 1:
                holder.binding.icImage.setImageResource(R.drawable.ic_viettel);
                break;
            case 2:
                holder.binding.icImage.setImageResource(R.drawable.ic_vina);
                break;
            case 3:
                holder.binding.icImage.setImageResource(R.drawable.ic_mobi);
                break;
            case 4:
                holder.binding.icImage.setImageResource(R.drawable.ic_vietnam);
                break;
            case 5:
                holder.binding.icImage.setImageResource(R.drawable.ic_gmobile);
                break;
            default:
                holder.binding.icImage.setImageResource(R.drawable.ic_other);
                break;
        }
        holder.binding.txtName.setText(group.getName());
        holder.itemView.setClickable(true);

    }

    @Override
    public int getItemCount() {
        return this.iGroup.getGroupCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemGroupBinding binding;

        ViewHolder(ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public interface IGroup {
        int getGroupCount();
        Group geGroup(int position);
    }

    public void setOnItemClickListener(IOnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(IOnRecyclerViewItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

}
