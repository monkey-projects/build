(ns monkey.test.build-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [clojure.xml :as xml]
            [monkey.build :as sut]))

(def test-version (constantly "test-version"))

(deftest determine-version
  (testing "returns `version` param"
    (is (= "test-version" (sut/determine-version {:version "test-version"}))))

  (testing "returns value from env"
    (is (string? (sut/determine-version {:version-env "PATH"}))))

  (testing "returns value from fn"
    (is (= "test-version" (sut/determine-version {:version-fn 'monkey.test.build-test/test-version})))))

(defn- with-new-pom* [f]
  (let [pom "pom.xml"
        existing (when (fs/exists? pom)
                   (slurp pom))]
    (try
      ;; Delete any pre-existing pom.xml otherwise it will be used
      (fs/delete-if-exists pom)
      (f)
      (finally
        (when existing
          (spit pom existing))))))

(defmacro with-new-pom [& body]
  `(with-new-pom* (fn [] ~@body)))

(deftest pom
  (with-new-pom
    (testing "writes `pom.xml` to target dir"
      (is (nil? (sut/pom {:lib "com.monkeyprojects/testlib"})))
      (is (fs/exists? "target/pom.xml"))
      (let [data (xml/parse "target/pom.xml")]
        (is (= "testlib"
               (->> data
                    :content
                    (filter (comp (partial = :artifactId) :tag))
                    (first)
                    :content
                    (first))))))

    (testing "copies to root dir"
      (is (fs/exists? "pom.xml"))))

  (with-new-pom
    (testing "includes additional `pom-data`"
      (is (nil? (sut/pom {:lib "com.monkeyprojects/testlib"
                          :pom-data [[:licenses
                                      [:license
                                       [:name "test license"]
                                       [:url "https://test-license"]]]
                                     [:organization "Test org"]]})))
      (let [data (xml/parse "target/pom.xml")]
        (is (= "test license"
               (->> data
                    :content
                    (filter (comp (partial = :licenses) :tag))
                    (first)
                    :content
                    (first)
                    :content
                    (filter (comp (partial = :name) :tag))
                    (first)
                    :content
                    (first))))
        (is (= "Test org"
               (->> data
                    :content
                    (filter (comp (partial = :organization) :tag))
                    (first)
                    :content
                    (first))))))))
