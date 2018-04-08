#include "Config.hpp"

#include <fstream>
#include <utility>

static int PATH =     0b00001;
static int LOGIN =    0b00010;
static int SOURCE =   0b00100;
static int USERNAME = 0b01000;
static int PASSWORD = 0b10000;
static int ALL =      0b11111;

static bool parse_line(std::ifstream&, std::string&, std::string&);

std::optional<Config::Config> Config::load(const std::string &path) {
  std::string key, value;
  Config config;
  std::ifstream file(path);

  int filled_fields = 0;

  while ( file ) {
    if ( !parse_line(file, key, value) ) continue;

    if ( !(filled_fields & PATH) && key == "path" ) {
      config.path = value;
      filled_fields |= PATH;
    }

    else if ( !(filled_fields & LOGIN) && key == "login" ) {
      config.login = value;
      filled_fields |= LOGIN;
    }

    else if ( !(filled_fields & SOURCE)  && key == "source" ) {
      config.source = value;
      filled_fields |= SOURCE;
    }

    else if ( !(filled_fields & USERNAME) && key == "username" ) {
      config.username = value;
      filled_fields |= USERNAME;
    }

    else if ( !(filled_fields & PASSWORD) && key == "password" ) {
      config.password = value;
      filled_fields |= PASSWORD;
    }
  }

  if ( filled_fields == ALL ) {
    return std::make_optional(std::move(config));
  } else {
    return std::nullopt;
  }

}

// Reads a line from file and splits it into the key and value.
// Characters are written into key until a colon is read, then characters are read into value.
// The leading spaces from value are then stripped.
//
// return: True if a line was successfully read and parsed, false otherwise.
// inputs: std::ifstream &file - A reference to the file from which characters are read
//         std::string &key    - A reference to the string into which the key is written
//         std::string &value  - A reference to the string into which the value is written
static bool parse_line(std::ifstream &file, std::string &key, std::string &value) {
  key.clear();
  value.clear();

  bool read_separator = false;
  char c;

  while ( file && (c = file.get()) != '\n' ) {

    if ( read_separator ) {
      value.push_back(c);
    } else if ( c == ':' ) {
      read_separator = true;
    } else {
      key.push_back(c);
    }

  }

  while ( !value.empty() && value[0] == ' ' ) {
    value.erase(0, 1);
  }

  return read_separator;

}
