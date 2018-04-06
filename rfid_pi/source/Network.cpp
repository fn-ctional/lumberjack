#include "Network.hpp"
#include <cstring>
#include <algorithm>
#include <iostream>

struct String {
  char const *ptr;
  size_t size;
};

size_t write_data(char*, size_t, size_t, String*);
size_t read_response(char*, size_t, size_t, Network::Response*);


Network::Form::Form( curl_mime *mime ) noexcept
: form( mime, _delete )
{}

Network::Form& Network::Form::add(const std::string &name, const std::string &data) {
  curl_mimepart *part = curl_mime_addpart( form.get() );
  curl_mime_name( part, name.c_str() );
  curl_mime_data( part, data.c_str(), data.length() );

  return *this;
}

void Network::Form::_delete(curl_mime *mime) {
  std::cerr << "Freeing a form!" << std::endl;
  curl_mime_free( mime );
}


Result<Network::Network,int> Network::Network::create() {
  auto handle = curl_easy_init();
  if ( handle == nullptr ) return Result<Network,int>::Err(0);

  // curl_easy_setopt(handle, CURLOPT_READFUNCTION, write_data);
  // curl_easy_setopt(handle, CURLOPT_WRITEFUNCTION, read_response);
  curl_easy_setopt(handle, CURLOPT_COOKIEFILE, "");

  // auto header = curl_slist_append(nullptr, "Transfer-Encoding:");

  return Result<Network,int>::Ok( handle );
}


Network::Network::Network(CURL *handle) noexcept
: handle( handle, _delete )
{}

void Network::Network::_delete( CURL *handle ) {
  std::cerr << "Freeing a form!" << std::endl;
  curl_easy_cleanup( handle );
  curl_global_cleanup();
}

Result<Network::Response,int> perform( CURL *handle, curl_slist *header ) {
  header = curl_slist_append( header, "Transfer-Encoding:");
  curl_easy_setopt(handle, CURLOPT_HTTPHEADER, header);

  Network::Response response;
  curl_easy_setopt( handle, CURLOPT_WRITEDATA, &response );

  // Something in here breaks stdio somehow?
  // I'm probably writing to a wrong pointer or something.
  // Maybe some cURL stuff is freed too soon.
  auto error = curl_easy_perform( handle );
  curl_slist_free_all( header );
  if ( error != CURLE_OK ) return Result<Network::Response,int>::Err(error);

  curl_easy_getinfo(handle, CURLINFO_RESPONSE_CODE, &response.code);
  char *redirect;
  curl_easy_getinfo(handle, CURLINFO_REDIRECT_URL, &redirect);
  if ( redirect != nullptr ) {
    response.data = redirect;
  }

  return Result<Network::Response,int>::Ok(response);
}

Result<Network::Response,int> Network::Network::send(const std::string &url, const std::string &body) {
  auto length = body.size();
  auto content_len = "Content-Length: " + std::to_string( length );
  auto header = curl_slist_append( nullptr, content_len.c_str() );

  curl_easy_setopt( handle.get(), CURLOPT_URL, url.c_str() );

  auto upload = length != 0;
  auto data = String{ body.c_str(), length };
  curl_easy_setopt( handle.get(), CURLOPT_UPLOAD, upload );
  if ( upload ) {
    curl_easy_setopt( handle.get(), CURLOPT_READDATA, &data );
  }

  return perform( handle.get(), header );
}

Result<Network::Response,int> Network::Network::send(const std::string &url, const Form &form) {
  curl_easy_setopt( handle.get(), CURLOPT_MIMEPOST, form.form.get() );
  curl_easy_setopt( handle.get(), CURLOPT_URL, url.c_str() );

  return perform( handle.get(), nullptr );
}






// Result<Network::Response,int> Network::Network::send(const std::string &body) {
//   auto length = body.size();
//   auto content_len = "Content-Length: " + std::to_string(length);
//   header = curl_slist_append(header, content_len.c_str());
//   curl_easy_setopt(handle, CURLOPT_HTTPHEADER, header);
//
//   auto upload = length != 0;
//   auto data = String{ body.c_str(), length };
//   curl_easy_setopt(handle, CURLOPT_UPLOAD, upload);
//   if ( upload ) {
//     curl_easy_setopt(handle, CURLOPT_READDATA, &data);
//   }
//
//   Response response;
//   curl_easy_setopt(handle, CURLOPT_WRITEDATA, &response);
//
//   auto error = curl_easy_perform(handle);
//   if ( error != CURLE_OK ) return Result<Network::Response,int>::Err(error);
//
//   curl_easy_getinfo(handle, CURLINFO_RESPONSE_CODE, &response.code);
//   char *redirect;
//   curl_easy_getinfo(handle, CURLINFO_REDIRECT_URL, &redirect);
//   if ( redirect != nullptr ) {
//     response.data = redirect;
//   }
//
//   return Result<Network::Response,int>::Ok(response);
// }

size_t write_data(char *buff, size_t size, size_t n, String *str) {
  size_t copy_size = std::min(str->size, size * n);
  if ( copy_size > 0 ) {
    memcpy(buff, str->ptr, copy_size);
    str->ptr  += copy_size;
    str->size -= copy_size;
  }
  return copy_size;
}

size_t read_response(char* buff, size_t size, size_t n, Network::Response *resp) {
  size_t copy_size = size * n;
  if ( copy_size > 0 ) {
    auto end = resp->data.size();
    resp->data.insert(end, buff, copy_size);
  }
  return copy_size;
}
