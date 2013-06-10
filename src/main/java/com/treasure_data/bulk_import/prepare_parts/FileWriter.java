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

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class FileWriter implements Closeable {
    private static final Logger LOG = Logger
            .getLogger(FileWriter.class.getName());

    protected PrepareConfig conf;
    protected long rowNum = 0;

    protected FileWriter(PrepareConfig conf) {
        this.conf = conf;
    }

    protected abstract void configure(String infileName)
            throws PreparePartsException;

    protected abstract void writeBeginRow(int size) throws PreparePartsException;

    protected abstract void write(Object v) throws PreparePartsException;
    public abstract void writeString(String v) throws PreparePartsException;
    public abstract void writeInt(int v) throws PreparePartsException;
    public abstract void writeLong(long v) throws PreparePartsException;
    public abstract void writeDouble(double v) throws PreparePartsException;
    public abstract void writeNil() throws PreparePartsException;

    protected abstract void writeEndRow() throws PreparePartsException;

    public void incrementRowNum() {
        rowNum++;
    }

    public long getRowNum() {
        return rowNum;
    }

    public abstract void close() throws IOException;

    public void closeSilently() {
        try {
            close();
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
    }
}
