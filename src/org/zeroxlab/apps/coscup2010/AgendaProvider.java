package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;
import org.zeroxlab.apps.coscup2010.Agenda.Speakers;
import org.zeroxlab.apps.coscup2010.Agenda.SpeakersSessions;
import org.zeroxlab.apps.coscup2010.Agenda.Tracks;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AgendaProvider extends ContentProvider {

    private static final String TAG = "COSCUP2010/Provider";

    private static final int TRACKS            = 1;
    private static final int TRACK_ID          = 2;
    private static final int TRACK_SESSIONS    = 3;
    private static final int SESSIONS          = 4;
    private static final int SESSION_ID        = 5;
    private static final int SESSIONS_STARRED  = 6;
    private static final int SESSIONS_SPEAKERS = 7;
    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy;

        if (!TextUtils.isEmpty(sortOrder))
            orderBy = sortOrder;
        else
            orderBy = null;

        switch (sUriMatcher.match(uri)) {
        case TRACKS:
            qb.setTables(DatabaseHelper.TRACKS_TABLE_NAME);
            if (orderBy == null)
                orderBy = Tracks.DEFAULT_SORT_ORDER;
            break;
        case TRACK_ID:
            qb.setTables(DatabaseHelper.TRACKS_TABLE_NAME);
            qb.appendWhere(Tracks._ID + "=" + uri.getPathSegments().get(1));
            if (orderBy == null)
                orderBy = Tracks.DEFAULT_SORT_ORDER;
            break;
        case TRACK_SESSIONS:
            qb.setTables(DatabaseHelper.SESSIONS_TABLE_NAME);
            qb.appendWhere(Sessions.TRACK + "=(SELECT " + Tracks.UUID + " FROM " +
                           DatabaseHelper.TRACKS_TABLE_NAME +
                           " WHERE " + Tracks._ID + "=" + uri.getPathSegments().get(1) +
                           ")"
                           );
            if (orderBy == null)
                orderBy = Sessions.DEFAULT_SORT_ORDER;
            break;
        case SESSIONS:
            qb.setTables(DatabaseHelper.SESSIONS_TABLE_NAME);
            if (orderBy == null)
                orderBy = Sessions.DEFAULT_SORT_ORDER;
            break;
        case SESSION_ID:
            qb.setTables(DatabaseHelper.SESSIONS_TABLE_NAME);
            qb.appendWhere(Sessions._ID + "=" + uri.getPathSegments().get(1));
            if (orderBy == null)
                orderBy = Sessions.DEFAULT_SORT_ORDER;
            break;
        case SESSIONS_STARRED:
            qb.setTables(DatabaseHelper.SESSIONS_TABLE_NAME);
            qb.appendWhere(Sessions.UUID + "=(SELECT * FROM " +
                           DatabaseHelper.STARRED_SESSIONS_TABLE_NAME + ")");
            if (orderBy == null)
                orderBy = Sessions.DEFAULT_SORT_ORDER;
            break;
        case SESSIONS_SPEAKERS:
            qb.setTables(DatabaseHelper.SPEAKERS_TABLE_NAME);
            qb.appendWhere(Speakers.UUID + " in (SELECT " + SpeakersSessions.SPEAKER +
                           " FROM " + DatabaseHelper.SPEAKERS_SESSIONS_TABLE_NAME +
                           " WHERE " + SpeakersSessions.SESSION + "=(SELECT " +
                           Sessions.UUID + " FROM " + DatabaseHelper.SESSIONS_TABLE_NAME +
                           " WHERE " + Sessions._ID + "=" + uri.getPathSegments().get(1) +
                           "))"
                           );
            if (orderBy == null)
                orderBy = Speakers.DEFAULT_SORT_ORDER;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case TRACKS:
            return Tracks.CONTENT_TYPE;
        case TRACK_ID:
            return Tracks.CONTENT_ITEM_TYPE;
        case TRACK_SESSIONS:
        case SESSIONS:
        case SESSIONS_STARRED:
            return Sessions.CONTENT_TYPE;
        case SESSION_ID:
            return Sessions.CONTENT_ITEM_TYPE;
        case SESSIONS_SPEAKERS:
            return Speakers.CONTENT_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Modification is not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                         String[] selectionArgs) {
        throw new UnsupportedOperationException("Modification is not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Modification is not supported");
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Agenda.AUTHORITY, "tracks", TRACKS);
        sUriMatcher.addURI(Agenda.AUTHORITY, "tracks/#", TRACK_ID);
        sUriMatcher.addURI(Agenda.AUTHORITY, "tracks/#/sessions", TRACK_SESSIONS);
        sUriMatcher.addURI(Agenda.AUTHORITY, "sessions", SESSIONS);
        sUriMatcher.addURI(Agenda.AUTHORITY, "sessions/#", SESSION_ID);
        sUriMatcher.addURI(Agenda.AUTHORITY, "sessions/starred", SESSIONS_STARRED);
        sUriMatcher.addURI(Agenda.AUTHORITY, "sessions/#/speakers", SESSIONS_SPEAKERS);
    }
}
