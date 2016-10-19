package test.client;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import test.myaidl.IMyAidlInterface;
import test.myaidl.User;

public class MainActivity extends AppCompatActivity {
IMyAidlInterface iMyAidlInterface;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                User user = new User();
                user.setName("its me");
                iMyAidlInterface.addUser(user);
                Log.i("=========",user.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setAction("service");
        //this is important
        intent.setPackage("test.myaidl");

        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

}
