#ifndef INCLUDED_FILE
#define INCLUDED_FILE

enum Error : int {
    Unknown   = -1
  , EndOfFile = -2
  , Timeout   = -3
};

class File {
public:
  File(const char*);
  File(File&) = delete;
  File(File&&);
  ~File();

  int read(char*, int, int = -1);
private:
  int file_descriptor;
};

#endif // INCLUDED_FILE
