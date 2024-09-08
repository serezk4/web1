package com.serezk4.server;

import com.fastcgi.FCGIInterface;
import com.serezk4.server.core.RequestHandler;
import com.serezk4.server.core.RequestHandlerImpl;
import com.serezk4.server.core.service.ContourServiceImpl;
import com.serezk4.server.core.validation.ValidationServiceImpl;
import com.serezk4.server.fcgi.FcgiInterfaceHolder;

/**
 * Main class of the application. It starts the FastCGI server and handles requests.
 *
 * @see FCGIInterface
 * @see FcgiInterfaceHolder
 * @see RequestHandler
 * @see RequestHandlerImpl
 * @see ContourServiceImpl
 * @see ValidationServiceImpl
 * @since 1.0
 */

public class FcgiServer {

    public static void main(String[] args) {
        RequestHandler requestHandler = new RequestHandlerImpl(new ContourServiceImpl(), new ValidationServiceImpl());
        requestHandler.handle();
    }

}
