package com.serezk4.server.fcgi.fck;

import com.fastcgi.FCGIInterface;

/**
 * Fcgi interface factory.
 */
public final class FcgiInterfaceFactory {
    private static FCGIInterface fcgiInterface = new FCGIInterface();

    private FcgiInterfaceFactory() {}

    public static FCGIInterface getInstance() {return fcgiInterface == null ? fcgiInterface = new FCGIInterface() : fcgiInterface;}
}
