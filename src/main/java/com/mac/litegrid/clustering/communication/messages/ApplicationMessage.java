package com.mac.litegrid.clustering.communication.messages;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 18/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationMessage implements Serializable {
    private static final long serialVersionUID = 5843277465093409049L;
    private List<Member> targetMembers;
    private Member originMember;
    private Object object;


    public ApplicationMessage() {
        targetMembers = new ArrayList<>();
    }

    public List<Member> getTargetMembers() {
        return targetMembers;
    }

    public void setTargetMembers(List<Member> targetMembers) {
        this.targetMembers = targetMembers;
    }

    public Member getOriginMember() {
        return originMember;
    }

    public void setOriginMember(Member originMember) {
        this.originMember = originMember;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
