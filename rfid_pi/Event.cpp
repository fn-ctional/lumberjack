#include "Event.hpp"
#include <fstream>

using Event::Source;
using Event::Event;

Source::Source( std::string path ):
  source( new std::ifstream(path) ) {
}

Source::Source( std::istream *&&stream ):
  source( stream ) {
}

bool Source::next( Event &event ) {
  source->read( reinterpret_cast<char*>(&event), sizeof(Event) );
  return source->good();
}

bool operator==(const input_event &x, const input_event &y) {
  return x.type == y.type
      && x.code == y.code
      && x.value == y.value;
}