package org.zeroxlab.apps.coscup2010;

public interface ISyncAble {
    public static final int SYNC_COMPLETED = 0;
    public static final int SYNC_TIMEOUT   = 1;
    public int sync();
}
