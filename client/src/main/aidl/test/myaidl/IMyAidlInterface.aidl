// IMyAidlInterface.aidl
package test.myaidl;
import test.myaidl.User;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   String getName();
   void addUser(in User user);
}
