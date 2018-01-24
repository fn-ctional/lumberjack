#include "Events.hpp"
#include <fstream>

using std::make_unique;

Events::Events(std::string path) : source(new std::ifstream(path)) {}

Events& Events::begin() {
  return ++(*this);
}

Events Events::end() {
  return Events();
}

input_event& Events::operator*() {
  return event;
}

Events& Events::operator++() {
  source->read((char*)&source, sizeof(input_event));
}

bool operator==(const input_event &x, const input_event &y) {
  return x.type == y.type
      && x.code == y.code
      && x.value == y.value;
}

bool Events::operator==(const Events &other) const {
  return ( source->eof() && other.source->eof() )
      || ( event == other.event );
}

bool Events::operator!=(const Events &other) const {
  return !( *this == other );
}
