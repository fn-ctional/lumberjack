#include "Event.hpp"
#include <iostream>

int main() {
  auto source = Event::Source("/dev/input/event0");
  Event::Event event;

  while( source.next(event) ) {
    if ( event.type == 1 && event.value == 1 ) {
      std::cout << event.code << std::flush;
    }
  }
}
