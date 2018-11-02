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
package pt.lsts.neptus.plugins.noc.transports;

import com.google.common.eventbus.Subscribe;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.neptus.plugins.NeptusProperty;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.noc.transports.messages.NocMessage;
import pt.lsts.neptus.plugins.noc.transports.messages.NocMessageDefinition;


@PluginDescription(name = "NOC Message Manager")
public class NocMsgManager extends ConsolePanel implements NocMessageListener{
    @NeptusProperty(name = "Communications timeout (Milliseconds)", description = "When sending a message use this value as a timeout")
    public int timeoutMillis = 1000;

    private final NocMessageDefinition nocDefinition = NocMessageDefinition.getInstance();
    private final NocTcpTransport tcpTransport = new NocTcpTransport(nocDefinition);

    public NocMsgManager(ConsoleLayout console) {
        super(console);
        tcpTransport.addListener(this);
    }

    @Subscribe
    public void consume(NocMessage msg) {
        // TODO catch all NocMessages on the bus and (perhaps) send them to the network
        tcpTransport.sendMessage(msg, new NocMessageDeliveryListener(timeoutMillis));
    }

    @Override
    public void cleanSubPanel() {

    }

    @Override
    public void initSubPanel() {

    }

    @Override
    public void onMessage(NocMessage msg) {
        post(msg);
    }
}
