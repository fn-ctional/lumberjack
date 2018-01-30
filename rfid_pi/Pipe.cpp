#include "Pipe.hpp"
#include <sys/stat.h>
#include <unistd.h>

Pipe::Pipe::Pipe(std::string path):
  std::fstream( ( mkfifo(path.c_str(), 0600), path ) ),
  path(path) {
}

Pipe::Pipe::~Pipe() {
  unlink(path.c_str());
}
