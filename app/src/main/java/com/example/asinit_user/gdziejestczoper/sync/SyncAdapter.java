package com.example.asinit_user.gdziejestczoper.sync;


import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.example.asinit_user.gdziejestczoper.db.Repository;

import javax.inject.Inject;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


    }

//    public static void performSync() {
//        Bundle b = new Bundle();
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        ContentResolver.requestSync(AccountGeneral.getAccount(),
//                CzoperContentProvider.AUTHORITY, b);
//    }
}
