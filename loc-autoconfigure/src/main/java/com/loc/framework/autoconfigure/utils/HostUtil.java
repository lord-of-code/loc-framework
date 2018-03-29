package com.loc.framework.autoconfigure.utils;

import com.google.common.base.Strings;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created on 2018/3/29.
 */
public interface HostUtil {

  String HOST_IP = getHostIp(getLocalHostAddress());

  static InetAddress getLocalHostAddress() {
    try {
      InetAddress candidateAddress = null;
      for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
          ifaces.hasMoreElements(); ) {
        NetworkInterface iface = ifaces.nextElement();
        for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
            inetAddrs.hasMoreElements(); ) {
          InetAddress inetAddr = inetAddrs.nextElement();
          if (!inetAddr.isLoopbackAddress()) {
            if (inetAddr.isSiteLocalAddress()) {
              return inetAddr;
            } else if (candidateAddress == null) {
              candidateAddress = inetAddr;
            }
          }
        }
      }
      if (candidateAddress != null) {
        return candidateAddress;
      }
      InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
      if (jdkSuppliedAddress == null) {
        throw new UnknownHostException(
            "The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
      }
      return jdkSuppliedAddress;
    } catch (Exception e) {
      throw new RuntimeException("Failed to determine LAN address: " + e);
    }
  }

  static String getHostIp(InetAddress netAddress) {
    if (null == netAddress) {
      return null;
    }
    return netAddress.getHostAddress(); //get the ip address
  }

  static String getHost(String pidHost) {
    int index = pidHost.indexOf('@');
    String retHost = String.valueOf(System.currentTimeMillis());
    if (index == -1) {
      retHost = pidHost;
    } else if (index + 1 <= pidHost.length()) {
      retHost = pidHost.substring(index + 1);
    }
    return retHost.replaceAll("\\.", "_");
  }

  static String getMXBeanName() {
    String pidHost = ManagementFactory.getRuntimeMXBean().getName();
    if (Strings.isNullOrEmpty(pidHost)) {
      return String.valueOf(System.currentTimeMillis());
    }
    return pidHost;
  }
}
