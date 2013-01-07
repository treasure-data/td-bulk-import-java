//
// Java Extension to CUI for Treasure Data
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
package com.treasure_data.utils;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.treasure_data.commands.CommandException;

public class CSVFileReader extends FileReader {
    private static final Logger LOG = Logger.getLogger(CSVFileReader.class
            .getName());

    private String fileName;

    public CSVFileReader(Properties props, String fileName) {
        validateProperties(props);
        initReader(props, fileName);
    }

    @Override
    public void initReader(Properties props, String fileName) {
        // TODO
    }

    public void validateProperties(Properties props) {
        // TODO
    }

    public Map<String, Object> readRecord() throws CommandException {
        // TODO
        return null;
    }
}