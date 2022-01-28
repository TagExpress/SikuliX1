package org.sikuli.basics;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Collector {
    private static final Logger logger = Logger.getLogger(Collector.class.getName());
    private static final Set<Object> references = new HashSet<>();

    public static synchronized <T> T add(T reference) {
//        references.add(reference);
        return reference;
    }

    public static synchronized <T> Collection<T> addAll(Collection<T> referenceList) {
//        references.addAll(referenceList);
        return referenceList;
    }

    public static void release() {

        Map<Long,Mat> matReferences = new TreeMap<>();

        for (Object reference : references) {
            try {
                if (reference instanceof Mat) {
                    matReferences.put(((Mat)reference).nativeObj, ((Mat)reference));
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        for (Mat mat : matReferences.values()) {
            try {
                logger.log(Level.INFO, "deleting Mat:{0}", mat.nativeObj);
                mat.deleteNativeObject();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        references.clear();
    }
}
