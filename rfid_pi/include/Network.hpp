#ifndef INCLUDED_NETWORK
#define INCLUDED_NETWORK

// This module defines classes used for network interactions. A Network object needs to created which manages all
// connections. Strings or Forms can be sent using this class and a Response object is returned.
//
// Examples of usage:
//
// > auto network = Network::create().get_ok();
// > network.send( "http://localhost/", "PUT", "Hello, world!" );
//
// > auto network = Network::create().get_ok();
// > auto form = network.new_form()
// >             .add("field_1", "value_1")
// >             .add("field_2", "value_2");
// > network.send( "http://localhost/", "POST", form );

#include <curl/curl.h>
#include <string>
#include <memory>
#include "Result.hpp"

namespace Network {

  // This class contains the HTTP response code and data returned from a HTTP request.
  struct Response {
    uint code;
    std::string data;
  };

  class Form;


  // This class does stuff.
  //
  // Constructor (private):
  //  inputs: CURL *handle - Pointer to the CURL handle used
  //
  // create:
  //  Attempts to create a new Network object
  //  inputs: None
  //  return: Result<Network,int> - This contains the Network object if it was successfully created,
  //                                if it wasn't, the error contains the curl error code
  //
  // new_form (const):
  //  Creates a new form that can be sent with this object
  //  inputs: None
  //  return: Form - The newly created form
  //
  // send [ std::string ]:
  //  Sends a string to a URL
  //  inputs: const std::string &url  - The URL to which the request is sent
  //          const std::string &verb - The HTTP verb used ( e.g. GET, PUT )
  //          const std::string &body - The body of the request
  //  return: Result<Response,int>    - This contains the Response object if the send was successful,
  //                                    if it wasn't, the error contains the CURL error code
  //                                    ( note: a response containing an error code is a success for this function )
  //
  // send [ Form ]:
  //  Sends a Form to a URL
  //  inputs: const std::string &url  - The URL to which the request is sent
  //          const std::string &verb - The HTTP verb used ( e.g. GET, PUT )
  //          const Form &body        - The form to send as the request
  //  return: Result<Response,int>    - This contains the Response object if the send was successful,
  //                                    if it wasn't, the error contains the CURL error code
  //                                    ( note: a response containing an error code is a success for this function )
  //
  // _delete (static):
  //  The Deleter for the unique_ptr
  //  inputs: CURL *handle - The handle to delete
  //  return: None
  class Network {
  public:
    static Result<Network,int> create();

    Network(CURL*) noexcept;

    Form new_form() const;

    Result<Response,int> send(const std::string&, const std::string&, const std::string&);
    Result<Response,int> send(const std::string&, const std::string&, const Form&);
  private:
    static void _delete(CURL*);
    std::unique_ptr<CURL, decltype(&_delete)> handle;
  };


  // This class is used for creating forms.
  //
  // Constructor (private):
  //  inputs: curl_mime *mime - The CURL mime handle for this form
  //
  // add:
  //  inputs: const std::string &field - The field to add
  //          const std::string &data  - The data associated with the field
  //  return: Form&&                   - A reference to 'this' to facilitate method chaining
  //
  // _delete (static):
  //  The Deleter for the unique_ptr
  //  inputs: CURL *mime - The mime handle to delete
  //  return: None
  class Form {
  public:
    Form(curl_mime*) noexcept;
    Form&& add(const std::string&, const std::string&);

  private:
    friend Result<Response,int> Network::send(const std::string&, const std::string&, const Form&);
    static void _delete(curl_mime*);
    std::unique_ptr<curl_mime, decltype(&_delete)> form;
  };

}

#endif // INCLUDED_NETWORK
