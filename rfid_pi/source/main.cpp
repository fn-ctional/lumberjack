#include <iostream>
#include "Event.hpp"
#include "Connection.hpp"

const static char *URL = "localhost:8080/devices";

int main() {
  auto connection = Connection::Connection(URL);
  auto response = Connection::Response();

  auto source = Event::Source("/dev/input/event0");
  std::string user, device;

  while ( true ) {

    source.readline( user , -1 );

    if ( !source.readline( device, 3000 ) ) {
      std::cerr << "[timeout]" << std::endl;
      continue;
    }

    auto data = "{\"user\":\"" + user + "\",\"device\":\"" + device + "\"}";
    if ( !connection.send(data, response) ) {
      std::cerr << "[data send failed]" << std::endl;
    } else {
      std::cout << data << std::endl;
    }

  }
}
