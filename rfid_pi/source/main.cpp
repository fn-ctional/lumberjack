#include <iostream>
#include "Event.hpp"
#include "Network.hpp"
#include "Config.hpp"

[[noreturn]] Config::Config&& handle_config_error( Config::Error );

int main() {
  const auto config = Config::load("/home/fred/.lumberjack")
                      .unwrap_or( handle_config_error );

  auto source = Event::Source(config.source);

  auto network = Network::Network::create()
                 .expect("Unable to create network object");

  const auto login_form = network.new_form()
             .add("email",    config.username)
             .add("password", config.password);

  network.send( config.login, "POST", login_form )
         .expect("Unable to log in");

  while ( true ) {

    auto user = source.readline(-1).value();

    auto device_opt = source.readline(3000);
    if ( !device_opt.has_value() ) {
      std::cerr << "[timeout]" << std::endl;
      continue;
    }

    auto data = "{\"user\":\"" + user + "\",\"device\":\"" + device_opt.value() + "\"}";
    auto result = network.send( config.path, "PATCH", data );
    if ( result.is_err() ) {
      std::cout << "[data send failed]" << std::endl;
      continue;
    }

    auto response = result.unwrap();
    std::cout << response.code << ": " << response.data << std::endl;

  }
}

[[noreturn]] Config::Config&& handle_config_error( Config::Error error ) {
  switch ( error ) {
    case Config::FileNotFound:
      throw std::logic_error( "Configuration file not found" );
    case Config::MissingFields:
      throw std::logic_error( "Configuration file missing fields" );
  }
  std::abort();
}
