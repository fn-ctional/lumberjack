#include "Event.hpp"

Event::Source::Source( std::string path ): source(path) {}

bool Event::Source::next( Event &event ) {
  source.read( reinterpret_cast<char*>(&event), sizeof(Event) );
  return source.good();
}

bool operator==(const input_event &x, const input_event &y) {
  return x.type == y.type
      && x.code == y.code
      && x.value == y.value;
}

char Event::toChar(Event &event) {
  switch (event.code) {
    case KEY_1:     return '1';
    case KEY_2:     return '2';
    case KEY_3:     return '3';
    case KEY_4:     return '4';
    case KEY_5:     return '5';
    case KEY_6:     return '6';
    case KEY_7:     return '7';
    case KEY_8:     return '8';
    case KEY_9:     return '9';
    case KEY_0:     return '0';
    case KEY_ENTER: return '\n';
    default:        return '\x00';
  }
}
