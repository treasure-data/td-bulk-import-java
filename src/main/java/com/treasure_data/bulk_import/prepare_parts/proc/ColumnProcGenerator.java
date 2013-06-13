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
package com.treasure_data.bulk_import.prepare_parts.proc;

import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.treasure_data.bulk_import.prepare_parts.PrepareConfig;
import com.treasure_data.bulk_import.prepare_parts.PreparePartsException;
import com.treasure_data.bulk_import.prepare_parts.ExtStrftime;

public class ColumnProcGenerator {

    public static CellProcessor[] generateSampleCellProcessors(String[] columnNames,
            int sampleRow) throws PreparePartsException {
        int len = columnNames.length;
        List<CellProcessor> cprocs = new ArrayList<CellProcessor>(len);
        for (int i = 0; i < len; i++) {
            cprocs.add(new ColumnSamplingProc(i, columnNames[i], sampleRow));
        }
        return cprocs.toArray(new CellProcessor[0]);
    }

    public static CellProcessor[] generateCellProcessors(
            com.treasure_data.bulk_import.prepare_parts.FileWriter writer,
            String[] columnNames, PrepareConfig.ColumnType[] columnTypes,
            int timeColumnIndex, ExtStrftime timeFormat) throws PreparePartsException {
        int len = columnTypes.length;
        List<CellProcessor> cprocs = new ArrayList<CellProcessor>(len);
        for (int i = 0; i < len; i++) {
            cprocs.add(generateCellProcessor(i, columnNames[i], columnTypes[i],
                    timeColumnIndex, timeFormat, writer));
        }
        return cprocs.toArray(new CellProcessor[0]);
    }

    public static ColumnProc[] generateColumnProcessors(String[] columnNames,
            PrepareConfig.ColumnType[] columnTypes, int timeColumnIndex, ExtStrftime timeFormat,
            com.treasure_data.bulk_import.prepare_parts.FileWriter writer)
                    throws PreparePartsException {
        int len = columnTypes.length;
        List<ColumnProc> cprocs = new ArrayList<ColumnProc>(len);
        for (int i = 0; i < len; i++) {
            cprocs.add(generateColumnProcessor(i, columnNames[i], columnTypes[i],
                    timeColumnIndex, timeFormat, writer));
        }
        return cprocs.toArray(new ColumnProc[0]);
    }

    public static CellProcessor generateCellProcessor(int index, String columnName,
            PrepareConfig.ColumnType columnType, int timeColumnIndex, ExtStrftime timeFormat,
            com.treasure_data.bulk_import.prepare_parts.FileWriter writer)
                    throws PreparePartsException {
        return (CellProcessor) generateColumnProcessor(index, columnName, columnType,
                timeColumnIndex, timeFormat, writer);
    }

    public static ColumnProc generateColumnProcessor(int index, String columnName,
            PrepareConfig.ColumnType columnType, int timeColumnIndex, ExtStrftime timeFormat,
            com.treasure_data.bulk_import.prepare_parts.FileWriter writer)
                    throws PreparePartsException {
        if (columnType == PrepareConfig.ColumnType.INT) {
            return new IntColumnProc(index, columnName, writer);
        } else if (columnType == PrepareConfig.ColumnType.LONG) {
            return new LongColumnProc(index, columnName, writer);
        } else if (columnType == PrepareConfig.ColumnType.DOUBLE) {
            return new DoubleColumnProc(index, columnName, writer);
        } else if (columnType == PrepareConfig.ColumnType.STRING) {
            return new StringColumnProc(index, columnName, writer);
        } else if (columnType == PrepareConfig.ColumnType.TIME) {
            return new TimeColumnProc(index, timeFormat, writer);
        } else { // otherwise
            throw new UnsupportedOperationException();
        }
    }

    public static ColumnProc generateTimeColumnProcessor(
            com.treasure_data.bulk_import.prepare_parts.FileWriter writer,
            int aliasTimeColumnIndex, ExtStrftime timeFormat, long timeValue) { // TODO should change timeformat
        if (aliasTimeColumnIndex < 0) {
            return new TimeValueColumnProc(timeValue, writer);
        } else {
            return new AliasTimeColumnProc(aliasTimeColumnIndex, timeFormat, writer);
        }
    }
}
