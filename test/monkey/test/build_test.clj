(ns monkey.test.build-test
  (:require [clojure.test :refer [deftest testing is]]
            [monkey.build :as sut]))

(def test-version (constantly "test-version"))

(deftest determine-version
  (testing "returns `version` param"
    (is (= "test-version" (sut/determine-version {:version "test-version"}))))

  (testing "returns value from env"
    (is (string? (sut/determine-version {:version-env "PATH"}))))

  (testing "returns value from fn"
    (is (= "test-version" (sut/determine-version {:version-fn 'monkey.test.build-test/test-version})))))

