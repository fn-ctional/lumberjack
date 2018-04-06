#ifndef INCLUDED_NETWORK
#define INCLUDED_NETWORK

#include <curl/curl.h>
#include <string>
#include <memory>
#include "Result.hpp"

namespace Network {

  struct Response {
    uint code;
    std::string data;
  };

  class Form;

  class Network {
  public:
    static Result<Network,int> create();

    Network(CURL*) noexcept;

    Form new_form() const;

    Result<Response,int> send(const std::string&, const std::string&);
    Result<Response,int> send(const std::string&, const Form&);
  private:
    static void _delete(CURL*);
    std::unique_ptr<CURL, decltype(&_delete)> handle;
  };

  class Form {
  public:
    Form(curl_mime*) noexcept;
    Form& add(const std::string&, const std::string&);

  private:
    friend Result<Response,int> Network::send(const std::string&, const Form&);
    static void _delete(curl_mime*);
    std::unique_ptr<curl_mime, decltype(&_delete)> form;
  };

}

#endif // INCLUDED_NETWORK
