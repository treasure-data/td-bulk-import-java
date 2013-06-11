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
package com.treasure_data.bulk_import.prepare_parts;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.Tokenizer;
import org.supercsv.prefs.CsvPreference;

import com.treasure_data.bulk_import.Config;
import com.treasure_data.bulk_import.prepare_parts.PrepareConfig;
import com.treasure_data.bulk_import.prepare_parts.PrepareConfig.ColumnType;
import com.treasure_data.bulk_import.prepare_parts.proc.ColumnSamplingProc;
import com.treasure_data.bulk_import.prepare_parts.proc.ColumnProc;
import com.treasure_data.bulk_import.prepare_parts.proc.ColumnProcGenerator;

public class CSVFileParser extends FileParser {
    private static final Logger LOG = Logger.getLogger(CSVFileParser.class.getName());

    private Tokenizer reader;
    private CsvPreference csvPref;
    private CellProcessor[] cprocs;
    private ColumnProc tcproc = null;

    private boolean needToAppendTimeColumn = false;
    private int timeColumnIndex = -1;
    private int aliasTimeColumnIndex = -1;
    private ExtStrftime timeFormat = null;
    private Long timeValue = 0L;
    private String[] columnNames;
    private ColumnType[] columnTypes;

    public CSVFileParser(PrepareConfig conf) throws PreparePartsException {
        super(conf);
    }

    @Override
    public void configure(String fileName) throws PreparePartsException {
        super.configure(fileName);

        // CSV preference
        csvPref = new CsvPreference.Builder(conf.getQuoteChar(),
                conf.getDelimiterChar(), conf.getNewline().newline()).build();
    }

    @Override
    public void sample(InputStream in) throws PreparePartsException {
        // create sample reader
        CsvListReader sampleReader = new CsvListReader(new InputStreamReader(
                in, decoder), csvPref);

        try {
            // extract column names
            // e.g. 
            // 1) [ "time", "name", "price" ]
            // 2) [ "timestamp", "name", "price" ]
            // 3) [ "name", "price" ]
            if (conf.hasColumnHeader()) {
                List<String> columnList = sampleReader.read();
                columnNames = columnList.toArray(new String[0]);
            } else {
                columnNames = conf.getColumnNames();
            }

            // get index of 'time' column
            // [ "time", "name", "price" ] as all columns is given,
            // the index is zero.
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i].equals(
                        Config.BI_PREPARE_PARTS_TIMECOLUMN_DEFAULTVALUE)) {
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
                        needToAppendTimeColumn = true;
                        break;
                    }
                }
            }

            if ((timeColumnIndex >= 0 || aliasTimeColumnIndex >= 0)
                    && conf.getTimeFormat() != null) {
                String timeFormat = conf.getTimeFormat();
                this.timeFormat = new ExtStrftime(timeFormat);
            }

            // if 'time' and the alias column don't exist,
            if (timeColumnIndex < 0 && aliasTimeColumnIndex < 0) {
                timeValue = conf.getTimeValue();
                needToAppendTimeColumn = true;
            }

            // read sample
            List<Object> firstRow = null;
            final int sampleRowSize = conf.getSampleRowSize();
            CellProcessor[] cprocs = ColumnProcGenerator.generateSampleCellProcessors(
                    columnNames, sampleRowSize);
            boolean isFirstRow = false;
            for (int i = 0; i < sampleRowSize; i++) {
                List<Object> row = sampleReader.read(cprocs);
                if (!isFirstRow) {
                    firstRow = row;
                    isFirstRow = true;
                }

                if (row == null || row.isEmpty()) {
                    break;
                }
            }

            // initialize types of all columns
            columnTypes = new PrepareConfig.ColumnType[columnNames.length];
            for (int i = 0; i < columnTypes.length; i++) {
                if (i == timeColumnIndex) {
                    columnTypes[i] = PrepareConfig.ColumnType.TIME;
                } else {
                    columnTypes[i] = ((ColumnSamplingProc) cprocs[i]).getColumnType();
                }
            }
        } catch (IOException e) {
            throw new PreparePartsException(e);
        } finally {

        }
    }

    @Override
    public void parse(InputStream in) throws PreparePartsException {
        // create reader
        reader = new Tokenizer(new InputStreamReader(in, decoder), csvPref);
        if (conf.hasColumnHeader()) {
            // header line is skipped
            try {
                reader.readColumns(new ArrayList<String>());
                incrementLineNum();
            } catch (IOException e) {
                throw new PreparePartsException(e);
            }
        }

        // create cell processors
        this.cprocs = ColumnProcGenerator.generateCellProcessors(
                writer, columnNames, columnTypes, timeColumnIndex, timeFormat);
        if (needToAppendTimeColumn) {
            tcproc = ColumnProcGenerator.generateTimeColumnProcessor(
                    writer, aliasTimeColumnIndex, timeFormat, timeValue);
        }

        List<String> row = new ArrayList<String>();
        boolean moreRead = true;
        while (moreRead) {
            incrementLineNum();
            try {
                moreRead = parseRow(row);
            } catch (IOException e) {
                // if reader throw I/O error, parseRow throws PreparePartsException.
                LOG.throwing("CSVFileParser", "parseRow", e);
                throw new PreparePartsException(e);
            } catch (PreparePartsException e) {
                LOG.warning(e.getMessage());
                // TODO the row data should be written to error rows file
            }
        }
    }

    private boolean parseRow(List<String> row) throws IOException, PreparePartsException {
        // if reader got EOF, it returns false.
        if (!reader.readColumns(row)) {
            return false;
        }

        // increment row number
        incrementRowNum();

        int rowSize = row.size();
        if (rowSize != cprocs.length) {
            throw new PreparePartsException(String.format(
                    "The number of columns to be processed (%d) must match the number of " +
                    "CellProcessors (%d): check that the number of CellProcessors you have " +
                    "defined matches the expected number of columns being read/written " +
                    "[line: %d]", rowSize, cprocs.length, getLineNum()));
        }

        // write begin of row (map data)
        if (needToAppendTimeColumn) {
            // if the row doesn't have 'time' column, new 'time' column needs
            // to be appended to it.
            writer.writeBeginRow(rowSize + 1);
        } else {
            writer.writeBeginRow(rowSize);
        }

        for (int i = 0; i < rowSize; i++) {
            try {
                cprocs[i].execute(row.get(i), null);
            } catch (Throwable t) {
                throw new PreparePartsException(String.format(
                        "It cannot translate #%d column '%s'. Please check row data: %s [line: %d]",
                        i, ((ColumnProc) cprocs[i]).getColumnName(),
                        reader.getUntokenizedRow(), getLineNum()));
            }
        }

        if (needToAppendTimeColumn) {
            tcproc.execute(row.get(tcproc.getIndex()));
        }

        // write end of row (map data)
        writer.writeEndRow();
        writer.incrementRowNum();

        return true;
    }

    public void close() throws PreparePartsException {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new PreparePartsException(e);
            }
        }
    }
}