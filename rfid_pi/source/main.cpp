#include <iostream>
#include <fstream>
#include <thread>
#include "Channel.hpp"

void reader(const char *path, Channel::Write<char> chan) {
  auto file = std::ifstream(path);
  char c;
  while ( file ) {
    file.get(c);
    chan.write(c);
  }

  chan.write('\x00');
}

void writer(Channel::Read<char> chan) {
  char c;
  while ( (chan.read(c), c) ) {
    std::cout << c << std::flush;
  }
}

int main(int argc, char **argv) {
  if ( argc < 2 ) {
    std::cerr << "No file specified" << std::endl;
    return -1;
  }

  Channel::Channel<char> channel;

  auto t_reader = std::thread( reader, argv[1], channel.get_write() );
  auto t_writer = std::thread( writer, channel.get_read() );

  t_reader.join();
  t_writer.join();

}
