package com.example.asinit_user.gdziejestczoper.sync;/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.asinit_user.gdziejestczoper.db.AppDatabase;
import com.example.asinit_user.gdziejestczoper.db.dao.GeoDao;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionDao;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;

import java.util.ArrayList;

import javax.inject.Inject;


/**
 * A {@link ContentProvider} based on a Room database.
 *
 * <p>Note that you don't need to implement a ContentProvider unless you want to expose the data
 * outside your process or your application already uses a ContentProvider.</p>
 */
public class CzoperContentProvider extends ContentProvider {

    /** The authority of this content provider. */
    public static final String AUTHORITY = "com.example.asinit_user.gdziejestczoper.sync";

    /** The URI for the Cheese table. */
    public static final Uri URI_POSITION = Uri.parse(
            "content://" + AUTHORITY + "/" + Position.TABLE_NAME);

    /** The match code for some items in the Cheese table. */
    private static final int CODE_POSITION_DIR = 1;

    /** The match code for an item in the Cheese table. */
    private static final int CODE_POSITION_ITEM = 2;

    /** The URI matcher. */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, Position.TABLE_NAME, CODE_POSITION_DIR);
        MATCHER.addURI(AUTHORITY, Position.TABLE_NAME + "/*", CODE_POSITION_ITEM);
    }

    @Inject
    PositionDao positionDao;

    @Inject
    GeoDao geoDao;

    @Inject
    AppDatabase appDatabase;


    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = MATCHER.match(uri);
        if (code == CODE_POSITION_DIR || code == CODE_POSITION_ITEM) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
//            PositionDao cheese = SampleDatabase.getInstance(context).cheese();
            final Cursor cursor;
            if (code == CODE_POSITION_DIR) {
                cursor = positionDao.selectAll();
            } else {
                cursor = positionDao.selectById(ContentUris.parseId(uri));
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_POSITION_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + Position.TABLE_NAME;
            case CODE_POSITION_ITEM:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + Position.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (MATCHER.match(uri)) {
            case CODE_POSITION_DIR:
                final Context context = getContext();
                if (context == null) {
                    return null;
                }
                final long id = positionDao.insert(Position.fromContentValues(values));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case CODE_POSITION_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (MATCHER.match(uri)) {
            case CODE_POSITION_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case CODE_POSITION_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final int count = positionDao.deleteById(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (MATCHER.match(uri)) {
            case CODE_POSITION_DIR:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case CODE_POSITION_ITEM:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final Position position = Position.fromContentValues(values);
                position.setPosition_id(ContentUris.parseId(uri));
                final int count = positionDao.update(position);
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(
            @NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final Context context = getContext();
        if (context == null) {
            return new ContentProviderResult[0];
        }
        appDatabase.beginTransaction();
        try {
            final ContentProviderResult[] result = super.applyBatch(operations);
            appDatabase.setTransactionSuccessful();
            return result;
        } finally {
            appDatabase.endTransaction();
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        switch (MATCHER.match(uri)) {
            case CODE_POSITION_DIR:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }

                final Position[] cheeses = new Position[valuesArray.length];
                for (int i = 0; i < valuesArray.length; i++) {
                    cheeses[i] = Position.fromContentValues(valuesArray[i]);
                }
                return positionDao.insertAll(cheeses).length;
            case CODE_POSITION_ITEM:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

}
