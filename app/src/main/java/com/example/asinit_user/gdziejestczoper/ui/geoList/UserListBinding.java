package com.example.asinit_user.gdziejestczoper.ui.geoList;

import android.databinding.BindingAdapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.asinit_user.gdziejestczoper.viewobjects.User;

import java.util.ArrayList;
import java.util.List;

public class UserListBinding {

    @BindingAdapter("app:items")
    public static void setItems(ListView listView, List<User> users){
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        ArrayList<String> userNames = new ArrayList<>();
        for (User u : users) {
            userNames.add(u.getNazwa());
        }
        if (adapter != null) {

            adapter.addAll(userNames);
        }
    }
}
