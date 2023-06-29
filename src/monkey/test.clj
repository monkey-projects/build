(ns monkey.test
  "For running unit tests using Kaocha"
  (:require [kaocha
             [repl :as k]
             [watch :as w]]))

(defn all
  "Run all unit tests"
  [_]
  (k/run-all))

(defn watch
  "Watches unit tests, for TDD"
  [_]
  (w/run (k/config)))

(defn junit
  "Runs unit tests once, outputs to junit file"
  [{:keys [output] :or {output "junit.xml"}}]
  (println "Outputting test results to" output)
  (k/run-all {:kaocha/plugins [:kaocha.plugin/junit-xml]
              :kaocha/cli-options {:junit-xml-file output}}))
