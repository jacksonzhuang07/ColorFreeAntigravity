package com.example.colorfree.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usage_events")
public class UsageEvent {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String packageName;
    public long startTime;
    public long endTime;
    public boolean isColorMode;

    public UsageEvent(String packageName, long startTime, long endTime, boolean isColorMode) {
        this.packageName = packageName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isColorMode = isColorMode;
    }

    public long getDuration() {
        return endTime - startTime;
    }
}
