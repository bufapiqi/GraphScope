package com.alibaba.graphscope.groot.dataload;

import com.alibaba.graphscope.groot.dataload.databuild.OfflineBuildOdps;
import com.alibaba.graphscope.groot.dataload.util.HttpClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadTool {

    private static final Logger logger = LoggerFactory.getLogger(LoadTool.class);

    public static final String VIP_SERVER_HOST_URL = "http://jmenv.tbsite.net:8080/vipserver/serverlist";

    public static final String VIP_SERVER_GET_DOMAIN_ENDPOINT_URL = "http://%s:80/vipserver/api/srvIPXT?dom=%s&qps=0&clientIP=127.0.0.1&udpPort=55963&encoding=GBK&";

    public static void ingest(String configPath) throws IOException {
        new IngestDataCommand(configPath).run();
    }

    public static void commit(String configPath) throws IOException {
        new CommitDataCommand(configPath).run();
    }

    public static void main(String[] args) throws ParseException, IOException {
        Options options = new Options();
        options.addOption(
                Option.builder("c")
                        .longOpt("command")
                        .hasArg()
                        .argName("COMMAND")
                        .desc("supported COMMAND: ingest / commit")
                        .build());
        options.addOption(
                Option.builder("f")
                        .longOpt("config")
                        .hasArg()
                        .argName("CONFIG")
                        .desc("path to configuration file")
                        .build());
        options.addOption(Option.builder("h").longOpt("help").desc("print this message").build());
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        String command = commandLine.getOptionValue("command");
        String configPath = commandLine.getOptionValue("config");

        if (commandLine.hasOption("help") || command == null) {
            printHelp(options);
        } else if (command.equalsIgnoreCase("ingest")) {
            ingest(configPath);
        } else if (command.equalsIgnoreCase("commit")) {
            commit(configPath);
        } else if (command.equalsIgnoreCase("ingestAndCommit")) {
            ingest(configPath);
            commit(configPath);
        } else {
            printHelp(options);
        }
    }

    public static String getEndpointFromVipServerDomain(String domain) throws Exception {
        String srvResponse = HttpClient.doGet(VIP_SERVER_HOST_URL, null);
        String[] srvList = srvResponse.split("\n");
        for (String srv : srvList) {
            String url = String.format(VIP_SERVER_GET_DOMAIN_ENDPOINT_URL, srv, domain);
            logger.info("url is {}", url);
            try {
                String endpointResponse = HttpClient.doGet(url, null);
                JsonParser parser = new JsonParser();
                JsonObject endpointResponseJson = parser.parse(endpointResponse).getAsJsonObject();
                JsonArray endpointJsonArray = endpointResponseJson.getAsJsonArray("hosts");
                for (JsonElement element : endpointJsonArray) {
                    JsonObject endpointJson = element.getAsJsonObject();
                    boolean isValid = endpointJson.get("valid").getAsBoolean();
                    if (isValid) {
                        String ip = endpointJson.get("ip").getAsString();
                        String port = endpointJson.get("port").getAsString();
                        return ip + ":" + port;
                    }
                }
            }catch (Exception e) {
                // continue
            }
        }
        return null;
    }

    /**
     *
     * @param dateStr
     * @param dateFormatStr eg. yyyyMMdd
     * @return
     */
    public static Long transferDateToTimeStamp(String dateStr, String dateFormatStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        try {
            Date date = dateFormat.parse(dateStr);
            return date.getTime();
        }catch (java.text.ParseException e) {
            return null;
        }
    }

    private static void printHelp(Options options) {
        new HelpFormatter().printHelp("load_tool", options, true);
    }
}
