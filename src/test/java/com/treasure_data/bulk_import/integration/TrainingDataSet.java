package com.treasure_data.bulk_import.integration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.junit.Ignore;

@Ignore
public class TrainingDataSet {

    private static final SimpleDateFormat format;
    private static final Object lock = new Object();

    static {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        format.setTimeZone(TimeZone.getTimeZone("JST"));
    }

    protected long numRows;
    protected long baseTime;
    protected String[] availableHeader;
    protected Random rand = new Random(new Random().nextInt());

    public TrainingDataSet(long numRows, long baseTime, String[] availableHeader) {
        this.numRows = numRows;
        this.baseTime = baseTime;
        this.availableHeader = availableHeader;
    }

    public void createDataFiles(FileGenerator[] gens) throws IOException {
        for (int i = 0; i < gens.length; i++) {
            gens[i].writeHeader();
        }

        Map<String, Object> generatedRow = new HashMap<String, Object>();
        Map<String, Object> row = new HashMap<String, Object>();
        for (long i = 0; i < numRows; i++) {
            createRow(generatedRow, i);
            for (int j = 0; j < gens.length; j++) {
                extractColumns(gens[j], generatedRow, row);
                gens[j].write(row);
                row.clear();
            }
            generatedRow.clear();
        }

        for (int i = 0; i < gens.length; i++) {
            gens[i].close();
        }
    }

    private void createRow(Map<String, Object> row, long i) {
        for (int j = 0; j < availableHeader.length; j++) {
            if (availableHeader[j].startsWith("string-")) {
                row.put(availableHeader[j], "muga" + rand.nextInt(100));
            } else if (availableHeader[j].startsWith("int-")) {
                row.put(availableHeader[j], rand.nextInt());
            } else if (availableHeader[j].startsWith("double-")) {
                row.put(availableHeader[j], rand.nextDouble());
            } else if (availableHeader[j].startsWith("long-")) {
                row.put(availableHeader[j], rand.nextInt());
            } else if (availableHeader[j].equals("time")) {
                row.put(availableHeader[j], baseTime + 60 * i);
            } else if (availableHeader[j].equals("timestamp")) {
                row.put(availableHeader[j], baseTime + 60 * i);
            } else if (availableHeader[j].equals("timeformat")) {
                long t = (long)((baseTime + 60 * i) * 1000);
                String s = null;
                synchronized (lock) {
                    s = format.format(new Date(t));
                }
                row.put(availableHeader[j], s);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    private void extractColumns(FileGenerator gen, Map<String, Object> generatedRow, Map<String, Object> row) {
        for (int i = 0; i < gen.header.length; i++) {
            Object v = generatedRow.get(gen.header[i]);
            if (v == null) {
                throw new NullPointerException("something wrong...");
            }
            row.put(gen.header[i], v);
        }
    }
}