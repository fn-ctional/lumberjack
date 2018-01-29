#include "Event.hpp"
#include <iostream>

using Event::toChar;

int main() {
  auto source = Event::Source("/dev/input/event0");
  Event::Event event;

  while( source.next(event) ) {
    if ( event.type == 1 && event.value == 1 ) {
      std::cout << toChar(event) << std::flush;
    }
  }
}
