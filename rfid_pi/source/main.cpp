#include <iostream>
#include "File.hpp"

int main(int argc, char **argv) {
  if ( argc < 2 ) {
    std::cerr << "No file specified" << std::endl;
    return -1;
  }

  File file(argv[1]);
  char buff[11];
  int err = 0;
  while ( err != Error::EndOfFile) {
    err = file.read( buff, 10 );
    std::cout << buff << std::flush;
  }
}
