#ifndef INCLUDED_EVENT
#define INCLUDED_EVENT

// This modules defines the class Source for extracting characters from Linux keyboard events. Once a Source object has
// been created by specifying a device file, single characters or entire lines can be read from the keyboard.
//
// Examples of usage:
// > auto keyboard = Source("/dev/input/event0");
// > std::string line = keyboard.readline(-1).value();
// > std::cout << line << std::endl;
//
// > auto keyboard = Source("/dev/input/event0");
// > while ( true ) {
// >   auto c_opt = keyboard.read(true);
// >   if ( c_opt.has_value() ) {
// >     std::cout << c_opt.value() << std::flush;
// >   }
// > }

#include <thread>
#include <string>
#include <fstream>
#include <optional>
#include "Channel.hpp"
#include <linux/input.h>

namespace Event {

  // Type synonyms for definitions in Linux headers
  using Event = input_event;
  using Code = decltype(Event::code);

  // This class reads input events from a specified file and returns the characters from the keyboard events.
  //
  // Constructor:
  //  inputs: const std::string &path - The path to the device file
  //
  // read:
  //  This method tries to read a single character from the source, optionally blocking
  //  return: std::optional<char> - This contains a char if one was read, otherwise it is empty
  //  inputs: bool block - If true, this method will not return until a char is read, if false, it returns immediately
  //
  // readline:
  //  This method reads characters up until a newline '\n' or the specified timeout is reached.
  //  return: std::optional<std::string> - This contains a string if a complete line was read, otherwise it is empty
  //  inputs: int timeout - The timeout in milliseconds. If less than 0, this function will block
  //
  //  read_raw (private):
  //   This method reads a character code from the input source. These are different from, but convertable to, chars
  //   return: std::optional<Code> - This contains a Code if one was read, otherwise it is empty
  //   input: bool block - If true, this method blocks, if false, this function returns immediately
  class Source {
  public:
    Source(const std::string&);

    std::optional<char> read(bool);
    std::optional<std::string> readline(int);

  private:
    std::optional<Code> read_raw(bool);

    Channel::Channel<Code> channel;
    Channel::Read<Code> reader;
    std::thread source;
  };

}

#endif // INCLUDED_EVENT
