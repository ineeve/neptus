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
 * Author: Tiago Sá Marques (file creation and skeleton)
 * Author.
 * 2018/10/12
 */
package pt.lsts.neptus.plugins.noc.transports.messages;

import pt.lsts.neptus.messages.IMessageProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * This class handles serialization and deserialization of
 * {@link NocMessage}
 *
 * @see pt.lsts.imc.IMCDefinition
 * TODO-NOC imlement
 * */
public class NocMessageDefinition implements IMessageProtocol<NocMessage> {
    private static NocMessageDefinition def;


    public static NocMessageDefinition getInstance() {
        if (def == null) {
            def = new NocMessageDefinition();
        }

        return def;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public String version() {
        return null;
    }

    @Override
    public int serializationSize(NocMessage msg) {
        return 0;
    }

    @Override
    public void serialize(NocMessage m, OutputStream os) throws Exception {

    }

    @Override
    public NocMessage unserialize(InputStream is) throws Exception {
        return null;
    }

    @Override
    public NocMessage newMessage(int id) throws Exception {
        return null;
    }

    @Override
    public NocMessage newMessage(String name) throws Exception {
        return null;
    }

    @Override
    public Collection<String> getMessageNames() {
        return null;
    }

    @Override
    public int getMessageCount() {
        return 0;
    }

    @Override
    public String getMessageName(int id) throws Exception {
        return null;
    }

    @Override
    public int getMessageId(String name) throws Exception {
        return 0;
    }

    // TODO-NOC implement
    public NocMessage nextMessage(InputStream in) throws IOException {
        return null;
    }
}
