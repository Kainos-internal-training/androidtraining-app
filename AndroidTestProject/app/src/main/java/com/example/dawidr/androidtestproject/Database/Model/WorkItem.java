package com.example.dawidr.androidtestproject.Database.Model;

import java.io.Serializable;
import java.util.List;

public class WorkItem implements Serializable {

    public long id;
    public String title;
    public String current_date;
    public double gps_longitude;
    public double gps_latitude;
    public int type;
    public List<WorkPhoto> photos;
}
