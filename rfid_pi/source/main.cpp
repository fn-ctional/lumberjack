#include <iostream>
#include "Event.hpp"
#include "Connection.hpp"

const static char *URL = "localhost:8080/devices";

int main() {
  auto connection = Connection::Connection(URL);
  auto response = Connection::Response();

  auto source = Event::Source("/dev/input/event0");

  while ( true ) {

    auto user = source.readline(-1).value();

    auto device_opt = source.readline(3000);
    if ( !device_opt.has_value() ) {
      std::cerr << "[timeout]" << std::endl;
      continue;
    }

    auto data = "{\"user\":\"" + user + "\",\"device\":\"" + device_opt.value() + "\"}";
    if ( !connection.send(data, response) ) {
      std::cerr << "[data send failed]" << std::endl;
    } else {
      std::cout << response.body << std::endl;
    }

  }
}
