#ifndef INCLUDED_CHANNEL
#define INCLUDED_CHANNEL

namespace Channel {

  enum class State { Read, Write };

  template<class T>
  struct Data {
    Data() : state(State::Write) {};
    State state;
    T data;
  };

  template<class T>
  class Channel;

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
    volatile Data<T> *data;
    friend Channel<T>;
  };

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
    volatile Data<T> *data;
    friend Channel<T>;
  };

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
      if ( read_exists ) exit(-1); // TODO: Fix???
      return Read<T>(data);
    };

    Write<T> get_write() {
      if ( write_exists ) exit(-1); // TODO: Fix???
      return Write<T>(data);
    };

  private:
    Data<T> *data;
    bool read_exists;
    bool write_exists;
  };

}

#endif // INCLUDED_CHANNEL
