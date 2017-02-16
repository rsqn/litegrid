package com.mac.litegrid.clustering.communication.utilities;

import com.mac.litegrid.clustering.communication.messages.Member;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 20/06/2005
 *
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class MemberComparator implements Comparator {

    public int compare (Object o1, Object o2) {
        Member m1 = (Member)o1;
        Member m2 = (Member)o2;

        String s1  = m1.toString();
        String s2 = m2.toString();

        return s1.compareTo(s2);
    }
}
