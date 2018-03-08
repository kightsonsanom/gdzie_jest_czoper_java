package com.example.asinit_user.mvvmapplication.ui.mainView;


import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.databinding.ActionItemBinding;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;

import java.util.List;
import java.util.Objects;

public class ActionsAdapter extends RecyclerView.Adapter<ActionsAdapter.ActionViewHolder> {

    private List<ActionEntity> actionEntityList;

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ActionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.action_item, parent, false);

        return new ActionViewHolder(binding);
    }


    public void setActionList(final List<ActionEntity> actionsList) {
        if (actionEntityList == null) {
            actionEntityList = actionsList;
            notifyItemRangeInserted(0, actionsList.size());
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
                    return Objects.equals(actionEntityList.get(oldItemPosition).getTekst(), actionsList.get(newItemPosition).getTekst());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ActionEntity newAction = actionsList.get(newItemPosition);
                    ActionEntity oldAction = actionEntityList.get(oldItemPosition);
                    return Objects.equals(newAction.getTekst(), oldAction.getTekst());
                }

            });
            actionEntityList = actionsList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, int position) {
        holder.binding.setAction(actionEntityList.get(position));
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        Log.d("ACTIONSADAPTER", "getItemCount");
        return actionEntityList == null ? 0 : actionEntityList.size();
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {

        final ActionItemBinding binding;


        public ActionViewHolder(ActionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
