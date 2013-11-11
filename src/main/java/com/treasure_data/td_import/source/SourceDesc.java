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
package com.treasure_data.td_import.source;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceDesc {
    private static Logger LOG = Logger.getLogger(SourceDesc.class.getName());
    private static Pattern remoteRegex = Pattern.compile("\\A(\\w+)\\:\\/\\/(?:([^@\\/\\:]*)(?:\\:([^@\\/\\:]*))?@)?([^\\/]*)(?:\\/([^\\?\\#]*)(?:\\?(.*))?)?\\z");

    protected String type;
    private String user;
    private String password;
    private String host;
    private int port;
    private String endpoint;
    private String path;

    public Object clone() {
        SourceDesc other = new SourceDesc();
        other.type = type;
        other.user = user;
        other.password = password;
        other.host = host;
        other.port = port;
        other.endpoint = endpoint;
        other.path = path;
        return other;
    }

    public static SourceDesc create(String s) {
        try {
            Matcher m;

            m = remoteRegex.matcher(s);
            if (m.matches()) {
                String rawType = m.group(1);
                String rawUser = m.group(2);
                String rawPassword = m.group(3);
                String rawEndpoint = m.group(4);
                String rawPath = m.group(5);

                String rawHost;
                int port;
                String[] hp = rawEndpoint.split(":", 2);
                if (hp.length == 2) {
                    rawHost = hp[0];
                    try {
                        port = Integer.parseInt(hp[1]);
                    } catch (NumberFormatException ex) {
                        port = 0;
                    }
                } else {
                    rawHost = hp[0];
                    port = 0;
                }

                SourceDesc src = new SourceDesc();
                src.type = rawType;
                if (rawUser != null) {
                    src.user = URLDecoder.decode(rawUser, "UTF-8");
                }
                if (rawPassword != null) {
                    src.password = URLDecoder.decode(rawPassword, "UTF-8");
                }
                src.host = rawHost; // TODO
                //src.host = URLDecoder.decode(rawHost, "UTF-8");
                src.port = port;
                src.endpoint = URLDecoder.decode(rawEndpoint, "UTF-8");
                src.path = URLDecoder.decode(rawPath, "UTF-8");

                return src;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("invalid URL: " + s, e);
        }

        throw new RuntimeException("invalid URL: " + s);
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getPort(int defaultPort) {
        return port <= 0 ? defaultPort : port;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(type);
            sb.append("://");
            if (user != null) {
                sb.append(URLEncoder.encode(user, "UTF-8"));
                if (password != null) {
                    sb.append(":");
                    sb.append(URLEncoder.encode(password, "UTF-8"));
                }
                sb.append("@");
            }
            sb.append(URLEncoder.encode(host, "UTF-8"));
            if (port != 0) {
                sb.append(":");
                sb.append(Integer.toString(port));
            }
            sb.append(URLEncoder.encode(path, "UTF-8"));

            return sb.toString();

        } catch (UnsupportedEncodingException e) {
            // "UTF-8" must be supported
            return "";
        }
    }
}