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

import java.io.File;

import com.treasure_data.commands.CommandException;
import com.treasure_data.commands.Config;
import com.treasure_data.commands.bulk_import.PreparePartsRequest;

public class FileParserFactory {

    private static final String CSV = "csv";

    public static FileParser newInstance(PreparePartsRequest request, File file)
            throws CommandException {
        String format = request.getFormat();
        if (format.equals(CSV)) {
            return new CSVFileParser(request, file);
        } else {
            // TODO any more type...
            throw new CommandException("Invalid format: "
                    + Config.BI_PREPARE_PARTS_FORMAT);
        }
    }
}