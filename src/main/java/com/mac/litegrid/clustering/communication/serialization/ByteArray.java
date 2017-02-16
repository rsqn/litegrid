package com.mac.litegrid.clustering.communication.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 26/11/13
 *
 * To change this template use File | Settings | File Templates.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByteArray {
   int getIndex();
}
