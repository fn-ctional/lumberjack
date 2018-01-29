.PHONY: default debug optimise

warnings=-Wall -Wextra -pedantic
source=*.cpp

default:
  g++ -std=c++14 $(warnings) $(source) -o scan $(debug) $(optimise)

debug:
	$(eval debug=-ggdb)
	default

optimise:
	$(eval optimise=-O3)
	default
