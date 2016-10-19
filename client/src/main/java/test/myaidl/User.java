package test.myaidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by skysoft on 2016/10/17.
 */
public class User implements Parcelable {
    public String name;
    public int id;
    public User() {
    }

    protected User(Parcel in) {
        name = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            in.readInt();
            in.readString();
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(id);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
