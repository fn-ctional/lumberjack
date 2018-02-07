#include "Event.hpp"
#include <chrono>

const int TIMEOUT = 5000;
int now();

Scanner::Scanner(const char *path)
 : file(path)
{}

bool Scanner::read_block(Event &event) {
  do {
    if ( !read_any(event, -1) ) return false;
  } while ( event.type != INPUT_PROP_DIRECT || event.value != EV_KEY );
  return true;
}

bool Scanner::read_timeout(Event &event, int timeout) {
  int end_time = now();
  int start_time;
  do {
    start_time = end_time;
    end_time = now();
    if ( timeout <= 0 ) return false;
    if ( !read_any(event, timeout) ) return false;
    timeout -= end_time - start_time;
  } while ( event.type != INPUT_PROP_DIRECT || event.value != EV_KEY );
  return true;
}

bool Scanner::read(Event &first, Event &second) {
  return read_block(first)
      && read_timeout(second, TIMEOUT);
}

bool Scanner::read_any(Event &event, int timeout) {
  auto buff = reinterpret_cast<char*>(&event);
  return file.read(buff, sizeof(Event), timeout) > 0;
}

int now() {
  using namespace std::chrono;
  return duration_cast<milliseconds>(
    steady_clock::now()
    .time_since_epoch()
  ).count();
}
