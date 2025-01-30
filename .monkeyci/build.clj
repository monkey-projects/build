(ns build
  (:require [monkey.ci.plugin
             [clj :as p]
             [github :as gh]]))

[(p/deps-library {:publish-alias ":jar:deploy"
                  :version-var "VERSION"})
 (gh/release-job {:dependencies ["publish"]})]
