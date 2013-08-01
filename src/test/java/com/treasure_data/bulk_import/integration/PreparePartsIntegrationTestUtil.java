package com.treasure_data.bulk_import.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.msgpack.MessagePack;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.unpacker.UnpackerIterator;

import com.treasure_data.bulk_import.Configuration;
import com.treasure_data.bulk_import.BulkImportMain;

@Ignore
public class PreparePartsIntegrationTestUtil {
    private static Value STRING_VALUE = ValueFactory.createRawValue("string-value");
    private static Value INT_VALUE = ValueFactory.createRawValue("int-value");
    private static Value DOUBLE_VALUE = ValueFactory.createRawValue("double-value");
    private static Value TIME = ValueFactory.createRawValue("time");

    static final String INPUT_DIR = "./src/test/resources/in/";
    static final String OUTPUT_DIR = "./src/test/resources/out/";

    protected Properties props;
    protected List<String> args;

    @Before
    public void createResources() throws Exception {
        props = new Properties();
        args = new ArrayList<String>();
    }

    @After
    public void destroyResources() throws Exception {
    }

    public void setProperties(String format, String columnHeader,
            String aliasTimeColumn, String timeFormat, String columnNames, String exclude, String only) {
        // format
        if (format != null && !format.isEmpty()) {
            props.setProperty(Configuration.BI_PREPARE_PARTS_FORMAT, format);
        }

        // output dir
        props.setProperty(Configuration.BI_PREPARE_PARTS_OUTPUTDIR, OUTPUT_DIR);

        // column header
        if (columnHeader != null && !columnHeader.isEmpty()) {
            props.setProperty(Configuration.BI_PREPARE_PARTS_COLUMNHEADER, columnHeader);
        }

        // alias time column
        if (aliasTimeColumn != null && !aliasTimeColumn.isEmpty()) {
            props.setProperty(Configuration.BI_PREPARE_PARTS_TIMECOLUMN, aliasTimeColumn);
        }

        // time format
        if (timeFormat != null && !timeFormat.isEmpty()) {
            props.setProperty(Configuration.BI_PREPARE_PARTS_TIMEFORMAT, timeFormat);
        }

        // column names
        if (columnNames != null && !columnNames.isEmpty()) {
            props.setProperty(Configuration.BI_PREPARE_PARTS_COLUMNS, columnNames);
        }

        // exclude columns
        if (exclude != null && !exclude.isEmpty()) {
            props.setProperty(Configuration.BI_PREPARE_PARTS_EXCLUDE_COLUMNS, exclude);
        }

        // only columns
        if (only != null && !only.isEmpty()) {
            props.setProperty(Configuration.BI_PREPARE_PARTS_ONLY_COLUMNS, only);
        }
    }

    public void prepareParts(String fileName) throws Exception {
        args.add(Configuration.CMD_PREPARE_PARTS);
        args.add(fileName);

        BulkImportMain.prepareParts(args.toArray(new String[0]), props);
    }

    public void preparePartsFromCSVWithTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "csvfile-with-time.csv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "csvfile-with-time_csv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromCSVWithAlasTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "csvfile-with-aliastime.csv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "csvfile-with-aliastime_csv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromCSVWithTimeFormat() throws Exception {
        prepareParts(INPUT_DIR + "csvfile-with-timeformat.csv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "csvfile-with-timeformat_csv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromHeaderlessCSVWithTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "headerless-csvfile-with-time.csv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "headerless-csvfile-with-time_csv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromHeaderlessCSVWithAlasTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "headerless-csvfile-with-aliastime.csv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "headerless-csvfile-with-aliastime_csv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromHeaderlessCSVWithTimeFormat() throws Exception {
        prepareParts(INPUT_DIR + "headerless-csvfile-with-timeformat.csv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "headerless-csvfile-with-timeformat_csv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromTSVWithTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "tsvfile-with-time.tsv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "tsvfile-with-time_tsv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromTSVWithAlasTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "tsvfile-with-aliastime.tsv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "tsvfile-with-aliastime_tsv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromTSVWithTimeFormat() throws Exception {
        prepareParts(INPUT_DIR + "tsvfile-with-timeformat.tsv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "tsvfile-with-timeformat_tsv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromHeaderlessTSVWithTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "headerless-tsvfile-with-time.tsv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "headerless-tsvfile-with-time_tsv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromHeaderlessTSVWithAlasTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "headerless-tsvfile-with-aliastime.tsv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "headerless-tsvfile-with-aliastime_tsv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromHeaderlessTSVWithTimeFormat() throws Exception {   
        prepareParts(INPUT_DIR + "headerless-tsvfile-with-timeformat.tsv");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "headerless-tsvfile-with-timeformat_tsv_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromSyslog() throws Exception {
        prepareParts(INPUT_DIR + "syslogfile.syslog");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "syslogfile_syslog_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName, "syslog");
    }

    public void preparePartsFromApacheLog() throws Exception {
        prepareParts(INPUT_DIR + "apachelogfile.apache");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "apachelogfile_apache_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName, "apache");
    }

    // TODO
    // TODO

    public void preparePartsFromJSONWithTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "jsonfile-with-time.json");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "jsonfile-with-time_json_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromJSONWithAlasTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "jsonfile-with-aliastime.json");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "jsonfile-with-aliastime_json_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromJSONWithTimeFormat() throws Exception {
        prepareParts(INPUT_DIR + "jsonfile-with-timeformat.json");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "jsonfile-with-timeformat_json_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromMessagePackWithTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "msgpackfile-with-time.msgpack");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "msgpackfile-with-time_msgpack_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromMessagePackWithAlasTimeColumn() throws Exception {
        prepareParts(INPUT_DIR + "msgpackfile-with-aliastime.msgpack");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "msgpackfile-with-aliastime_msgpack_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void preparePartsFromMessagePackWithTimeFormat() throws Exception {
        prepareParts(INPUT_DIR + "msgpackfile-with-timeformat.msgpack");

        String srcFileName = INPUT_DIR + "trainingfile-with-time.msgpack.gz";
        String dstFileName = OUTPUT_DIR + "msgpackfile-with-timeformat_msgpack_0.msgpack.gz";
        assertDataEquals(srcFileName, dstFileName);
    }

    public void assertDataEquals(String srcFileName, String dstFileName) throws Exception {
        assertDataEquals(srcFileName, dstFileName, "none");
    }

    public void assertDataEquals(String srcFileName, String dstFileName, String format) throws Exception {
        MessagePack msgpack = new MessagePack();

        InputStream srcIn = new BufferedInputStream(new GZIPInputStream(new FileInputStream(srcFileName)));
        InputStream dstIn = new BufferedInputStream(new GZIPInputStream(new FileInputStream(dstFileName)));

        UnpackerIterator srcIter = msgpack.createUnpacker(srcIn).iterator();
        UnpackerIterator dstIter = msgpack.createUnpacker(dstIn).iterator();

        while (srcIter.hasNext() && dstIter.hasNext()) {
            MapValue srcMap = srcIter.next().asMapValue();
            MapValue dstMap = dstIter.next().asMapValue();

            assertMapValueEquals(srcMap, dstMap, format);
        }

        assertFalse(srcIter.hasNext());
        assertFalse(dstIter.hasNext());
    }

    private void assertMapValueEquals(MapValue src, MapValue dst, String format) {
        if (format.equals("none")) {
            assertTrue(src.containsKey(STRING_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(STRING_VALUE));

            assertTrue(src.containsKey(INT_VALUE));
            assertEquals(src.get(INT_VALUE), dst.get(INT_VALUE));

            assertTrue(src.containsKey(DOUBLE_VALUE));
            assertEquals(src.get(DOUBLE_VALUE), dst.get(DOUBLE_VALUE));

            assertTrue(src.containsKey(TIME));
            assertEquals(src.get(TIME), dst.get(TIME));
        } else if (format.equals("syslog")) {
            assertTrue(src.containsKey(STRING_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(SyslogFileGenerator.HOST_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(SyslogFileGenerator.IDENT_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(SyslogFileGenerator.MESSAGE_VALUE));

            assertTrue(src.containsKey(INT_VALUE));
            assertEquals(src.get(INT_VALUE), dst.get(SyslogFileGenerator.PID_VALUE));

            assertTrue(src.containsKey(TIME));
            assertEquals(src.get(TIME), dst.get(TIME));
        } else if (format.equals("apache")) {
            try {
            assertTrue(src.containsKey(STRING_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(ApacheFileGenerator.HOST_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(ApacheFileGenerator.USER_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(ApacheFileGenerator.METHOD_VALUE));
            assertEquals(src.get(STRING_VALUE), dst.get(ApacheFileGenerator.PATH_VALUE));
            assertEquals(src.get(INT_VALUE), dst.get(ApacheFileGenerator.CODE_VALUE));
            assertEquals(src.get(INT_VALUE), dst.get(ApacheFileGenerator.SIZE_VALUE));

            assertTrue(src.containsKey(TIME));
            assertEquals(src.get(TIME), dst.get(TIME));
            } catch (Throwable t) {
                System.out.println("src: " + src);
                System.out.println("dst: " + dst);
                throw new RuntimeException(t);
            }
        } else {
            throw new RuntimeException();
        }
    }
}
