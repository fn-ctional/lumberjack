#ifndef INCLUDED_PIPE
#define INCLUDED_PIPE

#include <fstream>
#include <string>

namespace Pipe {

  class Pipe : public std::fstream {
    std::string path;
  public:
    Pipe(std::string);
    ~Pipe();
  };

}

#endif // INCLUDED_PIPE
