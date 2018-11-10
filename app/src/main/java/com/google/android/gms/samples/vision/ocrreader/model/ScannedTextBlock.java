package com.google.android.gms.samples.vision.ocrreader.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.vision.text.TextBlock;

public class ScannedTextBlock implements Parcelable
{
    String textBlock;

    public ScannedTextBlock(String textBlock) {
        this.textBlock = textBlock;
    }

    protected ScannedTextBlock(Parcel in) {
        textBlock = in.readString();
    }

    public static final Creator<ScannedTextBlock> CREATOR = new Creator<ScannedTextBlock>() {
        @Override
        public ScannedTextBlock createFromParcel(Parcel in) {
            return new ScannedTextBlock(in);
        }

        @Override
        public ScannedTextBlock[] newArray(int size) {
            return new ScannedTextBlock[size];
        }
    };

    public String getTextBlock() {
        return textBlock;
    }

    public void setTextBlock(String textBlock) {
        this.textBlock = textBlock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(textBlock);
    }
}
