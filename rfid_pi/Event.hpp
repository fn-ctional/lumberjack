#ifndef INCLUDED_EVENTS
#define INCLUDED_EVENTS

#include <string>
#include <istream>
#include <memory>

#include <linux/input.h>

namespace Event {

  using Event = input_event;

  class Source {
    std::unique_ptr<std::istream> source;
  public:
    Source(std::string);
    Source(std::istream*&&);
    bool next(Event&);
  };

}

#endif // INCLUDED_EVENTS
