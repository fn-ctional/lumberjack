#ifndef INCLUDED_RESULT
#define INCLUDED_RESULT

// The Result class is used to indicate that a function may fail and, instead of raising an exception, can return an
// object containing information regarding the error. This information could be an enum of possible errors or a string
// containing an error message, for example.
//
// This class is marked [[nodiscard]] so that the callee of a function returning a Result must check for errors.
//
// Examples of usage:
//
// TODO: The examples section is incomplete

#include <stdexcept>
#include <utility>
#include <variant>


// This class is a wrapper around a std::variant, limited to only two types. The first type, O or 'Ok', is the type of
// the object returned on success. The second type, E or 'Err', is the type of the object returned on error.
//
// Constructor [ Ok type ]:
//  inputs: O &&o - The Ok object being moved into the Result
//
// Constructor [ Err type ]:
//  inputs: E &&e - The Err object being moved into the Result
//
// Err (static) <typename... Args> :
//  This method is used to construct an Err object
//  inputs: Args&&... args - Arguments to be passed to the constructor of the Err object
//  return: Result<O,E>    - A Result containing the newly constructed Err object
//
// Ok (static) <typename... Args> :
//  This method is used to construct an Ok object
//  inputs: Args&&... args - Arguments to be passed to the constructor of the Ok object
//  return: Result<O,E>    - A Result containing the newly constructed Ok object
//
// is_err:
//  return: bool - true if the object contains an Err object, false otherwise
//
// is_ok:
//  return: bool - true if the object contains an Ok object, false otherwise
//
// get_err:
//  inputs: const std::string &msg  [optional] - Message to put in the throw
//  return: E&&                                - The xvalue referring to the Err object
//  throws: std::logic_error                   - Thrown if the object does not contain an Err object
//
// get_ok:
//  inputs: const std::string &msg  [optional] - Message to put in the throw
//  return: O&&                                - The xvalue referring to the Ok object
//  throws: std::logic_error                   - Thrown if the object does not contain an Ok object
//
// map_err <typename Fn>:
//  Applies a function to the Err object, if one exists, returning a new Result
//  inputs: Fn fn           - A function that converts an Err to a different error type, Err'
//  return: Result<Ok,Err'> - A new Result that contains either the new Err' or a moved Ok
//
// map_ok <typename Fn>:
//  Applies a function to the Ok object, if one exists, returning a new Result
//  inputs: Fn fn           - A function that converts an Ok to a different error type, Ok'
//  return: Result<Ok',Err> - A new Result that contains either the new Ok' or a moved Err
template<typename O, typename E>
class [[nodiscard]] Result {
public:
  Result(O &&o) : variant( std::move(o) ) {};
  Result(E &&e) : variant( std::move(e) ) {};

  // TODO: Emplace instead of moving
  template<typename... Args>
  static Result<O,E> Err(Args&&... args) {
    return Result<O,E>(
      E( args... )
    );
  }

  // TODO: Emplace instead of moving
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

  E&& get_err( const std::string &msg = "Attempted to get an error from a Result containing an ok" ) {
    auto ptr = std::get_if<E>( &variant );
    if ( ptr == nullptr ) {
      throw std::logic_error( msg );
    }
    return std::move( *ptr );
  };

  O&& get_ok( const std::string &msg = "Attempted to get an ok from a Result containing an error" ) {
    auto ptr = std::get_if<O>( &variant );
    if ( ptr == nullptr ) {
      throw std::logic_error( msg );
    }
    return std::move( *ptr );
  };

  template<typename Fn>
  auto map_err( Fn fn ) {
    typedef Result<O,std::remove_reference_t<std::invoke_result_t<Fn,E>>> NewResult;
    if ( is_ok() ) {
      return NewResult::Ok( get_ok() );
    } else {
      return NewResult::Err( fn( get_err() ) );
    }
  }

  template<typename Fn>
  auto map_ok( Fn fn ) {
    typedef Result<std::remove_reference_t<std::invoke_result_t<Fn,O>>,E> NewResult;
    if ( is_ok() ) {
      return NewResult::Ok( fn( get_ok() ) );
    } else {
      return NewResult::Err( get_err() );
    }
  }
private:
  std::variant<O,E> variant;
};

#endif // INCLUDED_RESULT
