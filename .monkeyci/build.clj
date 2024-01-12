(require '[monkey.ci.plugin.clj :as p])

(p/deps-library {:publish-alias ":jar:deploy"
                 :version-var "VERSION"})
