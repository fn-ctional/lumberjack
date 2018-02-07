#ifndef INCLUDED_EVENT
#define INCLUDED_EVENT

#include <linux/event.h>
#include "File.hpp"

using Event = input_event;

class Scanner {
public:
  Scanner(const char*);
  bool read_block(Event&);
  bool read_timeout(Event&, int);
  bool read(Event&, Event&);
private:
  File file;
  bool read_any(Event&, int);
};

#endif // INCLUDED_EVENT
