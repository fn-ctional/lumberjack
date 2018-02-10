#ifndef INCLUDED_EVENT
#define INCLUDED_EVENT

#include <thread>
#include <string>
#include <fstream>
#include "Channel.hpp"
#include <linux/input.h>

namespace Event {

  using Event = input_event;
  using Code = decltype(Event::code);

  class Source {
  public:
    Source(const char*);

    bool read(char&, bool);
    bool readline(std::string&, int);

  private:
    bool read_raw(Code&, bool);

    Channel::Channel<Code> channel;
    Channel::Read<Code> reader;
    std::thread source;
  };

}

#endif // INCLUDED_EVENT
