package pt.lsts.neptus.comm.transports.noc;

import pt.lsts.neptus.messages.IMessage;
import pt.lsts.neptus.messages.IMessageProtocol;
import pt.lsts.neptus.messages.InvalidFieldException;
import pt.lsts.neptus.messages.InvalidMessageException;

// Dummy message
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
}
