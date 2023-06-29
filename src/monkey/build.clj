(ns monkey.build
  "Opinionated functions for building Monkey Projects libraries"
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

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

(defn pom
  "Generates pom.xml file, with the given version"
  [{:keys [version lib scm]}]
  (let [basis (b/create-basis)
        opts {:basis basis
              :version version
              :lib (symbol lib)
              :target target
              :scm scm}
        pom "pom.xml"]
    (println "Writing" pom)
    (b/write-pom opts)
    (b/copy-file {:src (str target "/" pom)
                  :target pom})))

(defn deploy
  "Deploys the library to an external repo. If no repository is given,
   it uses Clojars.  Be sure to set `CLOJARS_USERNAME` and `CLOJARS_PASSWORD`
   in that case."
  [{:keys [jar repository] :as opts}]
  (pom opts)
  (println "Deploying" jar "to" (or repository "clojars"))
  (dd/deploy (merge {:installer :remote
                     :artifact jar}
                    opts)))
