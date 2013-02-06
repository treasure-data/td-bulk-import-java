package com.treasure_data.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.treasure_data.commands.CommandException;
import com.treasure_data.commands.Config;
import com.treasure_data.commands.bulk_import.CSVPreparePartsRequest;
import com.treasure_data.commands.bulk_import.PreparePartsRequest;
import com.treasure_data.file.CSVFileParser;
import com.treasure_data.file.MsgpackGZIPFileWriter;
import com.treasure_data.file.CSVFileParser.TypeSuggestionProcessor;

public class TestCSVFileParser {

    @Test
    public void testTypeSuggestion() throws Exception {
        int hintScore = 3;
        {
            String[] values = new String[] {
                    "v0\n", "v1\n", "v2\n", "v3\n", "v4\n",
            };
            StringBuilder sbuf = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                sbuf.append(values[i]);
            }

            String text = sbuf.toString();
            byte[] bytes = text.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            CsvPreference pref = new CsvPreference.Builder('"', ',', "\n").build();
            CsvListReader sampleReader = new CsvListReader(
                    new InputStreamReader(in), pref);

            TypeSuggestionProcessor TSP = new TypeSuggestionProcessor(
                    values.length, hintScore);
            TSP.addHint("string");
            CellProcessor[] procs = new CellProcessor[] { TSP };

            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.close();

            assertEquals(TSP.getScore(CSVFileParser.INT), 0);
            assertEquals(TSP.getScore(CSVFileParser.LONG), 0);
            assertEquals(TSP.getScore(CSVFileParser.DOUBLE), 0);
            assertEquals(TSP.getScore(CSVFileParser.STRING), hintScore
                    + values.length);

            assertEquals(CSVFileParser.STRING, TSP.getSuggestedType());
        }
        {
            String[] values = new String[] { "v0\n", "v1\n", "v2\n", "v3\n",
                    "v4\n", };
            StringBuilder sbuf = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                sbuf.append(values[i]);
            }

            String text = sbuf.toString();
            byte[] bytes = text.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            CsvPreference pref = new CsvPreference.Builder('"', ',', "\n")
                    .build();
            CsvListReader sampleReader = new CsvListReader(
                    new InputStreamReader(in), pref);

            TypeSuggestionProcessor TSP = new TypeSuggestionProcessor(
                    values.length, hintScore);
            TSP.addHint("int"); // int
            CellProcessor[] procs = new CellProcessor[] { TSP };

            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.close();

            assertEquals(TSP.getScore(CSVFileParser.INT), hintScore);
            assertEquals(TSP.getScore(CSVFileParser.LONG), 0);
            assertEquals(TSP.getScore(CSVFileParser.DOUBLE), 0);
            assertEquals(TSP.getScore(CSVFileParser.STRING), values.length);

