package com.example.asinit_user.mvvmapplication.ui.mainView;


import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.databinding.GeoItemBinding;
import com.example.asinit_user.mvvmapplication.db.entities.Geo;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class GeoAdapter extends RecyclerView.Adapter<GeoAdapter.GeoViewHolder> {

    private List<Geo> geoList;

    @Override
    public GeoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GeoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.geo_item, parent, false);

        return new GeoViewHolder(binding);
    }


    public void setGeoList(final List<Geo> newGeoList) {
        Timber.d("setting new geo list" + newGeoList);
        if (geoList == null) {
            geoList = newGeoList;
            notifyItemRangeInserted(0, newGeoList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return geoList.size();
                }

                @Override
                public int getNewListSize() {
                    return newGeoList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(geoList.get(oldItemPosition).getLocation(), newGeoList.get(newItemPosition).getLocation());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Geo newGeo = newGeoList.get(newItemPosition);
                    Geo oldGeo = geoList.get(oldItemPosition);
                    return Objects.equals(newGeo.getLocation(), oldGeo.getLocation());
                }

            });
            geoList = newGeoList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public void onBindViewHolder(GeoViewHolder holder, int position) {
        holder.binding.setGeo(geoList.get(position));
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        Log.d("ACTIONSADAPTER", "getItemCount");
        return geoList == null ? 0 : geoList.size();
    }

    public class GeoViewHolder extends RecyclerView.ViewHolder {

        final GeoItemBinding binding;


        public GeoViewHolder(GeoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
