#ifndef INCLUDED_EVENTS
#define INCLUDED_EVENTS

#include <string>
#include <fstream>

#include <linux/input.h>

namespace Event {

  using Event = input_event;

  class Source {
    std::ifstream source;
  public:
    Source(std::string);
    bool next(Event&);
  };

  char toChar(Event&);

}

#endif // INCLUDED_EVENTS
