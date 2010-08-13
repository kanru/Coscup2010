package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;
import org.zeroxlab.apps.coscup2010.Agenda.Speakers;
import org.zeroxlab.apps.coscup2010.Agenda.SpeakersSessions;
import org.zeroxlab.apps.coscup2010.Agenda.Tracks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.StartElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements ISyncAble {

    private final Context mContext;

    private static final String TAG = "COSCUP2010/DatabaseHelper";

    private static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";
    private static final String AGENDA_NAMESPACE = "http://schemas.0xlab.org/agenda";

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "coscup.db";

    public static final String TRACKS_TABLE_NAME = "tracks";
    public static final String SPEAKERS_TABLE_NAME = "speakers";
    public static final String SESSIONS_TABLE_NAME = "sessions";
    public static final String SPEAKERS_SESSIONS_TABLE_NAME = "speakers_sessions";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TRACKS_TABLE_NAME + " ("
                   + Tracks._ID + " INTEGER PRIMARY KEY,"
                   + Tracks.TITLE + " TEXT,"
                   + Tracks.UUID + " TEXT UNIQUE,"
                   + Tracks.UPDATED + " DATE,"
                   + Tracks.SUMMARY + " TEXT"
                   + ");");
        db.execSQL("CREATE TABLE " + SPEAKERS_TABLE_NAME + " ("
                   + Speakers._ID + " INTEGER PRIMARY KEY,"
                   + Speakers.NAME + " TEXT,"
                   + Speakers.UUID + " TEXT UNIQUE,"
                   + Speakers.UPDATED + " DATE,"
                   + Speakers.BIOGRAPHY + " TEXT"
                   + ");");
        db.execSQL("CREATE TABLE " + SESSIONS_TABLE_NAME + " ("
                   + Sessions._ID + " INTEGER PRIMARY KEY,"
                   + Sessions.TITLE + " TEXT,"
                   + Sessions.UUID + " TEXT UNIQUE,"
                   + Sessions.UPDATED + " DATE,"
                   + Sessions.SUMMARY + " TEXT,"
                   + Sessions.ROOM + " INTEGER,"
                   + Sessions.LANG + " TEXT,"
                   + Sessions.START + " DATE,"
                   + Sessions.END + " DATE,"
                   + Sessions.TRACK + " TEXT,"
                   + Sessions.STARRED + " BOOLEAN,"
                   + "FOREIGN KEY(" + Sessions.TRACK + ") REFERENCES "
                   + TRACKS_TABLE_NAME + "(" + Tracks.UUID + ")"
                   + ");");
        db.execSQL("CREATE TABLE " + SPEAKERS_SESSIONS_TABLE_NAME + " ("
                   + SpeakersSessions._ID + " INTEGER PRIMARY KEY,"
                   + SpeakersSessions.SPEAKER + " TEXT,"
                   + SpeakersSessions.SESSION + " TEXT,"
                   + "FOREIGN KEY(" + SpeakersSessions.SPEAKER + ") REFERENCES "
                   + SPEAKERS_TABLE_NAME + "(" + Speakers.UUID + "),"
                   + "FOREIGN KEY(" + SpeakersSessions.SESSION + ") REFERENCES "
                   + SESSIONS_TABLE_NAME + "(" + Sessions.UUID + ")"
                   + ");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TRACKS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SPEAKERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SESSIONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SPEAKERS_SESSIONS_TABLE_NAME);
        onCreate(db);
    }

    /* ISyncAble */
    public int sync() {
        int r;
        r = syncTracks();
        r = syncSpeakers();
        r = syncSessions();
        return r;
    }

    private int syncTracks() {
        final ContentValues values = new ContentValues();
        final SQLiteDatabase db = getWritableDatabase();
        RootElement trackRoot = new RootElement(ATOM_NAMESPACE, "feed");
        Element trackEntry = trackRoot.getChild(ATOM_NAMESPACE, "entry");
        trackEntry.setElementListener(new ElementListener() {
                public void start(Attributes attr) {
                    values.clear();
                }
                public void end() {
                    if (0 == db.update(TRACKS_TABLE_NAME, values,
                                       Tracks.UUID + "=?",
                                       new String[] { values.getAsString(Tracks.UUID) }))
                        db.insert(TRACKS_TABLE_NAME, Tracks.UUID, values);
                    Log.d(TAG, "Add track " + values.getAsString(Tracks.TITLE));
                }
            });
        trackEntry.requireChild(ATOM_NAMESPACE, "title")
            .setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    values.put(Tracks.TITLE, body);
                }
            });
        trackEntry.requireChild(ATOM_NAMESPACE, "id")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Tracks.UUID, body);
                    }
                });
        trackEntry.requireChild(ATOM_NAMESPACE, "updated")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Tracks.UPDATED, body);
                    }
                });
        trackEntry.requireChild(ATOM_NAMESPACE, "summary")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Tracks.SUMMARY, body);
                    }
                });
        InputStream in = mContext.getResources().openRawResource(R.raw.cache_tracks);
        try {
            Xml.parse(in, Xml.Encoding.UTF_8, trackRoot.getContentHandler());
        } catch (IOException e) {
            Log.d(TAG, "", e);
        } catch (SAXException e) {
            Log.d(TAG, "", e);
        }
        db.close();
        return ISyncAble.SYNC_TIMEOUT;
    }

    private int syncSessions() {
        final ContentValues values = new ContentValues();
        final SQLiteDatabase db = getWritableDatabase();
        final List<String> speakers = new ArrayList<String>();
        RootElement sessionRoot = new RootElement(ATOM_NAMESPACE, "feed");
        Element sessionEntry = sessionRoot.getChild(ATOM_NAMESPACE, "entry");
        sessionEntry.setElementListener(new ElementListener() {
                public void start(Attributes attr) {
                    values.clear();
                    speakers.clear();
                }
                public void end() {
                    String suuid = values.getAsString(Sessions.UUID);
                    if (0 == db.update(SESSIONS_TABLE_NAME, values,
                                       Sessions.UUID + "=?",
                                       new String[] { suuid }))
                        db.insert(SESSIONS_TABLE_NAME, Sessions.UUID, values);
                    Log.d(TAG, "Update session " + suuid);
                    db.delete(SPEAKERS_SESSIONS_TABLE_NAME,
                              SpeakersSessions.SESSION + "=?",
                              new String[] { suuid });
                    ContentValues joint = new ContentValues();
                    joint.put(SpeakersSessions.SESSION, suuid);
                    for (String uuid : speakers) {
                        joint.put(SpeakersSessions.SPEAKER, uuid);
                        db.insert(SPEAKERS_SESSIONS_TABLE_NAME, null, joint);
                    }
                }
            });
        sessionEntry.requireChild(ATOM_NAMESPACE, "title")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.TITLE, body);
                    }
                });
        sessionEntry.requireChild(ATOM_NAMESPACE, "id")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.UUID, body);
                    }
                });
        sessionEntry.requireChild(ATOM_NAMESPACE, "updated")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.UPDATED, body);
                    }
                });
        sessionEntry.requireChild(ATOM_NAMESPACE, "summary")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.SUMMARY, body);
                    }
                });
        sessionEntry.requireChild(AGENDA_NAMESPACE, "room")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.ROOM, Integer.parseInt(body));
                    }
                });
        sessionEntry.requireChild(AGENDA_NAMESPACE, "lang")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.LANG, body);
                    }
                });
        sessionEntry.requireChild(AGENDA_NAMESPACE, "start")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.START, body);
                    }
                });
        sessionEntry.requireChild(AGENDA_NAMESPACE, "end")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.END, body);
                    }
                });
        sessionEntry.requireChild(AGENDA_NAMESPACE, "speaker")
            .setStartElementListener(new StartElementListener() {
                    public void start(Attributes attrs) {
                        speakers.add(attrs.getValue("id"));
                    }
                });
        sessionEntry.requireChild(AGENDA_NAMESPACE, "track")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Sessions.TRACK, body);
                    }
                });
        InputStream in = mContext.getResources().openRawResource(R.raw.cache_sessions);
        try {
            Xml.parse(in, Xml.Encoding.UTF_8, sessionRoot.getContentHandler());
        } catch (IOException e) {
            Log.d(TAG, "", e);
        } catch (SAXException e) {
            Log.d(TAG, "", e);
        }
        db.close();
        return ISyncAble.SYNC_TIMEOUT;
    }

    private int syncSpeakers() {
        final ContentValues values = new ContentValues();
        final SQLiteDatabase db = getWritableDatabase();
        RootElement speakerRoot = new RootElement(ATOM_NAMESPACE, "feed");
        Element speakerEntry = speakerRoot.getChild(ATOM_NAMESPACE, "entry");
        speakerEntry.setElementListener(new ElementListener() {
                public void start(Attributes attr) {
                    values.clear();
                }
                public void end() {
                    if (0 == db.update(SPEAKERS_TABLE_NAME, values,
                                       Speakers.UUID + "=?",
                                       new String[] {values.getAsString(Speakers.UUID)}))
                        db.insert(SPEAKERS_TABLE_NAME, Speakers.UUID, values);
                    Log.d(TAG, "Update speaker " + values.getAsString(Speakers.UUID));
                }
            });
        speakerEntry.requireChild(ATOM_NAMESPACE, "title")
            .setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    values.put(Speakers.NAME, body);
                }
            });
        speakerEntry.requireChild(ATOM_NAMESPACE, "id")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Speakers.UUID, body);
                    }
                });
        speakerEntry.requireChild(ATOM_NAMESPACE, "updated")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Speakers.UPDATED, body);
                    }
                });
        speakerEntry.requireChild(ATOM_NAMESPACE, "summary")
            .setEndTextElementListener(new EndTextElementListener() {
                    public void end(String body) {
                        values.put(Speakers.BIOGRAPHY, body);
                    }
                });
        InputStream in = mContext.getResources().openRawResource(R.raw.cache_speakers);
        try {
            Xml.parse(in, Xml.Encoding.UTF_8, speakerRoot.getContentHandler());
        } catch (IOException e) {
            Log.d(TAG, "", e);
        } catch (SAXException e) {
            Log.d(TAG, "", e);
        }
        db.close();
        return ISyncAble.SYNC_TIMEOUT;
    }
}
