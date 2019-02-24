package com.panda.stuedent_map_1.Map_Pkg.Models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import qenawi.panda.a_predator.network_Handeler.CService_DBase;

import java.util.List;

public class DirectionResoinse extends CService_DBase implements Parcelable {
    public DirectionResoinse() {
    }

    @SerializedName("paths")
    @Expose
    private List<Path_O> paths = null;

    protected DirectionResoinse(Parcel in) {
        paths = in.createTypedArrayList(Path_O.CREATOR);
    }

    public static final Creator<DirectionResoinse> CREATOR = new Creator<DirectionResoinse>() {
        @Override
        public DirectionResoinse createFromParcel(Parcel in) {
            return new DirectionResoinse(in);
        }

        @Override
        public DirectionResoinse[] newArray(int size) {
            return new DirectionResoinse[size];
        }
    };

    public List<Path_O> getPaths() {
        return paths;
    }

    public void setPaths(List<Path_O> paths) {
        this.paths = paths;
    }

    @Override
    public boolean Is_Data_Good() {
        return paths != null && !paths.isEmpty();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(paths);
    }
}
