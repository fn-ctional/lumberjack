#ifndef INCLUDED_RESULT
#define INCLUDED_RESULT

#include <stdexcept>
#include <utility>
#include <variant>

template<typename O, typename E>
class [[nodiscard]] Result {
public:
  Result(O &&o) : variant( std::move(o) ) {};
  Result(E &&e) : variant( std::move(e) ) {};

  template<typename... Args>
  static Result<O,E> Err(Args&&... args) {
    return Result<O,E>(
      E( args... )
    );
  }

  template<typename... Args>
  static Result<O,E> Ok(Args&&... args) {
    return Result<O,E>(
      O( args... )
    );
  }

  bool is_err() const {
    return std::holds_alternative<E>( variant );
  };

  bool is_ok() const {
    return std::holds_alternative<O>( variant );
  };

  E& get_err() {
    auto ptr = std::get_if<E>( &variant );
    if ( ptr == nullptr ) {
      throw std::logic_error("Attempted to get an error from a Result containing an ok");
    }
    return *ptr;
  };

  O& get_ok() {
    auto ptr = std::get_if<O>( &variant );
    if ( ptr == nullptr ) {
      throw std::logic_error("Attempted to get an ok from a Result containing an error");
    }
    return *ptr;
  };

  O&& expect(std::string msg) {
    auto ptr = std::get_if<O>( &variant );
    if ( ptr == nullptr ) {
      throw std::logic_error(msg);
    }
    return std::move( *ptr );
  };

  O&& unwrap() {
    return std::move( *std::get_if<O>( &variant ) );
  }

  template<typename Fn>
  O&& unwrap_or(Fn fn) {
    auto ptr = std::get_if<O>( &variant );

    if ( ptr == nullptr ) {
      return fn( *std::get_if<E>( &variant ) );
    }

    return std::move( *ptr );

  }
private:
  std::variant<O,E> variant;
};

#endif // INCLUDED_RESULT
