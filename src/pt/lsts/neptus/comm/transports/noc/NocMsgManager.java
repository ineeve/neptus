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
 * Author: Tiago Sá Marques (first implementation)
 * Author.
 * 2018/10/12
 */
package pt.lsts.neptus.comm.transports.noc;

import com.google.common.eventbus.Subscribe;
import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageInfoImpl;
import pt.lsts.neptus.messages.listener.MessageListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class NocMsgManager implements MessageListener<MessageInfo, NocMessage> {
    private static NocMsgManager manager = null;
    //private NocTcpTransport tcpTransport = null;

    private final HashMap<String, HashMap<Object, Method>> callbacks = new HashMap<>();

    private NocMsgManager() {
        //tcpTransport = new NocTcpTransport(6002);
        //tcpTransport.addListener(this);
    }

    @Override
    public void onMessage(MessageInfo info, NocMessage msg) {
        synchronized (callbacks) {
            for (Map.Entry<Object, Method> entry : callbacks.get(msg.getClass().getName()).entrySet()) {
                try {
                    entry.getValue().invoke(entry.getKey(), msg);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Collect all methods that subscribe to specific NocMessage's
     * FIXME for not it doesn't call methods that subscribe to super-
     *       -class NocMessage, just derived classes
     * */
    public void register(Object registree) {
        List<Method> methodCallbacks = Arrays.stream(registree.getClass().getDeclaredMethods())
                .filter(m -> m.getAnnotation(Subscribe.class) != null &&
                        m.getParameterCount() == 1 &&
                        m.getParameters()[0].getType().getSuperclass() == NocMessage.class)
                .collect(Collectors.toList());

        for (Method m : methodCallbacks) {
            String paramTypeName = m.getParameters()[0].getType().getName();
            synchronized (callbacks) {
                HashMap<Object, Method> paramCallbacks = this.callbacks.getOrDefault(paramTypeName, new HashMap<>());

                paramCallbacks.put(registree, m);
                callbacks.putIfAbsent(paramTypeName, paramCallbacks);
            }

            NeptusLog.pub().debug("--> Method " + m.getName() + " " + m.getParameters()[0].getName() + " " + paramTypeName);
        }
    }

    public static NocMsgManager getManager() {
        if (manager == null)
            manager = new NocMsgManager();

        return manager;
    }

    private static class TestClass {

        @Subscribe
        void myMethod(NocAbort msg) {
            System.out.println("NocAbort!!");
        }

        @Subscribe
        void myMethod(NocAnnounce msg) {
            System.out.println("Noc announce!!!!");
        }
    }

    public static void main(String[] args) {
        TestClass t = new TestClass();
        NocAbort abort = new NocAbort();
        NocAnnounce announce = new NocAnnounce();
        NocMsgManager.getManager().register(t);

        MessageInfoImpl info = new MessageInfoImpl();
        NocMsgManager.getManager().onMessage(info, abort);
        NocMsgManager.getManager().onMessage(info, announce);
    }
}
