package test.myaidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skysoft on 2016/10/17.
 */
public class MyService extends Service {
    List<User> users = new ArrayList<User>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public String getName() throws RemoteException {
            return "My is server";
        }

        @Override
        public void addUser(User user) throws RemoteException {
             users.add(user);
        }


    }
}
