(ns monkey.test.test-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [kaocha
             [config :as kc]
             [result :as kr]]
            [monkey.test :as sut]))

(deftest passed?
  (testing "true if all passed"
    (is (true? (sut/passed? #::kr{:count 1 :passed 1 :error 0 :fail 0 :pending 0}))))

  (testing "false on error"
    (is (false? (sut/passed? #::kr{:count 1 :passed 0 :error 1 :fail 0 :pending 0}))))

  (testing "false on failure"
    (is (false? (sut/passed? #::kr{:count 1 :passed 0 :error 0 :fail 1 :pending 0})))))

(def test-config
  (-> (kc/default-config)
      (update :kaocha/tests
              (comp vector
                    (fn [c]
                      ;; Avoid stack overflow, exclude the :skip-recur tests
                      (assoc c :kaocha.filter/skip-meta [:kaocha/skip :skip-recur]))
                    first))))

(deftest ^:skip-recur junit
  (with-redefs-fn {#'sut/to-exit identity}
    #(testing "writes results to output file"
      (let [out (str (System/getProperty "java.io.tmpdir") "/junit.xml")]
        (is (map? (sut/junit
                   {:output out
                    :config test-config})))
        (is (true? (.exists (io/file out))))))))
