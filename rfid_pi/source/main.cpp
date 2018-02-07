#include <iostream>
#include "Event.hpp"

int main(int argc, char **argv) {
  if ( argc < 2 ) {
    std::cerr << "No file specified" << std::endl;
    return -1;
  }

  Scanner scanner(argv[1]);
  Event user, device;
  if ( scanner.read(user, device) ) {
    std::cout << user.code << device.code << std::endl;
  } else {
    std::cerr << "Oh no!" << std::endl;
  }
}
