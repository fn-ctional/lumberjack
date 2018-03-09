#include "Connection.hpp"
#include <cstring>
#include <algorithm>

struct String {
  char const *ptr;
  size_t size;
};

size_t writer(char*, size_t, size_t, String*);
size_t reader(char*, size_t, size_t, Connection::Response*);

Connection::Connection::Connection(const char *url)
: handle( curl_easy_init() )
{
  curl_easy_setopt(handle, CURLOPT_URL, url);
  curl_easy_setopt(handle, CURLOPT_CUSTOMREQUEST, "PATCH");
  curl_easy_setopt(handle, CURLOPT_READFUNCTION, writer);
  curl_easy_setopt(handle, CURLOPT_WRITEFUNCTION, reader);
  curl_easy_setopt(handle, CURLOPT_HEADER, 1);
  curl_easy_setopt(handle, CURLOPT_COOKIEFILE, "");
}

Connection::Connection::Connection(Connection &&c)
: handle(c.handle)
{
  c.handle = nullptr;
}

Connection::Connection::~Connection() {
  if ( handle != nullptr ) {
    curl_easy_cleanup(handle);
    curl_global_cleanup();
  }
}

bool Connection::Connection::send(std::string body, Response &response) {
  auto content_len = "Content-Length: " + std::to_string(body.size());
  String data = { body.c_str(), body.size() };

  auto header = curl_slist_append(nullptr, "Content-Type: application/json");
       header = curl_slist_append(header,  content_len.c_str());
       header = curl_slist_append(header,  "Transfer-Encoding:");
  curl_easy_setopt(handle, CURLOPT_HTTPHEADER, header);

  curl_easy_setopt(handle, CURLOPT_READDATA, &data);
  curl_easy_setopt(handle, CURLOPT_UPLOAD, 1L);
  curl_easy_setopt(handle, CURLOPT_WRITEDATA, &response);
  bool success = curl_easy_perform(handle) == CURLE_OK;
  curl_slist_free_all(header);
  return success;
}

Connection::Connection::operator bool() const {
  return handle != nullptr;
}

size_t writer(char *buff, size_t size, size_t nitems, String *str) {
  size_t copy_size = std::min(str->size, size * nitems);
  memcpy(buff, str->ptr, copy_size);

  str->ptr += copy_size;
  str->size -= copy_size;

  return copy_size;
}

size_t reader(char *buff, size_t size, size_t nitems, Connection::Response *resp) {
  size_t copy_size = size * nitems;
  if ( copy_size ) {
    size_t curr_size = 0;
    if ( resp->body )
      curr_size = strlen(resp->body);
    char *str = new char[curr_size + copy_size + 1];
    if ( resp->body )
      strncpy(str, resp->body, curr_size);
    strncpy(&str[curr_size], buff, copy_size);
    str[curr_size + copy_size] = '\x00';
    delete resp->body;
    resp->body = str;
    return copy_size;
  }
  return 0;
}
