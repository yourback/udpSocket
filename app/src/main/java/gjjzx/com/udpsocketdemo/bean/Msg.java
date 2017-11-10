package gjjzx.com.udpsocketdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by PC on 2017/11/10.
 */

public class Msg extends DataSupport implements Parcelable {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEDT = 1;

    private String content;
    private int type;
    private long currentTime;

    protected Msg(Parcel in) {
        content = in.readString();
        type = in.readInt();
        currentTime = in.readLong();
    }

    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel in) {
            return new Msg(in);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };

    public long getCurrentTime() {
        return currentTime;
    }

    public Msg(String content, int type, long currentTime) {
        this.content = content;
        this.type = type;
        this.currentTime = currentTime;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(content);
        parcel.writeInt(type);
        parcel.writeLong(currentTime);
    }
}
