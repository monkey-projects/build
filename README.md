# Monkey Build

Utility lib for building Clojure libs/apps using [Clojure CLI](https://clojure.org/reference/deps_and_cli).

I have provided this because although the CLI and related tools provide everything
I need, I found myself copy/pasting the same code over and over.  So I decided to
create this opinionated lib.  It does some assumptions regarding the way the code
is structured, which libs you use, etc...

## Usage

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

These commands are available:

- `monkey.test/all`: run all unit tests
- `monkey.test/watch`: run unit tests continuously and watch for changes
- `monkey.test/junit`: run all unit tests and output to `junit.xml`
- `monkey.build/jar`: build jar file
- `monkey.build/install`: install the jar locally
- `monkey.build/jar+install`: combines `jar` and `install`
- `monkey.build/deploy`: deploys to [Clojars](https://clojars.org)

### Parameters

In order to build a jar, or deploy, some extra `exec-args` are needed:

- `:jar`: the path to the jar file
- `:version`: the version to include in the `pom.xml`
- `:lib`: the name to deploy the library as (in case of deployment).

