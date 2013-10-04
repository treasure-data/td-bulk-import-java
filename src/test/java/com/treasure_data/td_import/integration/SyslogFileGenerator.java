package com.treasure_data.td_import.integration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Ignore;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

@Ignore
public class SyslogFileGenerator extends FileGenerator {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss");

    static Value HOST_VALUE = ValueFactory.createRawValue("host");
    static Value IDENT_VALUE = ValueFactory.createRawValue("ident");
    static Value MESSAGE_VALUE = ValueFactory.createRawValue("message");
    static Value PID_VALUE = ValueFactory.createRawValue("pid");

    protected static final String SPACE = " ";
    protected static final String LF = "\n";

    //"time", "host", "ident", "pid", "message"

    public SyslogFileGenerator(String fileName, String[] header)
            throws IOException {
        super(fileName, header);
    }

    @Override
    public void writeHeader() throws IOException {
        // non header
    }

    @Override
    public void write(Map<String, Object> map) throws IOException {
        StringBuilder sbuf = new StringBuilder();

        sbuf.append(dateFormat.format(new Date(((Long) map.get("time")) * 1000))).append(SPACE);
        sbuf.append(map.get("string-value")).append(SPACE);
        sbuf.append(map.get("string-value")).append("(").append(map.get("string-value")).append(")");
        sbuf.append("[").append(map.get("int-value")).append("]");
        sbuf.append(":").append(SPACE).append(map.get("string-value"));
        
        out.write(sbuf.toString().getBytes());
        out.write(LF.getBytes());
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
