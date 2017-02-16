package com.mac.litegrid.clustering.communication.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 19/07/13
 *
 * To change this template use File | Settings | File Templates.
 */
public abstract class FilteringMGroupListener<T> implements MGroupListener<Object> {

    public abstract Class[] getTypes();

    public abstract  void onFilteredEvent(T o);

    private boolean interested(Object o) {
        for (Class aClass : getTypes()) {
            if ( aClass.isAssignableFrom(o.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void event(Object o) {
        if ( interested(o)) {
            onFilteredEvent((T)o);
        }
    }
}
