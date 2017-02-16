package com.mac.litegrid.clustering.communication.messages;

import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 2/12/2005
 *
 * To change this template use File | Settings | File Templates.
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * this is a Fake packet, used for internal communication between FragmentManager and LiteGrid ONLY. as the LiteGrid Queue expects only PetworkLayerPackets
 */
public class UnableToDeserializePacket implements NetworkMessage, Serializable {
    private static final long serialVersionUID = -8121091315484509052L;
    Object error;
    String creator;
    long OID;

    public UnableToDeserializePacket() {
    }

    public UnableToDeserializePacket(UnableToDeserializeError e) {
        error = e;
        creator = e.creator;
        OID = e.oid;
    }

    public Object getObject() {
        return error;
    }

    public void setObject(String creator, long objectId, Object o) {
        throw new RuntimeException("this is a Fake packet, used for internal communication between FragmentManager and LiteGrid ONLY.");
    }

    public String getCreator() {
        return creator;
    }

    public void setConfirmationRequired(boolean confirm) {
    }

    public boolean getConfirmationRequired() {
        return false;
    }

    public long getObjectId() {
        return OID;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setOID(long OID) {
        this.OID = OID;
    }
}
