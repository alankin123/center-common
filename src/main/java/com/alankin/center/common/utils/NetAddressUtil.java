package com.alankin.center.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NetAddressUtil
{

    private static final String LOCALHOST_IP = "127.0.0.1";

    private static final String EMPTY_IP = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("[0-9]{1,3}(\\.[0-9]{1,3}){3,}");

    private static InetAddress localAddress;

    private static String localIpAddress;

    private NetAddressUtil()
    {

    }

    public static InetAddress getLocalAddress()
    {
        if (localAddress != null)
        {
            return localAddress;
        }
        try
        {
            InetAddress local = InetAddress.getLocalHost();
            if (isValidAddress(local))
            {
                localAddress = local;
                return localAddress;
            }
        }
        catch (Throwable throwable)
        {
        }
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null)
            {
                while (interfaces.hasMoreElements())
                {
                    try
                    {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null)
                        {
                            while (addresses.hasMoreElements())
                            {
                                try
                                {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address))
                                    {
                                        localAddress = address;
                                        return localAddress;
                                    }
                                }
                                catch (Throwable throwable)
                                {

                                }
                            }
                        }
                    }
                    catch (Throwable throwable)
                    {

                    }
                }
            }
        }
        catch (Throwable throwable)
        {

        }

        return localAddress;
    }

    public static String getIpAddress()
    {
        if (localIpAddress != null)
        {
            return localIpAddress;
        }
        InetAddress address = getLocalAddress();
        if (isValidAddress(address))
        {
            localIpAddress = address.getHostAddress();
        }
        return localIpAddress;
    }

    public static boolean isValidAddress(InetAddress address)
    {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null && !EMPTY_IP.equals(name) && !LOCALHOST_IP.equals(name) && IP_PATTERN.matcher(name)
            .matches());
    }

    public static boolean isValidAddress(String address)
    {
        return (address != null && !EMPTY_IP.equals(address) && !LOCALHOST_IP.equals(address) && IP_PATTERN.matcher(
            address).matches());
    }
}
