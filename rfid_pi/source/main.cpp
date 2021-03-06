#include <iostream>
#include "Event.hpp"
#include "Network.hpp"
#include "Config.hpp"

[[noreturn]] Config::Error&& throw_on_config_error( Config::Error );

int main() {
  using namespace std::string_literals;
  
  const auto config = Config::load( std::getenv("HOME") + "/.lumberjack"s )
                      .map_err( throw_on_config_error )
                      .get_ok();

  auto source = Event::Source(config.source);

  auto network = Network::Network::create()
                 .get_ok("Unable to create network object");

  const auto login_form = network.new_form()
             .add("username", config.username)
             .add("password", config.password);

  while ( true ) {

    auto user = source.readline(-1).value();

    auto device_opt = source.readline(3000);
    if ( !device_opt.has_value() ) {
      std::cerr << "[timeout]" << std::endl;
      continue;
    }

    auto data = "{\"user\":\"" + user + "\",\"device\":\"" + device_opt.value() + "\"}";

    for (uint attempts = 1; attempts <= 3; ++attempts ) {

      auto result = network.send( config.path, "PATCH", data );

      if ( result.is_err() ) {
        std::cout << "Data send failed (attempt " << attempts << "/3), "
                  << "error code " << result.get_err() << std::endl;
        continue;
      }

      auto response = result.get_ok();

      if ( response.code == 302 ) {
        auto login_response = network.send( config.login, "POST", login_form )
                                     .get_ok("Login connection failed");
        std::cout << "Login: " << login_response.code
                  << " - " << login_response.data
                  << std::endl;
        continue;
      }

      std::cout << response.code << ": " << response.data << std::endl;
      break;
    }

  }
}

[[noreturn]] Config::Error&& throw_on_config_error( Config::Error error ) {
  switch ( error ) {
    case Config::FileNotFound:
      throw std::logic_error( "Configuration file not found" );
    case Config::MissingFields:
      throw std::logic_error( "Configuration file missing fields" );
  }
  std::abort();
}
