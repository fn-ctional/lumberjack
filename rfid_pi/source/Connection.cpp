#include "Connection.hpp"

struct String {
  char const *start, *end;
};

size_t writer(char*, size_t, size_t, String*);

Connection::Connection::Connection(std::string url)
: url(url)
, handle( curl_easy_init() )
{
  curl_easy_setopt(handle, CURLOPT_URL, url.c_str());
  curl_easy_setopt(handle, CURLOPT_CUSTOMREQUEST, "PATCH");
  curl_easy_setopt(handle, CURLOPT_READFUNCTION, writer);
}

Connection::Connection::Connection(Connection &&c)
: url(c.url)
, handle(c.handle)
{
  c.handle = nullptr;
}

Connection::Connection::~Connection() {
  if ( handle != nullptr ) {
    curl_easy_cleanup(handle);
    curl_global_cleanup();
  }
}

bool Connection::Connection::send(std::string body, Response&) {
  String str = { body.c_str(), body.c_str() + body.size() };
  curl_easy_setopt(handle, CURLOPT_READDATA, &str);
  curl_easy_setopt(handle, CURLOPT_UPLOAD, 1L);
  return curl_easy_perform(handle) == CURLE_OK;
}

Connection::Connection::operator bool() const {
  return handle != nullptr;
}

size_t writer(char *buff, size_t size, size_t nitems, String *str) {
  size_t i = 0;
  for ( ; i < size*nitems && str->start!=str->end; ++i ) {
    buff[i] = *(str->start++);
  }
  return i;
}
