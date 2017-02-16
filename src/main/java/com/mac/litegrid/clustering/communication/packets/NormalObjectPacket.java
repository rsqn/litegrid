package com.mac.litegrid.clustering.communication.packets;

import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 10/01/2005
 *
 * To change this template use File | Settings | File Templates.
 * <p/>
 * <p/>
 * mm frags...
 */
public class NormalObjectPacket implements NetworkMessage, Serializable {
    private String creator = "";
    private long objectId;
    private Object object = null;
    private boolean confirmationRequired;

    @Override
    public void setObject(String creator, long objectId, Object o) {
        this.creator = creator;
        this.objectId = objectId;
        this.object = o;
    }

    @Override
    public void setConfirmationRequired(boolean confirm) {
        this.confirmationRequired = confirm;
    }

    @Override
    public boolean getConfirmationRequired() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
