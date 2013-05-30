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
package com.treasure_data.bulk_import;

import java.util.Properties;
import java.util.logging.Logger;

import com.treasure_data.commands.MultithreadsCommand;
import com.treasure_data.commands.bulk_import.PreparePartsCommand;
import com.treasure_data.commands.bulk_import.PreparePartsFactory;
import com.treasure_data.commands.bulk_import.PreparePartsRequest;
import com.treasure_data.commands.bulk_import.PreparePartsResult;
import com.treasure_data.commands.bulk_import.UploadPartsFactory;
import com.treasure_data.commands.bulk_import.UploadPartsRequest;
import com.treasure_data.commands.bulk_import.UploadPartsResult;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * > td bulk_import:prepare_parts2
     * usage:
     *   $ java BulkImportTool prepare_parts <files...>
     * example:
     *   $ java BulkImportTool prepareParts logs/*.csv \
     *         -Dtd.bulk_import.prepare_parts.format=csv \
     *         -Dtd.bulk_import.prepare_parts.columns=time,uid,price,count \
     *         -Dtd.bulk_import.prepare_parts.columntypes=long,string,long,int \
     *         -Dtd.bulk_import.prepare_parts.time_column=time \
     *         -Dtd.bulk_import.prepare_parts.output_dir=./parts/
     * description:
     *   Convert files into part file format
     * options:
     *   -f, --format NAME                source file format [csv]
     *   -h, --columns NAME,NAME,...      column names (use --column-header instead if the first line has column names)
     *       --column-types TYPE,TYPE,... column types [string, long, int]
     *   -H, --column-header              first line includes column names
     *   -t, --time-column NAME           name of the time column
     *       --time-value TIME            long value of the time column
     *   -s, --split-size SIZE_IN_KB      size of each parts (default: 16384)
     *   -o, --output DIR                 output directory
     *
     * @param args
     * @param props
     * @throws Exception
     */
    public static void prepareParts(final String[] args, Properties props)
            throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("File names not specified");
        }

        LOG.info(String.format("Start %s command", Config.CMD_PREPARE_PARTS));

        String[] fileNames = new String[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            fileNames[i] = args[i + 1];
        }

        PreparePartsRequest request = PreparePartsFactory.newInstance(
                fileNames, props);
        PreparePartsResult result = new PreparePartsResult();

        PreparePartsCommand command = new PreparePartsCommand();
        new MultithreadsCommand<PreparePartsRequest, PreparePartsResult>(
                command).execute(request, result);
    }

    /**
     * > td bulk_import:upoad_parts2
     * usage:
     *   $ td bulk_import:upload_parts <name> <files...>
     * example:
     *   $ td bulk_import:upload_parts parts/* --parallel 4
     * description:
     *   Upload or re-upload files into a bulk import session
     * options:
     *   -P, --prefix NAME       add prefix to parts name
     *   -s, --use-suffix COUNT  use COUNT number of . (dots) in the source file name to the parts name
     *       --auto-perform      perform bulk import job automatically
     *       --parallel NUM      perform uploading in parallel (default: 2; max 8)
     */
    public static void uploadParts(final String[] args, Properties props)
            throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("File names not specified");
        }

        LOG.info(String.format("Start %s command", Config.CMD_UPLOAD_PARTS));

        String sessionName = args[1];
        String[] fileNames = new String[args.length - 2];
        for (int i = 0; i < args.length - 2; i++) {
            fileNames[i] = args[i + 2];
        }

        UploadPartsRequest request = UploadPartsFactory.newRequestInstance(
                sessionName, fileNames, props);
        UploadPartsResult result = UploadPartsFactory.newResultInstance(request);

        UploadPartsFactory.newCommandInstance(request).execute(request, result);
    }

    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Command not specified");
        }

        String commandName = args[0];
        Properties props = System.getProperties();
        if (commandName.equals(Config.CMD_PREPARE_PARTS)) {
            prepareParts(args, props);
        } else if (commandName.equals(Config.CMD_UPLOAD_PARTS)) {
            uploadParts(args, props);
        } else {
            throw new IllegalArgumentException(
                    "Not support command: " + commandName);
        }
    }
}