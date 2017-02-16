package com.mac.litegrid.clustering.communication.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public interface NetworkMessage {
    public Object getObject();
    public void setObject(String creator, long objectId, Object o);
    public String getCreator();
    public void setConfirmationRequired(boolean confirm);
    public boolean getConfirmationRequired();
    public long getObjectId();
}
