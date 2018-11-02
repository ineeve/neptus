/*
 * Copyright (c) 2004-2017 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * Modified European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the Modified EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
 * and http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: Paulo Dias (Original implementation for IMC)
 * Author Tiago Sá Marques (adaption for NocMessage)
 * 2011/01/17
 * 2018/10/12
 */
package pt.lsts.neptus.plugins.noc.transports;

import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.comm.transports.DeliveryListener;
import pt.lsts.neptus.comm.transports.DeliveryListener.ResultEnum;
import pt.lsts.neptus.comm.transports.tcp.TCPMessageListener;
import pt.lsts.neptus.comm.transports.tcp.TCPNotification;
import pt.lsts.neptus.comm.transports.tcp.TCPTransport;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageInfoImpl;
import pt.lsts.neptus.messages.listener.MessageListener;
import pt.lsts.neptus.plugins.noc.transports.messages.NocAbort;
import pt.lsts.neptus.plugins.noc.transports.messages.NocMessage;
import pt.lsts.neptus.plugins.noc.transports.messages.NocMessageDefinition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * @author pdias
 * @author tsm
 *
 */
public class NocTcpTransport {

    private LinkedHashSet<NocMessageListener> listeners = new LinkedHashSet<>();
    private NocMessageDefinition nocDefinition;

    private TCPTransport tcpTransport = null;

    private int bindPort = 7011;

    final HashMap<String, TCPMessageProcessor> listProc = new HashMap<>();

    public NocTcpTransport(NocMessageDefinition nocDefinition) {
        this.nocDefinition = nocDefinition;
        getTcpTransport();
        setTCPListener();
    }

    /**
     * @return
     */
    public boolean isRunning() {
        return getTcpTransport().isRunning();
    }

    /**
     *
     */
    private void setTCPListener() {
        getTcpTransport().addListener(req -> {
            String id = req.getAddress().toString();
            TCPMessageProcessor proc = listProc.get(id);
            if (proc == null) {
                proc = new TCPMessageProcessor(id, listeners, nocDefinition);
                listProc.put(id, proc);
            }
            if (req.isEosReceived())
                listProc.remove(id);
            proc.onTCPMessageNotification(req);
        });
    }

    /**
     * @return the udpTransport
     */
    public TCPTransport getTcpTransport() {
        if (tcpTransport == null) {
            tcpTransport = new TCPTransport(bindPort);
        }
        return tcpTransport;
    }


    /**
     * @param listener
     * @return
     */
    public boolean addListener(NocMessageListener listener) {
        boolean ret = false;
        synchronized (listeners) {
            ret = listeners.add(listener);
        }
        return ret;
    }

    /**
     * @param listener
     * @return
     */
    public boolean removeListener(
            MessageListener<MessageInfo, NocMessage> listener) {
        boolean ret;
        synchronized (listeners) {
            ret = listeners.remove(listener);
        }
        return ret;
    }


