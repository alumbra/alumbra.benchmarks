(defproject alumbra.benchmarks "0.1.0-SNAPSHOT"
  :description "Benchmarks for the alumbra GraphQL infrastructure."
  :url "https://github.com/alumbra/alumbra.benchmarks"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2016
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [alumbra/parser "0.1.5"]
                 [alumbra/validator "0.1.0"]
                 [alumbra/analyzer "0.1.7"]
                 [perforate "0.3.4"]]
  :plugins [[perforate "0.3.4"]]
  :perforate
  {:benchmark-paths ["src"]
   :environments
   [{:name :document-parser
     :namespaces [alumbra.benchmarks.document-parser]}
    {:name :canonicalizer
     :namespaces [alumbra.benchmarks.canonicalizer]}
    {:name :validator
     :namespaces [alumbra.benchmarks.validator]}]}
  :jvm-opts ^:replace ["-server" "-Xmx1g" "-Xms1g"]
  :pedantic? :abort)
