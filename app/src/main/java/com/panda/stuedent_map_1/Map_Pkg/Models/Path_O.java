package com.panda.stuedent_map_1.Map_Pkg.Models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import qenawi.panda.a_predator.network_Handeler.CService_DBase;

import java.util.List;

public class Path_O  implements Parcelable
{
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("weight")
    @Expose
    private Double weight;
    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("transfers")
    @Expose
    private Integer transfers;
    @SerializedName("points_encoded")
    @Expose
    private Boolean pointsEncoded;
    @SerializedName("bbox")
    @Expose
    private List<Double> bbox = null;
    @SerializedName("points")
    @Expose
    private String points;

    @SerializedName("legs")
    @Expose
    private List<Object> legs = null;
    @SerializedName("ascend")
    @Expose
    private Double ascend;
    @SerializedName("descend")
    @Expose
    private Double descend;
    @SerializedName("snapped_waypoints")
    @Expose
    private String snappedWaypoints;

    protected Path_O(Parcel in) {
        if (in.readByte() == 0) {
            distance = null;
        } else {
            distance = in.readDouble();
        }
        if (in.readByte() == 0) {
            weight = null;
        } else {
            weight = in.readDouble();
        }
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readInt();
        }
        if (in.readByte() == 0) {
            transfers = null;
        } else {
            transfers = in.readInt();
        }
        byte tmpPointsEncoded = in.readByte();
        pointsEncoded = tmpPointsEncoded == 0 ? null : tmpPointsEncoded == 1;
        points = in.readString();
        if (in.readByte() == 0) {
            ascend = null;
        } else {
            ascend = in.readDouble();
        }
        if (in.readByte() == 0) {
            descend = null;
        } else {
            descend = in.readDouble();
        }
        snappedWaypoints = in.readString();
    }

    public static final Creator<Path_O> CREATOR = new Creator<Path_O>() {
        @Override
        public Path_O createFromParcel(Parcel in) {
            return new Path_O(in);
        }

        @Override
        public Path_O[] newArray(int size) {
            return new Path_O[size];
        }
    };

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getTransfers() {
        return transfers;
    }

    public void setTransfers(Integer transfers) {
        this.transfers = transfers;
    }

    public Boolean getPointsEncoded() {
        return pointsEncoded;
    }

    public void setPointsEncoded(Boolean pointsEncoded) {
        this.pointsEncoded = pointsEncoded;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }



    public List<Object> getLegs() {
        return legs;
    }

    public void setLegs(List<Object> legs) {
        this.legs = legs;
    }



    public Double getAscend() {
        return ascend;
    }

    public void setAscend(Double ascend) {
        this.ascend = ascend;
    }

    public Double getDescend() {
        return descend;
    }

    public void setDescend(Double descend) {
        this.descend = descend;
    }

    public String getSnappedWaypoints() {
        return snappedWaypoints;
    }

    public void setSnappedWaypoints(String snappedWaypoints) {
        this.snappedWaypoints = snappedWaypoints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (distance == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(distance);
        }
        if (weight == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(weight);
        }
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(time);
        }
        if (transfers == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(transfers);
        }
        parcel.writeByte((byte) (pointsEncoded == null ? 0 : pointsEncoded ? 1 : 2));
        parcel.writeString(points);
        if (ascend == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(ascend);
        }
        if (descend == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(descend);
        }
        parcel.writeString(snappedWaypoints);
    }


}
