# AIDL
AIDL跨进程传输

Android跨进程通信使用AIDL
AIDL的作用
AIDL (Android Interface Definition Language) 是一种IDL 语言，用于生成可以在Android设备上两个进程之间进行进程间通信(interprocess communication, IPC)的代码。如果在一个进程中（例如Activity）要调用另一个进程中（例如Service）对象的操作，就可以使用AIDL生成可序列化的参数。
选择AIDL的使用场合
官方文档特别提醒我们何时使用AIDL是必要的：只有你允许客户端从不同的应用程序为了进程间的通信而去访问你的service，以及想在你的service处理多线程。
如果不需要进行不同应用程序间的并发通信(IPC)，you should create your interface by implementing a Binder；或者你想进行IPC，但不需要处理多线程的，则implement your interface using a Messenger。
服务端
AIDL接口必须定义在.aidl文件中（命名满足Java语法），并同时保存在service所在的应用程序和其它绑定该service的应用程序（需要通过AIDL进行IPC的service）中，保存位置为源代码中src/目录下（android studio在main文件下）。
当我们新建一个.aidl文件时，android SDK工具就会根据该文件自动生成一个IBinder接口，并且保存在gen/目录下（android studio目录build/generated/source/aidl/debug下）。service必须实现IBinder接口，客户端才能绑定service并调用方法获得该对象进行IPC。
在android studio中结构如下： 
 
为了能够创建使用AIDL的service，必须要实现以下步骤：
1.	创建.aidl文件 
该文件定义了带有方法声明的编程接口
2.	实现接口及其里面的方法 
android SDK工具使用java生成一个接口，依据是根据.aidl文件。这个接口中有一个内部抽象类Stub，该类继承了Binder类，并且实现了AIDL接口中的方法，我们必须继承Stub类和实现其方法。
3.	将接口暴露给客户端 
实现Service类，覆盖onBind()方法，并且返回Stub的实现。
下面详细介绍以上几步：
1.	创建.aidl文件
Android studio中右键即可创建aidl文件，如下所示 
AIDL需要满足一些简单的语法：能够使我们声明一个接口，该接口可以包含一个或多个方法，且能够带有参数和返回值。参数和返回值可以是任何类型的甚至是其它AIDL生成的接口。
每个.aidl文件必须定义一个接口，并且只需要接口声明和方法声明。
默认情况下，AIDL支持以下数据类型：
•	java中的基本数据类型（int、long、char、Boolean、double等）
•	String
•	CharSequence
•	List 只支持ArrayList,里面每个元素都必须能够被AIDL支持
•	Map只支持HashMap,里面每个元素都必须能够被AIDL支持，包括Key和value
•	AIDL 所有的AIDL接口本身也可以在AIDL文件中使用
如要传递其他类型，必须使用import导入，即使它们在同一个包中，并且必须标识其方向in/out.如果AIDL文件中用到了自定义的Parcelable接口，那么必须新建一个和它同名的AIDL文件，并在其中声明它为Parcelabel.
在定义AIDL接口时，需要注意以下几点：
•	方法可以带有0个或多个参数，可以选择有无返回值。
•	所有的非基本数据类型都需要指定一个方向来标记数据的流向（进，出，进出同时）。基本数据类型默认是进，且不能改变。我们有必要限制数据方向（真正需要的方向），原因在于marshalling参数的代价消耗大。
•	文件中的所有代码注释都被包含在生成的IBinder接口中，除非是import和package之前注释的。
•	只支持方法，不支持静态成员变量。且方法不能有修饰符。
•	需要手动输入包名（android studio不需要手动）
2. 实现接口
自动生成文件包含一个内部抽象类Stub，继承了Binder并且是父类接口的一个抽象实现，实现了.aidl文件中的所有方法。Stub同时定义了一些其他有用的方法，尤其是asInterface()方法，该方法接收一个IBinder对象，返回Stub接口的实现。Stub英文表示存根的意思，该类在服务端进程，我们必须继承该类并实现aidl接口中的方法。
下面用一个类实现简单的接口实例：
//IMyAidlInterface是我们建立的.aidl文件
// IMyAidlInterface.Stub该类实现了在.aidl文件中声明的所有的方法

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

