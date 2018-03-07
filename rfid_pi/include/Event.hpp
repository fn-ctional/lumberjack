#ifndef INCLUDED_EVENT
#define INCLUDED_EVENT

// This modules defines the class Source for extracting characters from Linux keyboard events. Once a Source object has
// been created by specifying a device file, single characters or entire lines can be read from the keyboard.
//
// Examples of usage:
// > auto keyboard = Source("/dev/input/event0");
// > std::string line;
// > keyboard.readline(line, -1);
// > std::cout << line << std::endl;
//
// > auto keyboard = Source("/dev/input/event0");
// > char c;
// > while ( true ) {
// >   keyboard.read(c, true);
// >   std::cout << c << std::flush;
// > }

#include <thread>
#include <string>
#include <fstream>
#include "Channel.hpp"
#include <linux/input.h>

namespace Event {

  // Type synonyms for definitions in Linux headers
  using Event = input_event;
  using Code = decltype(Event::code);

  // This class reads input events from a specified file and returns the characters from the keyboard events.
  //
  // Constructor:
  //  inputs: const char *path - The path to the device file
  //
  // read:
  //  This method tries to read a single character from the source, optionally blocking
  //  return: bool - This is true if a character was read, false otherwise
  //  inputs: char &c - The char into which the character is read
  //          bool block - If true, this method will not return until a char is read, if false, it returns immediately
  //
  // readline:
  //  This method reads characters up until a newline '\n' or the specified timeout is reached.
  //  return: bool - If a complete line is read before the timeout, this is true. False otherwise.
  //  inputs: std::string &str - A reference to a string into which the output is written, excludng the newline
  //          int timeout - The timeout in milliseconds. If less than 0, this function will block
  //
  //  read_raw (private):
  //   This method reads a character code from the input source. These are different from, but convertable to, chars
  //   return: bool - This is true if a character code is read
  //   input: Code &code - A reference to the object into which the character code is written
  //          bool block - If true, this method blocks, if false, this function returns immediately
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
