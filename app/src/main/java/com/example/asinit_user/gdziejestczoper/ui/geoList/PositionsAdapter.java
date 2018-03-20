package com.example.asinit_user.gdziejestczoper.ui.geoList;


import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.databinding.PositionItemBinding;
import com.example.asinit_user.gdziejestczoper.db.entities.Position;

import java.util.List;
import java.util.Objects;

public class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.PositionViewHolder> {

    private List<Position> positionList;

    @Override
    public PositionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PositionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.position_item, parent, false);

        return new PositionViewHolder(binding);
    }


    public void setPositionsList(final List<Position> positionList) {
        if (this.positionList == null) {
            this.positionList = positionList;
            notifyItemRangeInserted(0, positionList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return PositionsAdapter.this.positionList.size();
                }

                @Override
                public int getNewListSize() {
                    return positionList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(PositionsAdapter.this.positionList.get(oldItemPosition).getId(), positionList.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Position newAction = positionList.get(newItemPosition);
                    Position oldAction = PositionsAdapter.this.positionList.get(oldItemPosition);
                    return Objects.equals(newAction.getId(), oldAction.getId());
                }

            });
            this.positionList = positionList;
            result.dispatchUpdatesTo(this);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PositionViewHolder holder, int position) {
        holder.binding.setPosition(positionList.get(position));
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return positionList == null ? 0 : positionList.size();
    }

    public class PositionViewHolder extends RecyclerView.ViewHolder {

        final PositionItemBinding binding;


        public PositionViewHolder(PositionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
