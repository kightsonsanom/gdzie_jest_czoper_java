package com.example.asinit_user.gdziejestczoper.ui.geoList;


import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.databinding.NieznanyItemBinding;
import com.example.asinit_user.gdziejestczoper.databinding.PostojItemBinding;
import com.example.asinit_user.gdziejestczoper.databinding.PrzerwaItemBinding;
import com.example.asinit_user.gdziejestczoper.databinding.RuchItemBinding;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;

import java.util.List;
import java.util.Objects;

public class PositionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Position> positionList;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                RuchItemBinding ruchItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ruch_item, parent, false);
                return new RuchViewHolder(ruchItemBinding);
            case 1:
                PostojItemBinding postojItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.postoj_item, parent, false);
                return new PostojViewHolder(postojItemBinding);
            case 2:
                NieznanyItemBinding nieznanyItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.nieznany_item, parent, false);
                return new NieznanyVieHolder(nieznanyItemBinding);
            case 3:
                PrzerwaItemBinding przerwaItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.przerwa_item, parent, false);
                return new PrzerwaViewHolder(przerwaItemBinding);
        }
        return null;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                RuchViewHolder ruchViewHolder = (RuchViewHolder) holder;
                ruchViewHolder.binding.setPosition(positionList.get(position));
                ruchViewHolder.binding.executePendingBindings();

                break;
            case 1:
                PostojViewHolder postojViewHolder = (PostojViewHolder) holder;
                postojViewHolder.binding.setPosition(positionList.get(position));
                postojViewHolder.binding.executePendingBindings();
                break;
            case 2:
                NieznanyVieHolder nieznanyVieHolder= (NieznanyVieHolder) holder;
                nieznanyVieHolder.binding.setPosition(positionList.get(position));
                nieznanyVieHolder.binding.executePendingBindings();

                break;
            case 3:
                PrzerwaViewHolder przerwaViewHolder = (PrzerwaViewHolder) holder;
                przerwaViewHolder.binding.setPosition(positionList.get(position));
                przerwaViewHolder.binding.executePendingBindings();
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        String status = positionList.get(position).getStatus();
        if (status.equals("Ruch")) {
            return 0;
        } else if (status.equals("Postój")) {
            return 1;
        } else if (status.equals("Nieznany")) {
            return 2;
        } else if (status.equals("Przerwa"))
            return 3;
        return 0;
    }

    @Override
    public int getItemCount() {
        return positionList == null ? 0 : positionList.size();
    }

    public class RuchViewHolder extends RecyclerView.ViewHolder {

        final RuchItemBinding binding;


        public RuchViewHolder(RuchItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class PostojViewHolder extends RecyclerView.ViewHolder {

        final PostojItemBinding binding;


        public PostojViewHolder(PostojItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class PrzerwaViewHolder extends RecyclerView.ViewHolder {

        final PrzerwaItemBinding binding;

        public PrzerwaViewHolder(PrzerwaItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class NieznanyVieHolder extends RecyclerView.ViewHolder {

        final NieznanyItemBinding binding;

        public NieznanyVieHolder(NieznanyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
