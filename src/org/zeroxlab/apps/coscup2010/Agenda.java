package org.zeroxlab.apps.coscup2010;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Agenda {
    public static final String AUTHORITY = "org.zeroxlab.apps.coscup2010.agenda";

    private Agenda() {}

    public static final class Tracks implements BaseColumns {
        private Tracks() {}

        public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/tracks");

        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.zeroxlab.track";

        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.zeroxlab.track";

        public static final String DEFAULT_SORT_ORDER = "updated DESC";

        public static final String TITLE = "title";

        public static final String UUID = "uuid";

        public static final String UPDATED = "updated";

        public static final String SUMMARY = "summary";

        public static final String COLOR = "color";
    }

    public static final class Speakers implements BaseColumns {
        private Speakers() {}

        public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/speakers");

        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.zeroxlab.speaker";

        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.zeroxlab.speaker";

        public static final String DEFAULT_SORT_ORDER = "updated DESC";

        public static final String NAME = "name";

        public static final String UUID = "uuid";

        public static final String UPDATED = "updated";

        public static final String BIOGRAPHY = "biography";
    }

    public static final class Sessions implements BaseColumns {
        private Sessions() {}

        public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/sessions");

        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.zeroxlab.session";

        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.zeroxlab.session";

        public static final String DEFAULT_SORT_ORDER = "updated DESC";

        public static final String TITLE = "title";

        public static final String UUID = "uuid";

        public static final String UPDATED = "updated";

        public static final String SUMMARY = "summary";

        public static final String ROOM = "room";

        public static final String LANG = "lang";

        public static final String START = "start";

        public static final String END = "end";

        public static final String TRACK = "track";
    }

    public static final class SpeakersSessions implements BaseColumns {
        private SpeakersSessions() {}

        public static final String SPEAKER = "speaker";

        public static final String SESSION = "session";
    }

    public static final class StarredSessions implements BaseColumns {
        private StarredSessions() {}

        public static final String SESSION = "session";
    }
}
