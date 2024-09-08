/*
 * @(#)FCGIMessage.java
 *
 *
 *      FastCGi compatibility package Interface
 *
 *
 *  Copyright (c) 1996 Open Market, Inc.
 *
 * See the file "LICENSE.TERMS" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 * $Id: FCGIMessage.java,v 1.4 2000/10/02 15:09:07 robs Exp $
 */
package com.serezk4.server.fcgi.message;

import com.serezk4.server.fcgi.config.FcgiGlobalDefs;
import com.serezk4.server.fcgi.io.FcgiInputStream;

import java.io.*;
import java.util.Properties;

/* This class handles reading and building the fastcgi messages.
 * For reading incoming mesages, we pass the input
 * stream as a param to the constructor rather than to each method.
 * Methods that build messages use and return internal buffers, so they
 * dont need a stream.
 */

public class FcgiMessage {
    private static final String RCSID = "$Id: FCGIMessage.java,v 1.4 2000/10/02 15:09:07 robs Exp $";

    /*
     * Instance variables
     */
    /*
     * FCGI Message Records
     * The logical structures of the FCGI Message Records.
     * Fields are originally 1 unsigned byte in message
     * unless otherwise noted.
     */
    /*
     * FCGI Header
     */
    private int h_version;
    private int h_type;
    private int h_requestID;       // 2 bytes
    private int h_contentLength;   // 2 bytes
    private int h_paddingLength;
    /*
     * FCGI BeginRequest body.
     */
    private int br_role;      // 2 bytes
    private int br_flags;

    private FcgiInputStream in;

    /*
     * constructor - Java would do this implicitly.
     */
    public FcgiMessage() {
        super();
    }

    /*
     * constructor - get the stream.
     */
    public FcgiMessage(FcgiInputStream instream) {
        in = instream;
    }

    /*
     * Message Reading Methods
     */

    /*
     * Interpret the FCGI Message Header. Processes FCGI
     * BeginRequest and Management messages. Param hdr is the header.
     * The calling routine has to keep track of the stream reading
     * management or use FCGIInputStream.fill() which does just that.
     */
    public int processHeader(byte[] hdr) throws IOException {
        processHeaderBytes(hdr);
        if (h_version != FcgiGlobalDefs.def_FCGIVersion1) {
            return (FcgiGlobalDefs.def_FCGIUnsupportedVersion);
        }
        in.contentLen = h_contentLength;
        in.paddingLen = h_paddingLength;
        if (h_type == FcgiGlobalDefs.def_FCGIBeginRequest) {
            return processBeginRecord(h_requestID);
        }
        if (h_requestID == FcgiGlobalDefs.def_FCGINullRequestID) {
            return processManagementRecord(h_type);
        }
        if (h_requestID != in.request.requestID) {
            return (FcgiGlobalDefs.def_FCGISkip);
        }
        if (h_type != in.type) {
            return (FcgiGlobalDefs.def_FCGIProtocolError);
        }
        return (FcgiGlobalDefs.def_FCGIStreamRecord);
    }

    /* Put the unsigned bytes in the incoming FCGI header into
     * integer form for Java, concatinating bytes when needed.
     * Because Java has no unsigned byte type, we have to be careful
     * about signed numeric promotion to int.
     */
    private void processHeaderBytes(byte[] hdrBuf) {
        h_version = hdrBuf[0] & 0xFF;
        h_type = hdrBuf[1] & 0xFF;
        h_requestID = ((hdrBuf[2] & 0xFF) << 8) | (hdrBuf[3] & 0xFF);
        h_contentLength = ((hdrBuf[4] & 0xFF) << 8) | (hdrBuf[5] & 0xFF);
        h_paddingLength = hdrBuf[6] & 0xFF;
    }

