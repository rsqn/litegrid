package com.mac.litegrid.clustering.communication;

import com.mac.libraries.network.NetworkUtilities;
import com.mac.platform.util.StringUtil;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by mandrewes on 12/01/16.
 */
public class HostTest {


    @Test
    public void shouldGetLocalHostNotLocalHost() throws Exception {
        String ip = NetworkUtilities.getLocalIPAddressOnSubnet("192.");

        System.out.println(ip);
    }
}
