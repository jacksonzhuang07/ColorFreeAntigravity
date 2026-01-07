package com.example.colorfree.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UsageDao {
    @Insert
    void insert(UsageEvent event);

    @Query("SELECT * FROM usage_events WHERE startTime >= :startTime ORDER BY startTime DESC")
    List<UsageEvent> getEventsSince(long startTime);

    @Query("SELECT * FROM usage_events")
    List<UsageEvent> getAllEvents();
}
