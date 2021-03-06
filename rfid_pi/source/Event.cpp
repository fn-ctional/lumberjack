#include "Event.hpp"
#include <chrono>
#include <type_traits>

void source_f(const std::string&, Channel::Write<Event::Code>);
char toChar(Event::Code);

auto now() {
  return std::chrono::steady_clock::now();
}

auto future(int milliseconds) {
  return now() + std::chrono::milliseconds(milliseconds);
}

// A Source object stores a Channel and its reader.
// The Writer is passed, along with the event source path, to a thread that runs the source_f function.
Event::Source::Source(const std::string &path)
: reader(channel.get_read().value())
, source(source_f, path, channel.get_write().value()) {}

std::optional<char> Event::Source::read(bool block) {
  auto opt = read_raw(block);

  if ( opt.has_value() ) {
    return std::optional( toChar( opt.value() ) );
  } else {
    return std::nullopt;
  }
}

std::optional<std::string> Event::Source::readline(int timeout) {
  auto end = future(timeout);
  bool block = timeout < 0;
  std::string str;

  while ( block || now() < end ) {
    auto c_opt = read( block );

    if ( c_opt ) {
      auto c = c_opt.value();
      if ( c == '\n' ) return std::optional(str);
      str += c;
    }
  }

  return std::nullopt;
}

std::optional<Event::Code> Event::Source::read_raw(bool block) {
  if ( block ) {
    return reader.read();
  } else {
    return reader.try_read();
  }
}

char toChar(Event::Code code) {
  switch ( code ) {
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
  }
  return '\x00';
}

// Events are read from the file specified by path.
// By communicating over a channel, a source object can perform non-blocking reads.
void source_f(const std::string &path, Channel::Write<Event::Code> chan) {
  auto file = std::ifstream(path);
  Event::Event event;
  auto buffer = reinterpret_cast<char*>( &event );
  while ( file ) {
    file.read( buffer, sizeof( Event::Event ) );
    // Keyboard events have the type and value 1
    if ( event.type == 1 && event.value == 1 ) {
      chan.write( event.code );
    }
  }
}