            assertEquals(CSVFileParser.STRING, TSP.getSuggestedType());
        }
        {
            String[] values = new String[] { "v0\n", "v1\n", "v2\n", "v3\n",
                    "v4\n", };
            StringBuilder sbuf = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                sbuf.append(values[i]);
            }

            String text = sbuf.toString();
            byte[] bytes = text.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            CsvPreference pref = new CsvPreference.Builder('"', ',', "\n")
                    .build();
            CsvListReader sampleReader = new CsvListReader(
                    new InputStreamReader(in), pref);

            TypeSuggestionProcessor TSP = new TypeSuggestionProcessor(
                    values.length, hintScore);
            TSP.addHint("long");
            CellProcessor[] procs = new CellProcessor[] { TSP };

            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.close();

            assertEquals(TSP.getScore(CSVFileParser.INT), 0);
            assertEquals(TSP.getScore(CSVFileParser.LONG), hintScore);
            assertEquals(TSP.getScore(CSVFileParser.DOUBLE), 0);
            assertEquals(TSP.getScore(CSVFileParser.STRING), values.length);

            assertEquals(CSVFileParser.STRING, TSP.getSuggestedType());
        }
        {
            String[] values = new String[] { "0\n", "1\n", "2\n", "3\n", "4\n", };
            StringBuilder sbuf = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                sbuf.append(values[i]);
            }

            String text = sbuf.toString();
            byte[] bytes = text.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            CsvPreference pref = new CsvPreference.Builder('"', ',', "\n")
                    .build();
            CsvListReader sampleReader = new CsvListReader(
                    new InputStreamReader(in), pref);

            TypeSuggestionProcessor TSP = new TypeSuggestionProcessor(
                    values.length, hintScore);
            TSP.addHint("int"); // int
            CellProcessor[] procs = new CellProcessor[] { TSP };

            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.read(procs);
            sampleReader.close();

            assertEquals(TSP.getScore(CSVFileParser.INT), hintScore
                    + values.length);
            assertEquals(TSP.getScore(CSVFileParser.LONG), values.length);
            assertEquals(TSP.getScore(CSVFileParser.DOUBLE), values.length);
            assertEquals(TSP.getScore(CSVFileParser.STRING), values.length);

            assertEquals(CSVFileParser.INT, TSP.getSuggestedType());
        }
        {
            String[] values = new String[] { "0\n", "1\n", "2\n", "3\n", "4\n", };
            StringBuilder sbuf = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                sbuf.append(values[i]);
            }

            String text = sbuf.toString();
            byte[] bytes = text.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            CsvPreference pref = new CsvPreference.Builder('"', ',', "\n")
                    .build();
            CsvListReader sampleReader = new CsvListReader(
                    new InputStreamReader(in), pref);

            TypeSuggestionProcessor TSP = new TypeSuggestionProcessor(
                    values.length, hintScore);
            TSP.addHint("int"); // int
            CellProcessor[] procs = new CellProcessor[] { TSP };

            for (int i = 0; i < values.length; i++) {
                sampleReader.read(procs);
            }
            sampleReader.close();

            assertEquals(TSP.getScore(CSVFileParser.INT), hintScore
                    + values.length);
            assertEquals(TSP.getScore(CSVFileParser.LONG), values.length);
            assertEquals(TSP.getScore(CSVFileParser.DOUBLE), values.length);
            assertEquals(TSP.getScore(CSVFileParser.STRING), values.length);

            assertEquals(CSVFileParser.INT, TSP.getSuggestedType());
        }
    }

    @Ignore
    static class MockFileWriter extends MsgpackGZIPFileWriter {
        private List<Integer> colSizeList;
        private List<Object> objectList;

        public MockFileWriter(PreparePartsRequest request)
                throws CommandException {
            super(request, null);
            colSizeList = new ArrayList<Integer>();
            objectList = new ArrayList<Object>();
        }

        @Override
        public void initWriter(PreparePartsRequest request, String infileName)
                throws CommandException {
            // do nothing
        }

        public void setColSize(int colSize) {
            colSizeList.add(colSize);
        }

        @Override
        public void writeBeginRow(int got) throws CommandException {
            int expected = colSizeList.remove(0);
            assertEquals(expected, got);
        }

        public void setRow(Object[] row) {
            for (Object c : row) {
                objectList.add(c);
            }
        }

        @Override
        public void write(Object got) throws CommandException {
            Object expected = objectList.remove(0);
            assertEquals(expected, got);
        }

        @Override
        public void writeEndRow() throws CommandException {
            // do nothing
        }

        @Override
        public void close() throws CommandException {
            // do nothing
        }

        @Override
        public void closeSilently() {
            // do nothing
        }
    }

    @Test
    public void parseSeveralTypesOfColumns() throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNS, "v0,v1,v2,v3,time");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "string,int,long,double,long");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "c00,0,0,0.0,12345\n" + "c10,1,1,1.1,12345\n"
                + "c20,2,2,2.2,12345\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(5);
        w.setRow(new Object[] { "v0", "c00", "v1", 0, "v2", 0L, "v3", 0.0,
                "time", 12345L });
        w.setColSize(5);
        w.setRow(new Object[] { "v0", "c10", "v1", 1, "v2", 1L, "v3", 1.1,
                "time", 12345L });
        w.setColSize(5);
        w.setRow(new Object[] { "v0", "c20", "v1", 2, "v2", 2L, "v3", 2.2,
                "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseSeveralTypesOfColumnsIncludeNull() throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNS, "v0,v1,v2,v3,time");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "string,int,long,double,long");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "c00,0,0,0.0,12345\n" + ",,,,12345\n"
                + "c20,2,2,2.2,12345\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(5);
        w.setRow(new Object[] { "v0", "c00", "v1", 0, "v2", 0L, "v3", 0.0,
                "time", 12345L });
        w.setColSize(5);
        w.setRow(new Object[] { "v0", null, "v1", null, "v2", null, "v3", null,
                "time", 12345L });
        w.setColSize(5);
        w.setRow(new Object[] { "v0", "c20", "v1", 2, "v2", 2L, "v3", 2.2,
                "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseInvalidTypesAndHandleErrors() throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNS, "v0,v1,time");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES, "int,long,long");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "0,0,12345\n" + "c10,1,12345\n" + "2,c21,12345\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(3);
        w.setRow(new Object[] { "v0", 0, "v1", 0L, "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(1, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseHeaderlessCSVText() throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNS, "v0,v1,time");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "string,string,long");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "c00,c01,12345\n" + "c10,c11,12345\n" + "c20,c21,12345\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c00", "v1", "c01", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c10", "v1", "c11", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c20", "v1", "c21", "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseHeaderlessTSVText() throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "tsv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNS, "v0,v1,time");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "string,string,long");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("tsv",
                new String[0], props);

        String text = "c00\tc01\t12345\n" + "c10\tc11\t12345\r\n" + "c20\tc21\t12345\r\n";
        byte[] bytes = text.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c00", "v1", "c01", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c10", "v1", "c11", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c20", "v1", "c21", "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseNotSpecifiedTimeColumnHeaderlessCSVTextWithAliasColumnName()
            throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNS, "v0,v1,timestamp");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "string,string,long");
        props.setProperty(Config.BI_PREPARE_PARTS_TIMECOLUMN, "timestamp");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "c00,c01,12345\n" + "c10,c11,12345\n" + "c20,c21,12345\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(4);
        w.setRow(new Object[] { "v0", "c00", "v1", "c01", "timestamp", 12345L,
                "time", 12345L });
        w.setColSize(4);
        w.setRow(new Object[] { "v0", "c10", "v1", "c11", "timestamp", 12345L,
                "time", 12345L });
        w.setColSize(4);
        w.setRow(new Object[] { "v0", "c20", "v1", "c21", "timestamp", 12345L,
                "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseNotSpecifiedTimeColumnHeaderlessCSVTextWithTimeValue()
            throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNS, "v0,v1");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES, "string,string");
        props.setProperty(Config.BI_PREPARE_PARTS_TIMEVALUE, "12345");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "c00,c01\n" + "c10,c11\n" + "c20,c21\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c00", "v1", "c01", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c10", "v1", "c11", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c20", "v1", "c21", "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseHeaderedCSVText() throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNHEADER, "true");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "string,string,long");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "v0,v1,time\n" + "c00,c01,12345\n" + "c10,c11,12345\n"
                + "c20,c21,12345\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c00", "v1", "c01", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c10", "v1", "c11", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c20", "v1", "c21", "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseNotSpecifiedTimeColumnHeaderedCSVTextWithAliasColumnName01()
            throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNHEADER, "true");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "string,string,long");
        props.setProperty(Config.BI_PREPARE_PARTS_TIMECOLUMN, "timestamp");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "v0,v1,timestamp\n" + "c00,c01,12345\n"
                + "c10,c11,12345\n" + "c20,c21,12345\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(4);
        w.setRow(new Object[] { "v0", "c00", "v1", "c01", "timestamp", 12345L,
                "time", 12345L });
        w.setColSize(4);
        w.setRow(new Object[] { "v0", "c10", "v1", "c11", "timestamp", 12345L,
                "time", 12345L });
        w.setColSize(4);
        w.setRow(new Object[] { "v0", "c20", "v1", "c21", "timestamp", 12345L,
                "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseNotSpecifiedTimeColumnHeaderedCSVTextWithAliasColumnName02()
            throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNHEADER, "true");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES,
                "long,string,string");
        props.setProperty(Config.BI_PREPARE_PARTS_TIMECOLUMN, "timestamp");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "timestamp,v0,v1\n" + "12345,c00,c01\n"
                + "12345,c10,c11\n" + "12345,c20,c21\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(4);
        w.setRow(new Object[] { "timestamp", 12345L, "v0", "c00", "v1", "c01",
                "time", 12345L });
        w.setColSize(4);
        w.setRow(new Object[] { "timestamp", 12345L, "v0", "c10", "v1", "c11",
                "time", 12345L });
        w.setColSize(4);
        w.setRow(new Object[] { "timestamp", 12345L, "v0", "c20", "v1", "c21",
                "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }

    @Test
    public void parseNotSpecifiedTimeColumnHeaderedCSVTextWithTimeValue()
            throws Exception {
        Properties props = new Properties();
        props.setProperty(Config.BI_PREPARE_PARTS_FORMAT, "csv");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNHEADER, "true");
        props.setProperty(Config.BI_PREPARE_PARTS_COLUMNTYPES, "string,string");
        props.setProperty(Config.BI_PREPARE_PARTS_TIMEVALUE, "12345");
        props.setProperty(Config.BI_PREPARE_PARTS_OUTPUTDIR, "out");
        props.setProperty(Config.BI_PREPARE_PARTS_SPLIT_SIZE, "" + (16 * 1024));
        CSVPreparePartsRequest request = new CSVPreparePartsRequest("csv",
                new String[0], props);

        String text = "v0,v1\n" + "c00,c01\n" + "c10,c11\n" + "c20,c21\n";
        byte[] bytes = text.getBytes();
        CSVFileParser p = new CSVFileParser(request);
        p.doPreExecute(new ByteArrayInputStream(bytes));
        p.doParse(new ByteArrayInputStream(bytes));

        MockFileWriter w = new MockFileWriter(request);
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c00", "v1", "c01", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c10", "v1", "c11", "time", 12345L });
        w.setColSize(3);
        w.setRow(new Object[] { "v0", "c20", "v1", "c21", "time", 12345L });

        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertTrue(p.parseRow(w));
        assertFalse(p.parseRow(w));

        assertEquals(3, p.getRowNum());

        p.close();
        w.close();
    }
}
