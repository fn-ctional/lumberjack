#ifndef INCLUDED_CHANNEL
#define INCLUDED_CHANNEL

// This module defines three main classes: Channel, Read, and Write. These can be used for one-way communication
// between threads. A Channel object must first be created and then the get_read and get_write methods can be used
// to create Read and Write objects respectively. The Channel object must live for as long as the Read and Write
// objects are in use.
//
// Example of usage:
// > auto channel = Channel<int>();
// > auto read = channel.get_read();
// > auto write = channel.get_write();
// > write.write(42);
// > int x;
// > read.read(x);
// > assert( 42 == x);

namespace Channel {

  // Flags to indicate whether or not data can be read or written to.
  enum class State { Read, Write };

  // This class stores the data that is sent and recieved through the channel.
  // The state indicates whether the data can be read or written to.
  // This defaults to Write as no data can be read before writing to it.
  template<class T>
  struct Data {
    State state = State::Write;
    T data;
  };

  // This forward declaration is necessary as Channel needs to be able to construct Write and Read
  //  objects so must be defined afterwards but needs to be made a friend of these classes first.
  template<class T>
  class Channel;


  // This class allows writing to a channel shared with a Read object.
  // This must not outlive the Channel that created it.
  // Channel<T> is a friend so that it can access the private constructor.
  //
  // Constructor (private):
  //  inputs: Data<T> *data - Pointer to the shared data
  //
  // Move Constructor:
  //  inputs: Write<T> &&w - The Write object being moved from
  //
  // try_write:
  //  This method attempts to write the data given into the channel.
  //  If the state flag is Write, the data is written and the flag set to Read, if not, no changes are made.
  //  return: bool - true if data was written, false if not
  //  inputs: T t - The data being written
  //
  // write:
  //  This method waits until data can be written to write it, it repeatedly calls try_write.
  //  inputs: T t - The data being written
  template<class T>
  class Write {
  public:
    Write(Write<T> &&w): data(w.data) {};

    bool try_write(T t) {
      if ( data->state != State::Write ) return false;
      data->data = t;
      data->state = State::Read;
      return true;
    };

    void write(T t) {
      while ( !try_write(t) ) {}
    };

  private:
    Write(Data<T> *data): data(data) {};
    Data<T> *data;
    friend Channel<T>;
  };


  // This class allows reading from a channel shared with a Write object.
  // This must not outlive the Channel that created it.
  // Channel<T> is a friend so that it can access the private constructor.
  //
  // Constructor (private):
  //  inputs: Data<T> *data - Pointer to the shared data
  //
  // Move Constructor:
  //  inputs: Read<T> &&r - The Read object being moved from
  //
  // try_read:
  //  This method attempts to read the data from the channel.
  //  If the state flag is Read, the data is read into t and the flag set to Write, if not, no changes are made.
  //  return: bool - true if data was read, false if not
  //  inputs: T &t - A reference to the object into which the data is read
  //
  // read:
  //  This method waits until data can be read to read from it, it repeatedly calls try_read.
  //  inputs: T &t - A reference to the object into which the data is read
  template<class T>
  class Read {
  public:
    Read(Read<T> &&r): data(r.data) {};

    bool try_read(T &t) {
      if ( data->state != State::Read ) return false;
      t = data->data;
      data->state = State::Write;
      return true;
    };

    void read(T &t) {
      while ( !try_read(t) ) {}
    };

  private:
    Read(Data<T> *data): data(data) {};
    Data<T> *data;
    friend Channel<T>;
  };


  // This class manages the Data object used by a Read and Write.
  // The Channel object must outlive the both of the Read and Write objects that it creates.
  // If more than one of either the Read or Write objects are created, the program will exit.
  //   (This should be changed to something more reasonable)
  //
  // The read_exists and write_exists members keep track of whether or not these objects have been created.
  //  Having multiple Write objects could result in them writing over each other.
  //
  // Constructor:
  //  A new Data<T> is created and read_exists and write_exists are set to false.
  //
  // Copy Constructor (deleted):
  //  This is explicitly deleted to prevent multiple frees of the data
  //
  // Move Constructor:
  //  This sets c.data to nullptr to prevent it freeing it in it's destructor
  //  inputs: Channel &&c - The Channel being moved from
  //
  // Destructor:
  //  Frees the data
  //
  // get_read:
  //  This method quits the program if it is called multiple times
  //  return: Read<T> - A Read object with the data constructed by this object
  //
  // get_write:
  //  This method quits the program if it is called multiple times
  //  return: Write<T> - A Write object with the data constructed by this object
  template<class T>
  class Channel {
  public:
    Channel()
      : data(new Data<T>())
      , read_exists(false)
      , write_exists(false) {
    };

    Channel(Channel&) = delete;

    Channel(Channel &&c)
      : data(c.data)
      , read_exists(c.read_exists)
      , write_exists(c.write_exists) {
      c.data = nullptr;
    };

    ~Channel() {
      delete data;
    };

    Read<T> get_read() {
      if ( read_exists ) exit(-1);
      return Read<T>(data);
    };

    Write<T> get_write() {
      if ( write_exists ) exit(-1);
      return Write<T>(data);
    };

  private:
    Data<T> *data;
    bool read_exists;
    bool write_exists;
  };

}

#endif // INCLUDED_CHANNEL
