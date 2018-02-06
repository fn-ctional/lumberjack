#include <poll.h>
#include <fcntl.h>
#include <unistd.h>
#include <cstring>

#include "File.hpp"

File::File(const char *path)
 : file_descriptor(open(path, O_RDONLY))
{}

File::File(File &&file)
 : file_descriptor(file.file_descriptor)
{
  file.file_descriptor = -1;
}

File::~File() {
  if (file_descriptor >= 0) {
    close(file_descriptor);
  }
}

int File::read(char *buff, int len, int timeout) {
  std::memset(buff, 0, len);
  auto pfd = pollfd{ file_descriptor, POLLIN, 0 };
  int read_len = 0;
  for ( ; read_len < len; ++read_len, ++buff ) {
    auto err = poll(&pfd, 1, timeout);
    if ( err == -1 ) {
      return Error::Unknown;
    } else
    if ( err == -1 ) {
      return Error::Timeout;
    } else
    if ( ::read(file_descriptor, buff, 1) != 1 ) {
      return Error::EndOfFile;
    }
  }
  return read_len;
}
