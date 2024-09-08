package com.serezk4.server.fcgi;

import com.fastcgi.FCGIInterface;

/**
 * This class is a holder for the FastCGI interface instance.
 * Used to prevent multiple instances of the FastCGI interface.
 *
 * @see FCGIInterface
 * @since 1.0
 */
public class FcgiInterfaceHolder {
    private static volatile FCGIInterface instance = null;

    public static FCGIInterface getInstance() {
        if (instance == null) {
            instance = new FCGIInterface();
        }
        return instance;
    }
}
