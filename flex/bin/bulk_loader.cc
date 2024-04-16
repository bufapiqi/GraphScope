/** Copyright 2020 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <csignal>
#include <filesystem>
#include <iostream>

#include <glog/logging.h>

#include <boost/program_options.hpp>

#include "flex/engines/graph_db/database/graph_db.h"
#include "flex/engines/http_server/options.h"

namespace bpo = boost::program_options;

static std::string work_dir;

void signal_handler(int signal) {
  LOG(INFO) << "Received signal " << signal << ", exiting...";
  // support SIGKILL, SIGINT, SIGTERM
  if (signal == SIGKILL || signal == SIGINT || signal == SIGTERM ||
      signal == SIGSEGV || signal == SIGABRT) {
    LOG(ERROR) << "Received signal " << signal
               << ",Clearing directory: " << work_dir << ", exiting...";
    // remove all files in work_dir
    std::filesystem::remove_all(work_dir);
    exit(0);
  } else {
    LOG(ERROR) << "Received unexpected signal " << signal << ", exiting...";
    exit(1);
  }
}

int main(int argc, char** argv) {
  bpo::options_description desc("Usage:");
  desc.add_options()("help", "Display help message")(
      "version,v", "Display version")("parallelism,p",
                                      bpo::value<uint32_t>()->default_value(1),
                                      "parallelism of bulk loader")(
      "data-path,d", bpo::value<std::string>(), "data directory path")(
      "graph-config,g", bpo::value<std::string>(), "graph schema config file")(
      "bulk-load,l", bpo::value<std::string>(), "bulk-load config file");
  google::InitGoogleLogging(argv[0]);
  FLAGS_logtostderr = true;

  bpo::variables_map vm;
  bpo::store(bpo::command_line_parser(argc, argv).options(desc).run(), vm);
  bpo::notify(vm);

  if (vm.count("help")) {
    std::cout << desc << std::endl;
    return 0;
  }

  if (vm.count("version")) {
    std::cout << "GraphScope/Flex version " << FLEX_VERSION << std::endl;
    return 0;
  }

  uint32_t parallelism = vm["parallelism"].as<uint32_t>();
  std::string data_path = "";
  std::string bulk_load_config_path = "";
  std::string graph_schema_path = "";

  if (!vm.count("graph-config")) {
    LOG(ERROR) << "graph-config is required";
    return -1;
  }
  graph_schema_path = vm["graph-config"].as<std::string>();
  if (!vm.count("data-path")) {
    LOG(ERROR) << "data-path is required";
    return -1;
  }
  data_path = vm["data-path"].as<std::string>();
  if (!vm.count("bulk-load")) {
    LOG(ERROR) << "bulk-load-config is required";
    return -1;
  }
  bulk_load_config_path = vm["bulk-load"].as<std::string>();

  setenv("TZ", "Asia/Shanghai", 1);
  tzset();

  double t = -grape::GetCurrentTime();

  auto schema_res = gs::Schema::LoadFromYaml(graph_schema_path);
  if (!schema_res.ok()) {
    LOG(ERROR) << "Fail to load graph schema file: "
               << schema_res.status().error_message();
    return -1;
  }
  auto loading_config_res = gs::LoadingConfig::ParseFromYamlFile(
      schema_res.value(), bulk_load_config_path);
  if (!loading_config_res.ok()) {
    LOG(ERROR) << "Fail to parse loading config file: "
               << loading_config_res.status().error_message();
    return -1;
  }

  std::filesystem::path data_dir_path(data_path);
  if (!std::filesystem::exists(data_dir_path)) {
    std::filesystem::create_directory(data_dir_path);
  }
  std::filesystem::path serial_path = data_dir_path / "schema";
  if (std::filesystem::exists(serial_path)) {
    LOG(WARNING) << "data directory is not empty: " << data_dir_path.string()
                 << ", please remove the directory and try again.";
    return -1;
  }

  work_dir = data_dir_path.string();

  // Register handlers for SIGKILL, SIGINT, SIGTERM, SIGSEGV, SIGABRT
  // LOG(FATAL) cause SIGABRT
  std::signal(SIGINT, signal_handler);
  std::signal(SIGTERM, signal_handler);
  std::signal(SIGKILL, signal_handler);
  std::signal(SIGSEGV, signal_handler);
  std::signal(SIGABRT, signal_handler);

  auto loader = gs::LoaderFactory::CreateFragmentLoader(
      data_dir_path.string(), schema_res.value(), loading_config_res.value(),
      parallelism);
  loader->LoadFragment();

  t += grape::GetCurrentTime();
  LOG(INFO) << "Finished bulk loading in " << t << " seconds.";

  return 0;
}
