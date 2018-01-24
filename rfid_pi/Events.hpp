#ifndef INCLUDED_EVENTS
#define INCLUDED_EVENTS

#include <string>
#include <istream>
#include <memory>

#include <linux/input.h>

using StreamPtr = std::shared_ptr<std::istream>;

class Events {
  input_event event;
  StreamPtr source;
public:
  Events() = default;
  Events(std::string);

  Events& begin();
  Events end();

  input_event& operator*();
  Events& operator++();

  bool operator==(const Events&) const;
  bool operator!=(const Events&) const;
};

#endif // INCLUDED_EVENTS
