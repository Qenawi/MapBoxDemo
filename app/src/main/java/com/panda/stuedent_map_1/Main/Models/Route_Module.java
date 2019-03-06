package com.panda.stuedent_map_1.Main.Models;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class Route_Module implements Parcelable {
    public Route_Module()
    {

    }

    protected Route_Module(Parcel in) {
        DriverId = in.readInt();
        LineId = in.readInt();
        LineName = in.readString();
        RouteString = in.readString();
        StartPoint = in.readString();


    }

    public static final Creator<Route_Module> CREATOR = new Creator<Route_Module>() {
        @Override
        public Route_Module createFromParcel(Parcel in) {
            return new Route_Module(in);
        }

        @Override
        public Route_Module[] newArray(int size) {
            return new Route_Module[size];
        }
    };

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public int getLineId() {
        return LineId;
    }

    public void setLineId(int lineId) {
        LineId = lineId;
    }

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getRouteString() {
        return RouteString;
    }

    public void setRouteString(String routeString) {
        RouteString = routeString;
    }

    public String getStartPoint() {
        return StartPoint;
    }

    public void setStartPoint(String startPoint) {
        StartPoint = startPoint;
    }

 private    int DriverId;
    private  int LineId;
    private    String LineName;
    private   String RouteString;
    private  String StartPoint;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(DriverId);
        parcel.writeInt(LineId);
        parcel.writeString(LineName);
        parcel.writeString(RouteString);
        parcel.writeString(StartPoint);
    }
}
