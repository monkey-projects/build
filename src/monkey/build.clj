(ns monkey.build
  "Opinionated functions for building Monkey Projects libraries"
  (:require [clojure.tools.build.api :as b]))

(def target "target")
(def class-dir (str target "/classes"))

(defn jar
  "Builds a JAR file"
  [{:keys [jar main]}]
  (let [basis (b/create-basis)]
    (println "Copying sources from:" (:paths basis))
    (b/copy-dir {:src-dirs (:paths basis)
                 :target-dir class-dir})
    (println "Building a jar to" jar)
    (b/jar {:class-dir class-dir
            :jar-file jar
            :main main})
    (println "Done.")))

(defn install
  "Installs JAR locally"
  [{:keys [jar version lib]}]
  (let [basis (b/create-basis)
        opts {:basis basis
              :class-dir class-dir
              :version version
              :lib (symbol lib)}]
    (println "Writing pom.xml for version" version)
    (b/write-pom (assoc opts :src-dirs (:paths basis)))
    (println "Installing" jar "to local mvn repo")
    (b/install (assoc opts :jar-file jar))
    (println "Done.")))

(defn jar+install [args]
  (jar args)
  (install args))
