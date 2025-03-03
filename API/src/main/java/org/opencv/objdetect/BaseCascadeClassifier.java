/*
 * Copyright (c) 2010-2020, sikuli.org, sikulix.com - MIT license
 */

//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.objdetect;

import org.opencv.core.Algorithm;

// C++: class BaseCascadeClassifier
//javadoc: BaseCascadeClassifier

public class BaseCascadeClassifier extends Algorithm {

    protected BaseCascadeClassifier(long addr) { super(addr); }

    // internal usage only
    public static BaseCascadeClassifier __fromPtr__(long addr) { return new BaseCascadeClassifier(addr); }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // native support for java finalize()
    private static native void delete(long nativeObj);

}
