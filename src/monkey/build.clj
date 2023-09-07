(ns monkey.build
  "Opinionated functions for building Monkey Projects libraries"
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def target "target")
(def class-dir (str target "/classes"))

(defn- copy-sources [basis]
  (println "Copying sources from:" (:paths basis))
  (b/copy-dir {:src-dirs (:paths basis)
               :target-dir class-dir}))

(defn jar
  "Builds a JAR file"
  [{:keys [jar main]}]
  (let [basis (b/create-basis)]
    (copy-sources basis)
    (println "Building a jar to" jar)
    (b/jar {:class-dir class-dir
            :jar-file jar
            :main main})
    (println "Done.")))

(defn- read-env [v]
  (some-> v
          (System/getenv)))

(defn- maybe-invoke [s]
  (when-let [v (some-> s (find-var))]
    (v)))

;; Determines the value of the version from the args.  This checks the
;; `version`, `version-env` and `version-fn` params.
(def determine-version
  (some-fn :version (comp read-env :version-env) (comp maybe-invoke :version-fn)))

(defn env-or-default []
  (or (System/getenv "VERSION") "0.1.0-SNAPSHOT"))

(defn install
  "Installs JAR locally"
  [{:keys [jar lib] :as args}]
  (let [basis (b/create-basis)
        version (determine-version args)
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
  [{:keys [lib scm] :as args}]
  (let [basis (b/create-basis)
        opts {:basis basis
              :version (determine-version args)
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

(defn uberjar
  "Creates an uberjar"
  [{:keys [jar main]}]
  (let [basis (b/create-basis)
        opts {:uber-file jar
              :basis basis
              :class-dir class-dir
              :main (when main (symbol main))}]
    (copy-sources basis)
    (println "Compiling...")
    (b/compile-clj {:basis basis
                    :src-dirs (:paths basis)
                    :class-dir class-dir
                    :compile-opts {:direct-linking true}})
    (println "Creating uberjar" jar)
    (b/uber opts)
    (println "Done.")))
