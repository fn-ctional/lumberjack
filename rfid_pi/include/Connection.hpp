#ifndef INCLUDED_CONNECTION
#define INCLUDED_CONNECTION

#include <curl/curl.h>
#include <string>

namespace Connection {

  class Response {
  public:
    unsigned int code;
    std::string body;
  };

  class Connection {
  public:
    Connection(const char*);
    Connection(Connection&) = delete;
    Connection(Connection&&);
    ~Connection();

    bool send(std::string, Response&);
    operator bool() const;
  private:
    CURL *handle;
  };

}

#endif // INCLUDED_CONNECTION
