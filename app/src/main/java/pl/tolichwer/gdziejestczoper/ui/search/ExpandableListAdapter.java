package pl.tolichwer.gdziejestczoper.ui.search;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import pl.tolichwer.gdziejestczoper.databinding.NieznanyItemBinding;
import pl.tolichwer.gdziejestczoper.databinding.PostojItemBinding;
import pl.tolichwer.gdziejestczoper.databinding.PrzerwaItemBinding;
import pl.tolichwer.gdziejestczoper.databinding.RuchItemBinding;
import pl.tolichwer.gdziejestczoper.R;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import timber.log.Timber;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> days;
    private TreeMap<String, List<Position>> positions;

    ExpandableListAdapter(Context context) {
        this.context = context;
    }

    public List<String> getDays() {
        return days;
    }

    void setDays(List<String> days) {
        this.days = days;
    }

    public TreeMap<String, List<Position>> getPositions() {
        return positions;
    }

    void setPositions(TreeMap<String, List<Position>> positions) {
        this.positions = positions;
        List<String> days = new ArrayList<>(positions.keySet());
        setDays(days);
    }

    @Override
    public int getGroupCount() {
        return days.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return positions.get(days.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return days.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return positions.get(days.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.expandable_header);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Position childEvent = (Position) getChild(groupPosition, childPosition);
        final int positionStatus = childEvent.getStatus();

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (positionStatus) {
            case 0:
                RuchItemBinding ruchItemBinding = DataBindingUtil.inflate(inflater, R.layout.ruch_item, parent, false);
                ruchItemBinding.setPosition(childEvent);
                ruchItemBinding.executePendingBindings();
                return ruchItemBinding.getRoot();
            case 1:
                PostojItemBinding postojItemBinding = DataBindingUtil.inflate(inflater, R.layout.postoj_item, parent, false);
                postojItemBinding.setPosition(childEvent);
                postojItemBinding.executePendingBindings();
                return postojItemBinding.getRoot();
            case 2:
                NieznanyItemBinding nieznanyItemBinding = DataBindingUtil.inflate(inflater, R.layout.nieznany_item, parent, false);
                nieznanyItemBinding.setPosition(childEvent);
                nieznanyItemBinding.executePendingBindings();
                return nieznanyItemBinding.getRoot();
            case 3:
                PrzerwaItemBinding przerwaItemBinding = DataBindingUtil.inflate(inflater, R.layout.przerwa_item, parent, false);
                przerwaItemBinding.setPosition(childEvent);
                przerwaItemBinding.executePendingBindings();
                return przerwaItemBinding.getRoot();
            default:
                NieznanyItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.nieznany_item, parent, false);
                binding.setPosition(childEvent);
                binding.executePendingBindings();
                return binding.getRoot();
        }

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
