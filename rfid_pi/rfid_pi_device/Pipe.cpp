#include "Pipe.hpp"
#include <sys/stat.h>

std::fstream Pipe::make(std::string path) {
  mkfifo(path.c_str(), 0600);
  return std::fstream(path);
}
