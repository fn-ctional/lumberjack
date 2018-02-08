#include <iostream>
#include <fstream>
#include <thread>
#include "Channel.hpp"

#include <linux/input.h>

using Event = input_event;
using Code = decltype(Event::code);

void reader(const char *path, Channel::Write<Code> chan) {
  auto file = std::ifstream(path);
  Event event;
  while ( file ) {
    file.read(reinterpret_cast<char*>(&event), sizeof(Event));
    if ( event.type == 1 && event.value == 1 ) {
      chan.write(event.code);
    }
  }
}

void writer(Channel::Read<Code> chan) {
  while ( true ) {
    Code code;
    chan.read(code);
    std::cout << "[writer] '" << code << "'" << std::endl;
  }
}

int main(int argc, char **argv) {
  if ( argc < 2 ) {
    std::cerr << "No file specified" << std::endl;
    return -1;
  }

  Channel::Channel<Code> channel;

  auto t_reader = std::thread( reader, argv[1], channel.get_write() );
  auto t_writer = std::thread( writer, channel.get_read() );

  t_reader.join();
  t_writer.join();

}
