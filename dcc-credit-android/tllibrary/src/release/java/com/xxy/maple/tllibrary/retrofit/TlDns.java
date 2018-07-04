package com.xxy.maple.tllibrary.retrofit;

import android.support.annotation.NonNull;
import okhttp3.Dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class TlDns implements Dns {
    @Override
    public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
        return Dns.SYSTEM.lookup(hostname);
    }

    public static void hackDnsImpl() {
        //do nothing for release
    }
}
