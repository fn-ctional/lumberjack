#include "Events.hpp"
#include <iostream>

int main() {
  Events events("/dev/input/event0");
  for (auto &event : events) {
    if (event.type == 1 && event.value == 1) {
      std::cout << event.code << std::flush;
    }
  }
}
