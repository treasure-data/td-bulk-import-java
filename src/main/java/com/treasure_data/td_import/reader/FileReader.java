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

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.treasure_data.td_import.Configuration;
import com.treasure_data.td_import.model.AliasTimeColumnValue;
import com.treasure_data.td_import.model.ColumnType;
import com.treasure_data.td_import.model.ColumnValue;
import com.treasure_data.td_import.model.Row;
import com.treasure_data.td_import.model.TimeColumnSampling;
import com.treasure_data.td_import.model.TimeColumnValue;
import com.treasure_data.td_import.model.TimeValueTimeColumnValue;
import com.treasure_data.td_import.prepare.HHmmssStrftime;
import com.treasure_data.td_import.prepare.PrepareConfiguration;
import com.treasure_data.td_import.prepare.PreparePartsException;
import com.treasure_data.td_import.prepare.Task;
import com.treasure_data.td_import.writer.FileWriter;

public abstract class FileReader<T extends PrepareConfiguration> implements Closeable {
    private static final Logger LOG = Logger.getLogger(FileReader.class.getName());

    protected T conf;
    protected FileWriter writer;
    protected Row convertedRow;

    protected String name;
    protected String[] columnNames;
    protected ColumnType[] columnTypes;
    protected Set<String> skipColumns = new HashSet<String>();
    protected TimeColumnValue timeColumnValue;

    protected long lineNum = 0;

    protected FileReader(T conf, FileWriter writer) {
        this.conf = conf;
        this.writer = writer;
    }

    public void configure(Task task) throws PreparePartsException {
        name = task.fileName;
        columnNames = conf.getColumnNames();
        columnTypes = conf.getColumnTypes();

        // check compression type of the file
        conf.checkCompressionType(name);
    }

    public void resetLineNum() {
        lineNum = 0;
    }

    public void incrementLineNum() {
        lineNum++;
    }

    public long getLineNum() {
        return lineNum;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public ColumnType[] getColumnTypes() {
        return columnTypes;
    }

    public Set<String> getSkipColumns() {
        return skipColumns;
    }

    public void setSkipColumns() {
        String[] excludeColumns = conf.getExcludeColumns();
        String[] onlyColumns = conf.getOnlyColumns();
        for (int i = 0; i < columnNames.length; i++) {
            // check exclude columns
            boolean isExcluded = false;
            for (String excludeColumn : excludeColumns) {
                if (columnNames[i].equals(excludeColumn)) {
                    isExcluded = true;
                    break;
                }
            }

            if (isExcluded) {
                skipColumns.add(columnNames[i]);
                continue;
            }

            // check only columns
            if (onlyColumns.length == 0) {
                continue;
            }

            boolean isOnly = false;
            for (String onlyColumn : onlyColumns) {
                if (columnNames[i].equals(onlyColumn)) {
                    isOnly = true;
                    break;
                }
            }

            if (!isOnly) {
                skipColumns.add(columnNames[i]);
                continue; // not needed though,..
            }
        }
    }

    public int getTimeColumnIndex() {
        int index = -1;
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(
                    Configuration.BI_PREPARE_PARTS_TIMECOLUMN_DEFAULTVALUE)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getAliasTimeColumnIndex(int timeColumnIndex) {
        int index = -1;
        if (timeColumnIndex < 0 && conf.getAliasTimeColumn() != null) {
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i].equals(conf.getAliasTimeColumn())) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public void initializeColumnTypes(TimeColumnSampling[] sampleColumnValues) {
        if (columnTypes == null || columnTypes.length == 0) {
            columnTypes = new ColumnType[columnNames.length];
            for (int i = 0; i < columnTypes.length; i++) {
                columnTypes[i] = sampleColumnValues[i].getColumnTypeRank();
            }
            conf.setColumnTypes(columnTypes);
        }
    }

    public TimeColumnValue getTimeColumnValue() {
        return timeColumnValue;
    }

    public void setTimeColumnValue(TimeColumnSampling[] sampleColumnValues,
            int timeColumnIndex, int aliasTimeColumnIndex) {
        if (timeColumnIndex >= 0) {
            if (conf.getTimeFormat() != null) {
                timeColumnValue = new TimeColumnValue(timeColumnIndex, conf.getTimeFormat());
            } else {
                String suggested = sampleColumnValues[timeColumnIndex].getSTRFTimeFormatRank();
                if (suggested != null) {
                    if (suggested.equals(TimeColumnSampling.HHmmss_STRF)) {
                        timeColumnValue = new TimeColumnValue(timeColumnIndex, new HHmmssStrftime());
                    } else {
                        timeColumnValue = new TimeColumnValue(timeColumnIndex, conf.getTimeFormat(suggested));
                    }
                } else {
                    timeColumnValue = new TimeColumnValue(timeColumnIndex, null);
                }
            }
        } else if (aliasTimeColumnIndex >= 0) {
            if (conf.getTimeFormat() != null) {
                timeColumnValue = new AliasTimeColumnValue(aliasTimeColumnIndex, conf.getTimeFormat());
            } else {
                String suggested = sampleColumnValues[aliasTimeColumnIndex].getSTRFTimeFormatRank();
                if (suggested != null) {
                    if (suggested.equals(TimeColumnSampling.HHmmss_STRF)) {
                        timeColumnValue = new AliasTimeColumnValue(aliasTimeColumnIndex, new HHmmssStrftime());
                    } else {
                        timeColumnValue = new AliasTimeColumnValue(aliasTimeColumnIndex, conf.getTimeFormat(suggested));
                    }
                } else {
                    timeColumnValue = new AliasTimeColumnValue(aliasTimeColumnIndex, null);
                }
            }
        } else {
            timeColumnValue = new TimeValueTimeColumnValue(conf.getTimeValue());
        }
    }

    public void initializeConvertedRow() {
        ColumnValue[] values = new ColumnValue[columnTypes.length];
        for (int i = 0; i < columnTypes.length; i++) {
            values[i] = columnTypes[i].createColumnValue();
        }
        convertedRow = new Row(values);
    }

    public boolean next() throws PreparePartsException {
        try {
            // if reader got EOF, it returns false.
            if (!readRow()) {
                return false;
            }

            // convert each column in row
            convertTypesOfColumns();

            // write each column value
            writer.next(convertedRow);

            writer.incrementRowNum();
        } catch (IOException e) {
            // if reader throw I/O error, parseRow throws PreparePartsException.
            LOG.throwing("CSVFileParser", "parseRow", e);
            throw new PreparePartsException(e);
        } catch (PreparePartsException e) {
            writer.incrementErrorRowNum();

            // the row data should be written to error rows file
            String msg = String.format("line %d in %s: %s", lineNum, name, getCurrentRow());
            LOG.warning(e.getMessage());
            LOG.warning(msg);
            handleError(e);
        }
        return true;
    }

    public abstract boolean readRow() throws IOException, PreparePartsException;

    public abstract void convertTypesOfColumns() throws PreparePartsException;

    public abstract String getCurrentRow();

    public void handleError(PreparePartsException e) throws PreparePartsException {
        conf.getErrorRecordsHandling().handleError(e);
    }

    // Closeable#close()
    public void close() throws IOException {
    }
}
