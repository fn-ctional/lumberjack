#include "Event.hpp"
#include <chrono>

void source_f(const char*, Channel::Write<Event::Code>);
char toChar(Event::Code);

auto now() {
  return std::chrono::steady_clock::now();
}

auto future(int milliseconds) {
  return now() + std::chrono::milliseconds(milliseconds);
}

Event::Source::Source(const char *path)
: reader(channel.get_read())
, source(source_f, path, channel.get_write()) {}

bool Event::Source::read(char &c, bool block) {
  Code code;
  if ( !read_raw(code, block) ) return false;
  c = toChar( code );
  return true;
}

bool Event::Source::readline(std::string &str, int timeout) {
  auto end = future(timeout);
  bool block = timeout < 0;
  str.clear();
  char c;
  while ( block || now() < end ) {
    if ( read( c, block ) ) {
      if ( c == '\n' ) return true;
      str += c;
    }
  }
  return false;
}

bool Event::Source::read_raw(Code &code, bool block) {
  if ( block ) {
    reader.read(code);
    return true;
  } else {
    return reader.try_read(code);
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

void source_f(const char *path, Channel::Write<Event::Code> chan) {
  auto file = std::ifstream(path);
  Event::Event event;
  auto buffer = reinterpret_cast<char*>( &event );
  while ( file ) {
    file.read( buffer, sizeof( Event::Event ) );
    if ( event.type == 1 && event.value == 1 ) {
      chan.write( event.code );
    }
  }
}
