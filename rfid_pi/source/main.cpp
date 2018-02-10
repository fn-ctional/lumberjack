#include <iostream>
#include "Event.hpp"

int main() {
  auto source = Event::Source("/dev/input/event0");
  std::string data;
  
  while ( true ) {
    source.readline( data , -1 );
    std::cout << data << std::endl;

    if ( source.readline( data, 3000) ) {
      std::cout << data << std::endl;
    } else {
      std::cerr << "[timeout]" << std::endl;
    }

    std::cout << std::endl;
  }
}
