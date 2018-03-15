#ifndef INCLUDED_CONFIG
#define INCLUDED_CONFIG

// This module allows for reading data from a configuration file.
// Each line have the key, a colon, any number of spaces, and the value. As a regular expression:
//   (?<key>[^:\n]*): *(?<value>[^\n]*)\n
// Any line that does not match this form or does not use one of the known keys is discarded.
// Lines with repeated keys are ignored.
//
// Example of usage:
// > auto config_opt = Config::load("/path/to/config_file");
// > if ( !config_opt ) {
// >   exit(-1);
// > }
// > auto config = config_opt.value();

#include <optional>

namespace Config {

  class Config {
  public:
    Config() = default;
    Config(Config&) = delete;
    Config(Config&&);
    ~Config();

    const char *path     = nullptr,
               *login    = nullptr,
               *source   = nullptr,
               *username = nullptr,
               *password = nullptr;
  };

  // Attempts to create a Config object from a file. See the description above for the format
  //
  // return: std::optional<Config> - This contains a Config object if the file contained all the fields
  // inputs: const char *path      - The path to the configuration file
  std::optional<Config> load(const char*);

}

#endif // INCLUDED_CONFIG