    /*
     * Reads FCGI Begin Request Record.
     */
    public int processBeginRecord(int requestID) throws IOException {
        byte beginReqBody[];
        byte endReqMsg[];
        if (requestID == 0 || in.contentLen
                != FcgiGlobalDefs.def_FCGIEndReqBodyLen) {
            return FcgiGlobalDefs.def_FCGIProtocolError;
        }
        /*
         * If the webserver is multiplexing the connection,
         * this library can't deal with it, so repond with
         * FCGIEndReq message with protocolStatus FCGICantMpxConn
         */
        if (in.request.isBeginProcessed) {
            endReqMsg = new byte[FcgiGlobalDefs.def_FCGIHeaderLen
                    + FcgiGlobalDefs.def_FCGIEndReqBodyLen];
            System.arraycopy(makeHeader(
                            FcgiGlobalDefs.def_FCGIEndRequest,
                            requestID,
                            FcgiGlobalDefs.def_FCGIEndReqBodyLen,
                            0), 0, endReqMsg, 0,
                    FcgiGlobalDefs.def_FCGIHeaderLen);
            System.arraycopy(makeEndrequestBody(0,
                            FcgiGlobalDefs.def_FCGICantMpxConn), 0,
                    endReqMsg,
                    FcgiGlobalDefs.def_FCGIHeaderLen,
                    FcgiGlobalDefs.def_FCGIEndReqBodyLen);
            /*
             * since isBeginProcessed is first set below,this
             * can't be out first call, so request.out is properly set
             */
            try {
                in.request.outStream.write(endReqMsg, 0,
                        FcgiGlobalDefs.def_FCGIHeaderLen
                                + FcgiGlobalDefs.def_FCGIEndReqBodyLen);
            } catch (IOException e) {
                in.request.outStream.setException(e);
                return -1;
            }
        }
        /*
         * Accept this  new request. Read the record body
         */
        in.request.requestID = requestID;
        beginReqBody =
                new byte[FcgiGlobalDefs.def_FCGIBeginReqBodyLen];
        if (in.read(beginReqBody, 0,
                FcgiGlobalDefs.def_FCGIBeginReqBodyLen) !=
                FcgiGlobalDefs.def_FCGIBeginReqBodyLen) {
            return FcgiGlobalDefs.def_FCGIProtocolError;
        }
        br_flags = beginReqBody[2] & 0xFF;
        in.request.keepConnection
                = (br_flags & FcgiGlobalDefs.def_FCGIKeepConn) != 0;
        br_role = ((beginReqBody[0] & 0xFF) << 8) | (beginReqBody[1] & 0xFF);
        in.request.role = br_role;
        in.request.isBeginProcessed = true;
        return FcgiGlobalDefs.def_FCGIBeginRecord;
    }

    /*
     * Reads and Responds to a Management Message. The only type of
     * management message this library understands is FCGIGetValues.
     * The only variables that this library's FCGIGetValues understands
     * are def_FCGIMaxConns, def_FCGIMaxReqs, and def_FCGIMpxsConns.
     * Ignore the other management variables, and repsond to other
     * management messages with FCGIUnknownType.
     */
    public int processManagementRecord(int type) throws IOException {

        byte[] response = new byte[64];
        int wrndx = response[FcgiGlobalDefs.def_FCGIHeaderLen];
        int value, len, plen;
        if (type == FcgiGlobalDefs.def_FCGIGetValues) {
            Properties tmpProps = new Properties();
            readParams(tmpProps);

            if (in.getFCGIError() != 0 || in.contentLen != 0) {
                return FcgiGlobalDefs.def_FCGIProtocolError;
            }
            if (tmpProps.containsKey(
                    FcgiGlobalDefs.def_FCGIMaxConns)) {
                makeNameVal(
                        FcgiGlobalDefs.def_FCGIMaxConns, "1",
                        response, wrndx);
            } else {
                if (tmpProps.containsKey(
                        FcgiGlobalDefs.def_FCGIMaxReqs)) {
                    makeNameVal(
                            FcgiGlobalDefs.def_FCGIMaxReqs, "1",
                            response, wrndx);
                } else {
                    if (tmpProps.containsKey(
                            FcgiGlobalDefs.def_FCGIMaxConns)) {
                        makeNameVal(
                                FcgiGlobalDefs.def_FCGIMpxsConns, "0",
                                response, wrndx);
                    }
                }
            }
            plen = 64 - wrndx;
            len = wrndx - FcgiGlobalDefs.def_FCGIHeaderLen;
            System.arraycopy(makeHeader(
                            FcgiGlobalDefs.def_FCGIGetValuesResult,
                            FcgiGlobalDefs.def_FCGINullRequestID,
                            len, plen), 0,
                    response, 0,
                    FcgiGlobalDefs.def_FCGIHeaderLen);
        } else {
            plen = len =
                    FcgiGlobalDefs.def_FCGIUnknownBodyTypeBodyLen;
            System.arraycopy(makeHeader(
                            FcgiGlobalDefs.def_FCGIUnknownType,
                            FcgiGlobalDefs.def_FCGINullRequestID,
                            len, 0), 0,
                    response, 0,
                    FcgiGlobalDefs.def_FCGIHeaderLen);
            System.arraycopy(makeUnknownTypeBodyBody(h_type), 0,
                    response,
                    FcgiGlobalDefs.def_FCGIHeaderLen,
                    FcgiGlobalDefs.def_FCGIUnknownBodyTypeBodyLen);
        }
        /*
         * No guarantee that we have a request yet, so
         * dont use fcgi output stream to reference socket, instead
         * use the FileInputStream that refrences it. Also
         * nowhere to save exception, since this is not FCGI stream.
         */

        try {
            in.request.socket.getOutputStream().write(response, 0,
                    FcgiGlobalDefs.def_FCGIHeaderLen +
                            FcgiGlobalDefs.def_FCGIUnknownBodyTypeBodyLen);

        } catch (IOException e) {
            return -1;
        }
        return FcgiGlobalDefs.def_FCGIMgmtRecord;
    }

