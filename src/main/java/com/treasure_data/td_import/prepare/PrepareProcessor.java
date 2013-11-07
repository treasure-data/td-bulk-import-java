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
package com.treasure_data.td_import.prepare;

import java.io.IOException;
import java.util.logging.Logger;

import com.treasure_data.td_import.prepare.Task;
import com.treasure_data.td_import.reader.FileReader;
import com.treasure_data.td_import.writer.FileWriter;

public class PrepareProcessor {

    private static final Logger LOG = Logger.getLogger(
            PrepareProcessor.class.getName());

    protected PrepareConfiguration conf;

    public PrepareProcessor(PrepareConfiguration conf) {
        this.conf = conf;
    }

    public TaskResult execute(final Task task) {
        String msg = String.format("Converting '%s'...", task.source.getRawPath());
        System.out.println(msg);
        LOG.info(msg);

        TaskResult result = new TaskResult();
        result.task = task;

        // create and initialize file writer
        FileWriter w = null;
        try {
            w = conf.getOutputFormat().createFileWriter(conf);
            w.configure(task, result);
        } catch (Exception e) {
            result.error = e;
            return result;
        }

        // create and initialize file reader
        FileReader r = null;
        try {
            r = conf.getFormat().createFileReader(conf, w);
            r.configure(task);
        } catch (Exception e) {
            result.error = e;
            return result;
        }

        if (w != null && r != null) {
            w.setColumnNames(r.getColumnNames());
            w.setColumnTypes(r.getColumnTypes());
            w.setSkipColumns(r.getSkipColumns());
            w.setTimeColumnValue(r.getTimeColumnValue());

            try {
                while (r.next()) {
                    ;
                }

                result.readLines = r.getLineNum();
                result.convertedRows = w.getRowNum();
                result.invalidRows = w.getErrorRowNum();
            } catch (Exception e) {
                e.printStackTrace();
                result.error = e;
            }
        }

        if (r != null) {
            try {
                r.close();
            } catch (IOException e) {
                result.error = e;
                return result;
            }
        }

        if (w != null) {
            try {
                w.close();
            } catch (IOException e) {
                result.error = e;
                return result;
            }
        }

        LOG.info(String.format("Converted %s, result: %s", task.source.getRawPath(), result));

        return result;
    }

}
