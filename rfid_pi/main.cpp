#include "Event.hpp"
#include "Pipe.hpp"
#include <iostream>

using Event::toChar;

int main(int argc, char **argv) {
  if ( argc < 2 ) {
    std::cerr << "No file specified" << std::endl;
    return -1;
  }

  auto source = Event::Source("/dev/input/event0");
  Event::Event event;

  Pipe::Pipe pipe(argv[1]);

  while( source.next(event) ) {
    if ( event.type == INPUT_PROP_DIRECT && event.value == EV_KEY ) {
      pipe << toChar(event) << std::flush;
    }
  }
}
