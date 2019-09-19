package pl.tolichwer.gdziejestczoper.ui.geoList;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import pl.tolichwer.gdziejestczoper.R;
import pl.tolichwer.gdziejestczoper.databinding.PositionListFragmentViewBinding;
import pl.tolichwer.gdziejestczoper.utils.Converters;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;


import pl.tolichwer.gdziejestczoper.viewobjects.Resource;
import java.util.List;
import javax.inject.Inject;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;

public class PositionListFragment extends DaggerFragment {


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PositionsAdapter positionsAdapter;

    private PositionListFragmentViewModel viewModel;
    private PositionListFragmentViewBinding binding;
    private final Fragment currentFragment = this;
    private boolean userListVisible;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.position_list_fragment_view, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PositionListFragmentViewModel.class);
        binding.setModel(viewModel);
        binding.setLifecycleOwner(this);
        initUserList();
        setEventListeners();
    }

    private void initUserList() {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        viewModel.getObservableUserNames().observe(this, resource -> {
            if (resource != null && resource.data != null) {
                itemsAdapter.addAll(resource.data);
            }
        });

        userListVisible = true;
        binding.userList.setAdapter(itemsAdapter);
    }


    private void setEventListeners() {

        binding.userList.setOnItemClickListener((parent, view, position, id) -> {
            binding.positionRecycler.setAdapter(positionsAdapter);
            LiveData<Resource<List<Position>>> positions = viewModel.getPositionsForUserAndDay(position);

            positions.observe(currentFragment, resource -> {
                if (resource != null && resource.data != null) {
                    Converters.sortPositions(resource.data);
                    positionsAdapter.setPositionsList(resource.data);
                }
            });

            binding.userList.setVisibility(View.GONE);
            binding.currentDay.setVisibility(View.VISIBLE);
            binding.positionRecycler.setVisibility(View.VISIBLE);
            userListVisible = false;
        });
    }

    public void returnToUserList(){
        binding.userList.setVisibility(View.VISIBLE);
        binding.currentDay.setVisibility(View.GONE);
        binding.positionRecycler.setVisibility(View.GONE);
        userListVisible = true;
    }

    public boolean isUserListVisible() {
        return userListVisible;
    }
}
