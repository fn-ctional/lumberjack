#include "Event.hpp"
#include "Pipe.hpp"

using Event::toChar;

int main() {
  auto source = Event::Source("/dev/input/event0");
  Event::Event event;

  Pipe::Pipe pipe("foo");

  while( source.next(event) ) {
    if ( event.type == INPUT_PROP_DIRECT && event.value == EV_KEY ) {
      pipe << toChar(event) << std::flush;
    }
  }
}