    /**
     * @param message
     * @param deliveryListener
     */
    public boolean sendMessage(final NocMessage message,
                               final NocMessageDeliveryListener deliveryListener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int size = message.serialize(baos);
            DeliveryListener listener = null;
            if (deliveryListener != null) {
                listener = (result, error) -> {
                    switch (result) {
                        case Success:
                            deliveryListener.deliverySuccess(message);
                            break;
                        case Error:
                            deliveryListener.deliveryError(message, error);
                            break;
                        case TimeOut:
                            deliveryListener.deliveryTimeOut(message);
                            break;
                        case Unreacheable:
                            deliveryListener.deliveryUnreacheable(message);
                            break;
                        default:
                            deliveryListener.deliveryError(message, new Exception("Delivery "
                                    + ResultEnum.UnFinished));
                            break;
                    }
                };
            }

            boolean ret = getTcpTransport().sendMessage(baos.toByteArray(), listener);
            if (!ret) {
                if (deliveryListener != null) {
                    deliveryListener.deliveryError(message, new Exception("Delivery "
                            + ResultEnum.UnFinished + " due to closing transport!"));
                }
            }
            return ret;
        } catch (Exception e) {
            NeptusLog.pub().error(e);
            if (deliveryListener != null) {
                deliveryListener.deliveryError(message, e);
            }
            return false;
        }
    }

    /**
     *
     */
    public void stop() {
        getTcpTransport().stop();
        stopAndCleanReceiveProcessors();
    }

    /**
     * Call only after stop or purge.
     */
    private void stopAndCleanReceiveProcessors() {
        for (TCPMessageProcessor proc : listProc.values()) {
            proc.cleanup();
        }
        listProc.clear();
    }

    /**
     * @author pdias
     *
     */
    static class TCPMessageProcessor implements TCPMessageListener, // GzLsf2Llf.MessageListener,
            Comparable<TCPMessageProcessor> {
        String id = "";
        PipedOutputStream pos;
        PipedInputStream pis;

        // Needed because the pis.available() not always when return '0' means end of stream
        boolean isInputClosed = false;

        String host = "";
        int port = 0;

        LinkedHashSet<NocMessageListener> listeners;
        NocMessageDefinition nocDefinition;

        public TCPMessageProcessor(String id, LinkedHashSet<NocMessageListener> listeners,
                                   NocMessageDefinition nocDefinition) {
            this.nocDefinition = nocDefinition;
            this.id = id;
            this.listeners = listeners;
            pos = new PipedOutputStream();
            try {
                pis = new PipedInputStream(pos);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            final NocMessageDefinition nocDef = nocDefinition;
            new Thread(NocTcpTransport.class.getSimpleName() + " :: " + TCPMessageProcessor.class.getSimpleName() + "(" + TCPMessageProcessor.this.hashCode() + ")") {
                @Override
                public void run() {
                    try {
                        NeptusLog.pub().debug("New data");
                        while(!isInputClosed && pis.available() >= 0) { // the pis.available() not always when return '0' means end of stream

                            if (pis.available() == 0) {
                                try { Thread.sleep(20); } catch (InterruptedException e) { }
                                continue;
                            }
                            try {
                                NocMessage msg = nocDef.nextMessage(pis);
                                if (msg != null)
                                    msgArrived(/*(long) timeMillis,*/ msg);
                            }
                            catch (IOException e) {
                                
                            }
                        }
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        @Override
        public void onTCPMessageNotification(TCPNotification req) {
            host = req.getAddress().getAddress().getHostAddress();
            port = req.getAddress().getPort();

            try {
                if (req.isEosReceived()) {
                    isInputClosed = true;
                    pos.flush();
                    pos.close();
                }
                else {
                    pos.write(req.getBuffer());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Calling this will invalidate the instance for future use.
         */
        public void cleanup() {
            try {
                pos.flush();
                pos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            isInputClosed = true;
        }

        public void msgArrived(/*long timeStampMillis,*/ NocMessage msg) {
            MessageInfo info = new MessageInfoImpl();
            info.setPublisher(host);
            info.setPublisherInetAddress(host);
            info.setPublisherPort(port);
            //FIXME time here is in milliseconds and MiddlewareMessageInfo is in nanoseconds
//            info.setTimeReceivedNanos(req.getTimeMillis() * (long)1E6);
            info.setTimeReceivedNanos(System.currentTimeMillis() * (long)1E6);
            info.setTimeSentNanos((long)msg.getTimestamp() * (long)1E9);
            info.setProperty(MessageInfo.TRANSPORT_MSG_KEY, "TCP");

            for (NocMessageListener lst : listeners) {
                try {
                    lst.onMessage(msg);
                }
                catch (Exception e) {
                    NeptusLog.pub().error(e);
                }
                catch (Error e) {
                    NeptusLog.pub().error(e);
                }
            }
        }

        @Override
        public int compareTo(TCPMessageProcessor o) {
            return id.compareTo(o.id);
        }
    }


    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
        NocTcpTransport nocTcp = new NocTcpTransport(NocMessageDefinition.getInstance());
        Thread.sleep(5000);

        NocAbort abort = new NocAbort();
        boolean ret = nocTcp.sendMessage(abort, new NocMessageDeliveryListener(10000));

        if (!ret)
            NeptusLog.pub().error("Failed to send NocAbort");

        while (nocTcp.isRunning()) {}
    }
}
