package pl.tolichwer.gdziejestczoper.ui.mainView;


import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import pl.tolichwer.gdziejestczoper.R;
import pl.tolichwer.gdziejestczoper.databinding.GeoItemBinding;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;

import java.util.List;
import java.util.Objects;

public class GeoAdapter extends RecyclerView.Adapter<GeoAdapter.GeoViewHolder> {

    private List<Geo> geoList;

    @Override
    public GeoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GeoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.geo_item, parent, false);

        return new GeoViewHolder(binding);
    }


    public void setGeoList(final List<Geo> newGeoList) {
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
