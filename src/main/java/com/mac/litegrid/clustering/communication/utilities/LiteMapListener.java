package com.mac.litegrid.clustering.communication.utilities;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 19/09/13
 *
 * To change this template use File | Settings | File Templates.
 */
public interface LiteMapListener<String,V> {

    void onUpdate(String k, V v);

    void onRemove(String k);
}
