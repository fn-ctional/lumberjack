#include <iostream>
#include "Event.hpp"
#include "Network.hpp"
#include "Config.hpp"

int main() {
  auto config = Config::load("/home/fred/.lumberjack").value();
  auto source = Event::Source(config.source);

  auto network = Network::Network::create()
                 .expect("Unable to create network object");

  while ( true ) {

    auto user = source.readline(-1).value();

    auto device_opt = source.readline(3000);
    if ( !device_opt.has_value() ) {
      std::cerr << "[timeout]" << std::endl;
      continue;
    }

    auto data = "{\"user\":\"" + user + "\",\"device\":\"" + device_opt.value() + "\"}";
    auto result = network.send( config.path, data );
    if ( result.is_err() ) {
      std::cout << "[data send failed]" << std::endl;
      continue;
    }

    auto response = result.expect("This shouldn't happen");
    std::cout << response.code << ": " << response.data << std::endl;

  }
}
