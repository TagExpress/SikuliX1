/*
 * Copyright (c) 2010-2020, sikuli.org, sikulix.com - MIT license
 */

//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.core;

import org.sikuli.basics.Collector;

import java.lang.String;

// C++: class Algorithm
//javadoc: Algorithm

public class Algorithm {

    protected final long nativeObj;
    protected Algorithm(long addr) {
        nativeObj = addr;
        Collector.add(this);
    }

    public long getNativeObjAddr() { return nativeObj; }

    // internal usage only
    public static Algorithm __fromPtr__(long addr) { return new Algorithm(addr); }

    //
    // C++:  String getDefaultName()
    //

    //javadoc: Algorithm::getDefaultName()
    public  String getDefaultName()
    {
        
        String retVal = getDefaultName_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  bool empty()
    //

    //javadoc: Algorithm::empty()
    public  boolean empty()
    {
        
        boolean retVal = empty_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void clear()
    //

    //javadoc: Algorithm::clear()
    public  void clear()
    {
        
        clear_0(nativeObj);
        
        return;
    }


    //
    // C++:  void read(FileNode fn)
    //

    // Unknown type 'FileNode' (I), skipping the function


    //
    // C++:  void save(String filename)
    //

    //javadoc: Algorithm::save(filename)
    public  void save(String filename)
    {
        
        save_0(nativeObj, filename);
        
        return;
    }

    public void deleteNativeObject() {
//        delete(nativeObj);
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }


    // C++:  String getDefaultName()
    private static native String getDefaultName_0(long nativeObj);

    // C++:  bool empty()
    private static native boolean empty_0(long nativeObj);

    // C++:  void clear()
    private static native void clear_0(long nativeObj);

    // C++:  void save(String filename)
    private static native void save_0(long nativeObj, String filename);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