    /*
     * Makes a name/value with name = string of some length, and
     * value a 1 byte integer. Pretty specific to what we are doing
     * above.
     */
    void makeNameVal(String name, String value, byte[] dest, int pos) {
        int nameLen = name.length();
        if (nameLen < 0x80) {
            dest[pos++] = (byte) nameLen;
        } else {
            dest[pos++] = (byte) (((nameLen >> 24) | 0x80) & 0xff);
            dest[pos++] = (byte) ((nameLen >> 16) & 0xff);
            dest[pos++] = (byte) ((nameLen >> 8) & 0xff);
            dest[pos++] = (byte) nameLen;
        }
        int valLen = value.length();
        if (valLen < 0x80) {
            dest[pos++] = (byte) valLen;
        } else {
            dest[pos++] = (byte) (((valLen >> 24) | 0x80) & 0xff);
            dest[pos++] = (byte) ((valLen >> 16) & 0xff);
            dest[pos++] = (byte) ((valLen >> 8) & 0xff);
            dest[pos++] = (byte) valLen;
        }

        try {
            System.arraycopy(name.getBytes("UTF-8"), 0, dest, pos, nameLen);
            pos += nameLen;

            System.arraycopy(value.getBytes("UTF-8"), 0, dest, pos, valLen);
            pos += valLen;
        } catch (UnsupportedEncodingException x) {
        }
    }

    /*
     * Read FCGI name-value pairs from a stream until EOF. Put them
     * into a Properties object, storing both as strings.
     */
    public int readParams(Properties props) throws IOException {
        int nameLen, valueLen;
        byte lenBuff[] = new byte[3];
        int i = 1;

        while ((nameLen = in.read()) != -1) {
            i++;
            if ((nameLen & 0x80) != 0) {
                if ((in.read(lenBuff, 0, 3)) != 3) {
                    in.setFCGIError(
                            FcgiGlobalDefs.def_FCGIParamsError);
                    return -1;
                }
                nameLen = ((nameLen & 0x7f) << 24)
                        | ((lenBuff[0] & 0xFF) << 16)
                        | ((lenBuff[1] & 0xFF) << 8)
                        | (lenBuff[2] & 0xFF);
            }

            if ((valueLen = in.read()) == -1) {
                in.setFCGIError(
                        FcgiGlobalDefs.def_FCGIParamsError);
                return -1;
            }
            if ((valueLen & 0x80) != 0) {
                if ((in.read(lenBuff, 0, 3)) != 3) {
                    in.setFCGIError(
                            FcgiGlobalDefs.def_FCGIParamsError);
                    return -1;
                }
                valueLen = ((valueLen & 0x7f) << 24)
                        | ((lenBuff[0] & 0xFF) << 16)
                        | ((lenBuff[1] & 0xFF) << 8)
                        | (lenBuff[2] & 0xFF);
            }

            /*
             * nameLen and valueLen are now valid; read the name
             * and the value from the stream and construct a standard
             * environmental entity
             */
            byte[] name = new byte[nameLen];
            byte[] value = new byte[valueLen];
            if (in.read(name, 0, nameLen) != nameLen) {
                in.setFCGIError(
                        FcgiGlobalDefs.def_FCGIParamsError);
                return -1;
            }

            if (in.read(value, 0, valueLen) != valueLen) {
                in.setFCGIError(
                        FcgiGlobalDefs.def_FCGIParamsError);
                return -1;
            }
            String strName = new String(name);
            String strValue = new String(value);
            props.put(strName, strValue);
        }
        return 0;


    }
    /*
     * Message Building Methods
     */

    /*
     * Build an FCGI Message Header -
     */
    public byte[] makeHeader(int type,
                             int requestId,
                             int contentLength,
                             int paddingLength) {
        byte[] header = new byte[FcgiGlobalDefs.def_FCGIHeaderLen];
        header[0] = (byte) FcgiGlobalDefs.def_FCGIVersion1;
        header[1] = (byte) type;
        header[2] = (byte) ((requestId >> 8) & 0xff);
        header[3] = (byte) ((requestId) & 0xff);
        header[4] = (byte) ((contentLength >> 8) & 0xff);
        header[5] = (byte) ((contentLength) & 0xff);
        header[6] = (byte) paddingLength;
        header[7] = 0;  //reserved byte
        return header;
    }

    /*
     * Build an FCGI Message End Request Body
     */
    public byte[] makeEndrequestBody(int appStatus, int protocolStatus) {
        byte body[] = new byte[FcgiGlobalDefs.def_FCGIEndReqBodyLen];
        body[0] = (byte) ((appStatus >> 24) & 0xff);
        body[1] = (byte) ((appStatus >> 16) & 0xff);
        body[2] = (byte) ((appStatus >> 8) & 0xff);
        body[3] = (byte) ((appStatus) & 0xff);
        body[4] = (byte) protocolStatus;
        for (int i = 5; i < 8; i++) {
            body[i] = 0;
        }
        return body;
    }

    /*
     * Build an FCGI Message UnknownTypeBodyBody
     */
    public byte[] makeUnknownTypeBodyBody(int type) {
        byte body[] =
                new byte[FcgiGlobalDefs.def_FCGIUnknownBodyTypeBodyLen];
        body[0] = (byte) type;
        for (int i = 1;
             i < FcgiGlobalDefs.def_FCGIUnknownBodyTypeBodyLen; i++) {
            body[i] = 0;
        }
        return body;
    }

} //end class
