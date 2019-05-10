package com.example.asinit_user.gdziejestczoper.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.example.asinit_user.gdziejestczoper.db.Repository;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class LogJobIntentService extends JobIntentService {


    private static final int JOB_ID = 3000;

    @Inject
    Repository repository;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }


    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LogJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        repository.sendLogFile();
        //przeslij plik z logami na serwer
    }
}
