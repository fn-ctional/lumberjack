#include <iostream>
#include "Event.hpp"

int main() {
  auto source = Event::Source("/dev/input/event0");
  std::string data;
  while ( true ) {
    source.readline( data , 0 );
    std::cout << data << std::endl;
  }
}
