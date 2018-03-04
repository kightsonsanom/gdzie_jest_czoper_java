package com.example.asinit_user.mvvmapplication.mainView;


import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.databinding.RecyclerItemBinding;
import com.example.asinit_user.mvvmapplication.model.Action;

import java.util.List;
import java.util.Objects;

public class ActionsAdapter extends RecyclerView.Adapter<ActionsAdapter.ActionViewHolder> {

    List<? extends Action> actionEntityList;

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.recycler_item, parent, false);

        return new ActionViewHolder(binding);
    }


    public void setActionList(final List<? extends Action> actionsList) {
        if (actionEntityList == null) {
            actionEntityList = actionsList;
//            notifyItemRangeInserted(0, productList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return actionEntityList.size();
                }

                @Override
                public int getNewListSize() {
                    return actionsList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(actionEntityList.get(oldItemPosition).getText(), actionsList.get(newItemPosition).getText());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Action newAction = actionsList.get(newItemPosition);
                    Action oldAction = actionEntityList.get(oldItemPosition);
                    return Objects.equals(newAction.getText(), oldAction.getText());
                }

            });
            actionEntityList = actionsList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, int position) {
        holder.binding.setAction(actionEntityList.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {

        final RecyclerItemBinding binding;


        public ActionViewHolder(RecyclerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
