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
package pt.lsts.neptus.comm.transports.noc;

import pt.lsts.neptus.messages.IMessage;
import pt.lsts.neptus.messages.IMessageProtocol;
import pt.lsts.neptus.messages.InvalidFieldException;
import pt.lsts.neptus.messages.InvalidMessageException;

import java.io.OutputStream;

/**
 * Base class for noc messages
 *
 * TODO-NOC implement
 * */
public abstract class NocMessage implements IMessage {
    String value;

    @Override
    public int getMgid() {
        return 0;
    }

    @Override
    public String getAbbrev() {
        return null;
    }

    @Override
    public String getLongName() {
        return null;
    }

    @Override
    public String[] getFieldNames() {
        return new String[0];
    }

    @Override
    public void validate() throws InvalidMessageException {

    }

    @Override
    public Object getValue(String fieldName) {
        return null;
    }

    @Override
    public String getAsString(String fieldName) {
        return null;
    }

    @Override
    public Number getAsNumber(String fieldName) {
        return null;
    }

    @Override
    public String getTypeOf(String fieldName) {
        return null;
    }

    @Override
    public String getUnitsOf(String fieldName) {
        return null;
    }

    @Override
    public boolean hasFlag(String flagName) {
        return false;
    }

    @Override
    public String getLongFieldName(String fieldName) {
        return null;
    }

    @Override
    public void setValue(String fieldName, Object value) throws InvalidFieldException {

    }

    @Override
    public IMessageProtocol<? extends IMessage> getProtocolFactory() {
        return null;
    }

    @Override
    public <M extends IMessage> M cloneMessage() {
        return null;
    }

    @Override
    public Object getHeaderValue(String field) {
        return null;
    }

    // TODO-NOC implement
    public double getTimestamp() {
        return 0;
    }

    public int serialize(OutputStream os) throws Exception {
        int nbytes = 0;
        NocMessageDefinition.getInstance().serialize(this, os);

        return nbytes;
    }

}
