package com.javanostra.meetyourmatch.persistance.entity;

import android.os.Parcel;        
import android.os.Parcelable;    
import androidx.annotation.NonNull; 


public class Tag implements Parcelable {
    private Long id;
    private String name;

    
    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    

    
    protected Tag(Parcel in) {
        
        if (in.readByte() == 0) { 
            id = null;
        } else {
            id = in.readLong(); 
        }
        
        name = in.readString();
    }

    
    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in); 
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size]; 
        }
    };

    @Override
    public int describeContents() {
        return 0; 
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        
        if (id == null) {
            dest.writeByte((byte) 0); 
        } else {
            dest.writeByte((byte) 1); 
            dest.writeLong(id);       
        }
        
        dest.writeString(name);
    }

    

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}