Stub实现了Binder类（定义了远程过程调用协议Remote Procedure Call Protocol RPC），因此mBinder可以传输给客户端。
在实现AIDL时需要注意一下几点：
•	调用不能保证在主线程中执行，我们应该考虑多线程问题，并保证service是线程安全的。
•	默认情况，RPC调用是异步的。若service需要长时间的操作要保证调用不能发生在主线程中，因为这个可能出现应用程序无法响应问题Application Not Responding ANR。因此我们应该保证调用发生在另外的子线程中。
•	不会给调用者抛出异常。
3. 将接口暴露给客户端
为service实现了AIDL接口，把接口暴露给客户端，使得他们能够绑定它。下面给出完整的代码，说明如何实现：
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
最后在AndroidManifest.xml中配置该服务：
<service
    android:name=".MyService"
    android:process=":remote"
    >
    <intent-filter>
        <action android:name="service"/>
    </intent-filter>
    </service>

客户端
将服务端的aidl文件夹拷贝至客户端
客户端结构如下：
 

那么，当客户端调用bindService()连接service时，客户端回调onServiceConnected()方法接收mBinder实例(service的onBinder()方法返回的)。
客户端必须也能够获得该接口类，因此当客户端和service在不同的应用程序时，客户端应用程序必须复制一份.aidl文件，这样才能获得AIDL中的方法。
当客户在onServiceConnected()方法中接收IBinder对象时，必须通过调用IMyAidlInterface.Stub.asInterface(service)转换为IMyAidlInterface类型。如下：

•	    Intent intent = new Intent();
        intent.setAction("service");
        //this is important
        intent.setPackage("test.myaidl");
•	
//绑定服务
        bindService(intent,conn, Service.BIND_AUTO_CREATE);
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

// 转换为IMyAidlInterface类型
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service); 

            try {
//通过接口传递数据
iMyAidlInterface.getName();
                         } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
通过IPC传递自定义对象
我们可以实现通过IPC把对象从一个进程传递到另一个进程中。但是，我们必须要确保在另一个进程中可以获得该对象（即需要该类的代码），并且该类需要支持Parcelable接口。必须要支持Parcelable，这样系统才能将对象分解为基本数据类型（能够跨进程marshalled）。
注意：Parcelable是一个接口，实现该接口的类实例能够保存在Parcel中并从中恢复。该类中必须有一个名叫CREATOR的静态成员变量，该成员是Parcelable.Creator的一个实现实例。
为了创建支持Parcelable协议的类，必须完成以下几点：
1.	该类必须实现Parcelable接口；
2.	实现writeToParcel()方法，记录当前对象的状态（成员变量等），并用Parcel保存。还要实现describeContents()，一般返回0；
3.	添加静态成员变量CREAROR，该成员是Parcelable.Creator的一个实现实例；
4.	最后创建一个.aidl文件声明该parcelable类（例如下面的Useraidl文件）。AIDL通过上述办法产生marshall和unmarshall对象。
下面是一个实现Parcelable接口的类User，首先要有User.aidl文件。
(注意：User.aidl需要在User.java之前建好，若先新建User.java再建User.aidl的时候会显示接口名字重复而新建失败，在android studio中自定义类型的java类不能放在aidl文件中，否则会报)错找不到符号)
User.aidl如下：
声明自定义类型User
 
实现了Parcelable接口的User类：
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
    public void writeToParcel(Parcel dest, int flags) 

    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

下面总结一下客户端调用过程
客户端必须完成以下步骤才能实现调用远程接口：
1.	在项目中包含.aidl文件。
2.	实现ServiceConnection。
3.	调用Context.bindService()，并传递ServiceConnection的实现。
4.	在onServiceConnected()实现中，我们可以接受IBinder的一个实例（名为service)。调用asInterface()转换成接口实例。调用在接口中定义的方法，必须要捕获RemoteException异常（当连接断开时），这是调用远程方法的唯一异常。
5.	调用Context.unBindService()解除连接。

