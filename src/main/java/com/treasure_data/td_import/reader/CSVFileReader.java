//
// Treasure Data Bulk-Import Tool in Java
//
// Copyright (C) 2012 - 2013 Muga Nishizawa
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package com.treasure_data.td_import.reader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.supercsv.io.Tokenizer;
import org.supercsv.prefs.CsvPreference;

import com.treasure_data.td_import.Configuration;
import com.treasure_data.td_import.model.AliasTimeColumnValue;
import com.treasure_data.td_import.model.ColumnSampling;
import com.treasure_data.td_import.model.ColumnType;
import com.treasure_data.td_import.model.TimeColumnSampling;
import com.treasure_data.td_import.model.TimeColumnValue;
import com.treasure_data.td_import.model.TimeValueTimeColumnValue;
import com.treasure_data.td_import.prepare.CSVPrepareConfiguration;
import com.treasure_data.td_import.prepare.PreparePartsException;
import com.treasure_data.td_import.prepare.Task;
import com.treasure_data.td_import.writer.FileWriter;
import com.treasure_data.td_import.writer.JSONFileWriter;

public class CSVFileReader extends FixnumColumnsFileReader<CSVPrepareConfiguration> {
    private static final Logger LOG = Logger.getLogger(CSVFileReader.class.getName());

    protected CsvPreference csvPref;
    private Tokenizer tokenizer;
    protected List<String> row = new ArrayList<String>();

    public CSVFileReader(CSVPrepareConfiguration conf, FileWriter writer)
            throws PreparePartsException {
        super(conf, writer);
    }

    @Override
    public void configure(Task task) throws PreparePartsException {
        super.configure(task);

        // initialize csv preference
        csvPref = new CsvPreference.Builder(conf.getQuoteChar().quote(),
                conf.getDelimiterChar(), conf.getNewline().newline()).build();

        // if conf object doesn't have column names, types, etc,
        // sample method checks those values.
        sample(task);

        try {
            tokenizer = new Tokenizer(new InputStreamReader(
                    task.createInputStream(conf.getCompressionType()),
                    conf.getCharsetDecoder()), csvPref);
            if (conf.hasColumnHeader()) {
                // header line is skipped
                incrementLineNum();
                tokenizer.readColumns(new ArrayList<String>());
            }
        } catch (IOException e) {
            throw new PreparePartsException(e);
        }
    }

