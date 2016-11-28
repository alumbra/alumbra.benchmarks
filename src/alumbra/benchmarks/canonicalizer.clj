(ns alumbra.benchmarks.canonicalizer
  (:require [alumbra.benchmarks.utils :as u]
            [alumbra.analyzer :as a]))

;; ## Goal

(u/defgoal+ canonicalizer
  "Performance when validating operations."
  (partial a/canonicalize-operation u/schema))

;; ## Cases

(u/defcases-common canonicalizer)
