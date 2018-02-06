warnings := -Wall -Wextra -pedantic
source := source/*.cpp
inc_dir := -I include/
headers := include/*.hpp

default: $(source) $(headers)
	@ mkdir -p bin
	@ g++ -std=c++14 $(warnings) $(inc_dir) $(source) -o bin/scan $(debug) $(optimise)

debug: | debug_set default

optimise: | optimise_set default

clean:
	@ rm -rf bin/*

run: default
	@ ./bin/scan || true

gdb: debug
	@ gdb bin/scan || true

debug_set:
	$(eval debug=-ggdb)

optimise_set:
	$(eval optimise=-Os)
