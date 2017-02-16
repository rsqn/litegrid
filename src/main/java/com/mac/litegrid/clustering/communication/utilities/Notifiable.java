package com.mac.litegrid.clustering.communication.utilities;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 8/10/13
 *
 * To change this template use File | Settings | File Templates.
 */
public interface Notifiable<T> {
    NotifiableStatus notify(T t);
}
