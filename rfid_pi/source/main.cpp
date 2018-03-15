#include <iostream>
#include "Event.hpp"
#include "Connection.hpp"
#include "Config.hpp"

int main() {
  auto config = Config::load("/home/fred/.lumberjack").value();

  auto connection = Connection::Connection(config.path);
  auto response = Connection::Response();

  auto source = Event::Source(config.source);

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
