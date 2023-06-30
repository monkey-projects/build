# Monkey Build

Utility lib for building Clojure libs/apps using [Clojure CLI](https://clojure.org/reference/deps_and_cli).

I have provided this because although the CLI and related tools provide everything
I need, I found myself copy/pasting the same code over and over.  So I decided to
create this opinionated lib.  It does some assumptions regarding the way the code
is structured, which libs you use, etc...

## Usage

[![Clojars Project](https://img.shields.io/clojars/v/com.monkeyprojects/build.svg)](https://clojars.org/com.monkeyprojects/build)
[![CircleCI](https://circleci.com/gh/monkey-projects/build.svg?style=svg)](https://app.circleci.com/pipelines/github/monkey-projects/build)

Include the library in your `deps.edn` file, and then you can call it as a function,
using the `-X` parameter.  For example, to add an alias to run all unit tests using
Kaocha, add this:

```clojure
 :aliases
 {:test
  {:extra-deps {com.monkeyprojects/build {:mvn/version "0.1.0-SNAPSHOT"}}
   :exec-fn monkey.test/all}}
```
To run the tests, just execute:
```bash
clojure -X:test
```

## Available Commands

These commands are available to use in `exec-fn`:

- `monkey.test/all`: run all unit tests
- `monkey.test/watch`: run unit tests continuously and watch for changes
- `monkey.test/junit`: run all unit tests and output to `junit.xml`
- `monkey.build/jar`: build jar file
- `monkey.build/install`: install the jar locally
- `monkey.build/jar+install`: combines `jar` and `install`
- `monkey.build/deploy`: deploys to [Clojars](https://clojars.org)

Since this is just Clojure code, you are of course completely free to call
these functions from your own build code.  All they require is a single parameter,
containing the arguments passed in (see below).

### Parameters

In order to build a jar, or deploy, some extra `exec-args` are needed:

- `:jar`: the path to the jar file
- `:version`: the version to include in the `pom.xml`
- `:lib`: the name to deploy the library as (in case of deployment).
- `:scm` (optional): add additional SCM info to the pom file.

You can also customize the `junit.xml` output file by adding an `:output` parameter.
The `test` functions essentially just call the [Kaocha](https://github.com/lambdaisland/kaocha)
code, so if you want more customization, either specify it in the `tests.edn` file, or
call the functions directly from your own build code.  As I said, this is an opinionated
lib, mostly created for my own purposes.