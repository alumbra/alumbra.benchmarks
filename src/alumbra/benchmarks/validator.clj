(ns alumbra.benchmarks.validator
  (:require [alumbra.benchmarks.utils :as u]
            [alumbra.validator :as v]))

;; ## Goal

(u/defgoal+ validator
  "Performance when validating operations."
  (v/validator u/schema))

;; ## Cases

(u/defcases-common validator)
