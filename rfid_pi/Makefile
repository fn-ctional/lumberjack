warnings := -Wall -Wextra -pedantic
source := source/*.cpp
inc_dir := -I include/
headers := include/*.hpp
link := -lpthread $(shell curl-config --libs)

opt_msg := "unoptimised"
dbg_msg := ""

default: $(source) $(headers)
	@ mkdir -p bin
	@ echo -e "\e[00;36m[compiling - $(opt_msg)$(dbg_msg)]\e[0m"
	@ g++ -std=c++17 $(warnings) $(inc_dir) $(link) $(source) -o bin/scan $(debug) $(optimise)

debug: | debug_set default

optimise: | optimise_set default

clean:
	@ rm -rf bin/*

run: default
	@ echo -e "\e[00;36m[running]\e[0m"
	@ ./bin/scan || true

gdb: debug
	@ gdb bin/scan || true

debug_set:
	$(eval debug=-ggdb)
	$(eval dbg_msg=" + debug info")

optimise_set:
	$(eval optimise=-Os)
	$(eval opt_msg="optimised")