    public void sample(Task task) throws PreparePartsException {
        Tokenizer sampleTokenizer = null;

        int timeColumnIndex = -1;
        int aliasTimeColumnIndex = -1;
        List<String> row = new ArrayList<String>();

        try {
            // create sample reader
            sampleTokenizer = new Tokenizer(new InputStreamReader(
                    task.createInputStream(conf.getCompressionType()),
                    conf.getCharsetDecoder()), csvPref);

            // extract column names
            // e.g. 
            // 1) [ "time", "name", "price" ]
            // 2) [ "timestamp", "name", "price" ]
            // 3) [ "name", "price" ]
            if (conf.hasColumnHeader()) {
                sampleTokenizer.readColumns(row);
                if (columnNames == null || columnNames.length == 0) {
                    columnNames = row.toArray(new String[0]);
                    conf.setColumnNames(columnNames);
                }
            }

            // get index of 'time' column
            // [ "time", "name", "price" ] as all columns is given,
            // the index is zero.
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i].equals(
                        Configuration.BI_PREPARE_PARTS_TIMECOLUMN_DEFAULTVALUE)) {
                    timeColumnIndex = i;
                    break;
                }
            }

            // get index of specified alias time column
            // [ "timestamp", "name", "price" ] as all columns and
            // "timestamp" as alias time column are given, the index is zero.
            //
            // if 'time' column exists in row data, the specified alias
            // time column is ignore.
            if (timeColumnIndex < 0 && conf.getAliasTimeColumn() != null) {
                for (int i = 0; i < columnNames.length; i++) {
                    if (columnNames[i].equals(conf.getAliasTimeColumn())) {
                        aliasTimeColumnIndex = i;
                        break;
                    }
                }
            }

            // if 'time' and the alias columns don't exist, ...
            if (timeColumnIndex < 0 && aliasTimeColumnIndex < 0) {
                if (conf.getTimeValue() >= 0) {
                } else {
                    throw new PreparePartsException(
                            "Time column not found. --time-column or --time-value option is required");
                }
            }

            boolean isFirstRow = true;
            List<String> firstRow = new ArrayList<String>();
            final int sampleRowSize = conf.getSampleRowSize();
            TimeColumnSampling[] sampleColumnValues = new TimeColumnSampling[columnNames.length];
            for (int i = 0; i < sampleColumnValues.length; i++) {
                sampleColumnValues[i] = new TimeColumnSampling(sampleRowSize);
            }

            // read some rows
            for (int i = 0; i < sampleRowSize; i++) {
                if (!isFirstRow && (columnTypes == null || columnTypes.length == 0)) {
                    break;
                }

                sampleTokenizer.readColumns(row);

                if (row == null || row.isEmpty()) {
                    break;
                }

                if (isFirstRow) {
                    firstRow.addAll(row);
                    isFirstRow = false;
                }

                if (sampleColumnValues.length != row.size()) {
                    throw new PreparePartsException(String.format(
                            "The number of columns to be processed (%d) must " +
                            "match the number of column types (%d): check that the " +
                            "number of column types you have defined matches the " +
                            "expected number of columns being read/written [line: %d] %s",
                            row.size(), columnTypes.length, i, row));
                }

                // sampling
                for (int j = 0; j < sampleColumnValues.length; j++) {
                    sampleColumnValues[j].parse(row.get(j));
                }
            }

            // initialize types of all columns
            if (columnTypes == null || columnTypes.length == 0) {
                columnTypes = new ColumnType[columnNames.length];
                for (int i = 0; i < columnTypes.length; i++) {
                    columnTypes[i] = sampleColumnValues[i].getColumnTypeRank();
                }
                conf.setColumnTypes(columnTypes);
            }

            // initialize time column value
            if (timeColumnIndex >= 0) {
                if (conf.getTimeFormat() != null) {
                    timeColumnValue = new TimeColumnValue(timeColumnIndex,
                            conf.getTimeFormat());
                } else {
                    timeColumnValue = new TimeColumnValue(timeColumnIndex,
                            conf.getTimeFormat(sampleColumnValues[timeColumnIndex]
                                    .getSTRFTimeFormatRank()));
                }
            } else if (aliasTimeColumnIndex >= 0) {
                if (conf.getTimeFormat() != null) {
                    timeColumnValue = new AliasTimeColumnValue(
                            aliasTimeColumnIndex, conf.getTimeFormat());
                } else {
                    timeColumnValue = new AliasTimeColumnValue(aliasTimeColumnIndex,
                            conf.getTimeFormat(sampleColumnValues[aliasTimeColumnIndex]
                                    .getSTRFTimeFormatRank()));
                }
            } else {
                timeColumnValue = new TimeValueTimeColumnValue(
                        conf.getTimeValue());
            }

            initializeConvertedRow();

            // check properties of exclude/only columns
            setSkipColumns();

            // print first sample row
            JSONFileWriter w = null;
            try {
                w = new JSONFileWriter(conf);
                w.setColumnNames(getColumnNames());
                w.setColumnTypes(getColumnTypes());
                w.setSkipColumns(getSkipColumns());
                w.setTimeColumnValue(getTimeColumnValue());

                this.row.addAll(firstRow);

                // convert each column in row
                convertTypesOfColumns();
                // write each column value
                w.next(convertedRow);
                String ret = w.toJSONString();
                String msg = null;
                if (ret != null) {
                    msg = "sample row: " + ret;
                } else  {
                    msg = "cannot get sample row";
                }
                System.out.println(msg);
                LOG.info(msg);
            } finally {
                if (w != null) {
                    w.close();
                }
            }
        } catch (IOException e) {
            throw new PreparePartsException(e);
        } finally {
            if (sampleTokenizer != null) {
                try {
                    sampleTokenizer.close();
                } catch (IOException e) {
                    throw new PreparePartsException(e);
                }
            }
        }
    }

    @Override
    public boolean readRow() throws IOException, PreparePartsException {
        row.clear();
        if (!tokenizer.readColumns(row)) {
            return false;
        }

        incrementLineNum();

        int rawRowSize = row.size();
        if (rawRowSize != columnTypes.length) {
            writer.incrementErrorRowNum();
            throw new PreparePartsException(String.format(
                    "The number of columns to be processed (%d) must " +
                    "match the number of column types (%d): check that the " +
                    "number of column types you have defined matches the " +
                    "expected number of columns being read/written [line: %d]",
                    rawRowSize, columnTypes.length, getLineNum()));
        }

        return true;
    }

    @Override
    public void convertTypesOfColumns() throws PreparePartsException {
        for (int i = 0; i < this.row.size(); i++) {
            columnTypes[i].convertType(this.row.get(i), convertedRow.getValue(i));
        }
    }

    @Override
    public String getCurrentRow() {
        return row.toString();
    }

    @Override
    public void close() throws IOException {
        super.close();

        if (tokenizer != null) {
            tokenizer.close();
        }
    }

}