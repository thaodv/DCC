package com.xxy.maple.tllibrary.retrofit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xxy.maple.tllibrary.BuildConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import okhttp3.Dns;

public class TlDns implements Dns {

    private static final InetAddress TL_SERVER_ADDRESS;
    private static final InetAddress TL_H5_ADDRESS;

    static {
        InetAddress server = null, h5 = null;
        try {
            server = InetAddress.getByName(BuildConfig.TL_SERVER_RESOLVE_IP);
            h5 = InetAddress.getByName(BuildConfig.TL_H5_RESOLVE_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        TL_SERVER_ADDRESS = server;
        TL_H5_ADDRESS = h5;
    }

    /**
     * 139.198.1.166    tlsiren.xiaoxinyong.com
     * 139.198.1.166    tleos.xiaoxinyong.com
     */
    @Override
    public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
        InetAddress resolveTl = resolveTl(hostname);
        if(resolveTl!=null){
            return Collections.singletonList(resolveTl);
        }
        return Dns.SYSTEM.lookup(hostname);
    }

    @Nullable
    private static InetAddress resolveTl(String host) {
        switch (host.toLowerCase()) {
            case BuildConfig.TL_SERVER_URL: {
                return TL_SERVER_ADDRESS;
            }
            case BuildConfig.TL_H5_URL: {
                return TL_H5_ADDRESS;
            }
            default: {
                return null;
            }
        }
    }
}
