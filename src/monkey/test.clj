(ns monkey.test
  "For running unit tests using Kaocha"
  (:require [kaocha
             [config :as kc]
             [result :as kr]
             [repl :as k]
             [watch :as w]]))

(defn passed?
  "True if the kaocha test result is successful"
  [r]
  (not (kr/failed? r)))

(defn to-exit
  "Exits the VM with exit code 0 on success, 1 on failure."
  [r]
  (System/exit (if (passed? r) 0 1)))

(defn all
  "Run all unit tests"
  [_]
  (to-exit
   (k/run-all)))

(defn watch
  "Watches unit tests, for TDD"
  [_]
  (-> (w/run (k/config))
      (first)
      (deref)))

(defn junit
  "Runs unit tests once, outputs to junit file"
  [{:keys [output config] :or {output "junit.xml"}}]
  (println "Outputting test results to" output)
  (to-exit
   (k/run-all (merge config
                     {:kaocha/plugins [:kaocha.plugin/junit-xml]
                      :kaocha/cli-options {:junit-xml-file output}}))))
