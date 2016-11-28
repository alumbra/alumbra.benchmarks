(ns alumbra.benchmarks.utils
  (:require [perforate.core :refer [defgoal defcase]]
            [alumbra.parser :as ql]
            [alumbra.analyzer :as a]
            [alumbra.validator :as v]))

;; ## Helpers

(defmacro defgoal+
  [id docstring function-under-test]
  `(do
     (defgoal ~id ~docstring)
     (alter-meta! (var ~id) assoc ::function ~function-under-test)))

(defmacro defcase+
  [goal k query & params]
  `(let [ast# (ql/parse-document ~query)
         f# (-> (var ~goal) meta ::function)]
     (assert (not (:alumbra/parser-errors ast#))
             (format "parsing failed in case '%s': %s"
                     ~(str k)
                     ast#))
     (defcase ~goal ~k
       []
       (f# ast# ~@params))))

(defmacro defcases+
  [goal & body]
  (let [groups (->> (partition-by keyword? body)
                    (partition 2))]
    `(do
       ~@(for [[[k] rst] groups]
           `(defcase+ ~goal ~k ~@rst)))))

;; ## Base Schema

(def schema
  (a/analyze-schema
    "interface Pet { name: String!, owner: Person! }
     type Cat implements Pet { name: String!, meowVolume: Int! }
     type Dog implements Pet { name: String!, barkVolume: Int! }
     type Person { id: ID!, name: String!, age: Int!, pets: [Pet!] }
     type QueryRoot { me: Person!, person(id: ID!): Person }
     schema { query: QueryRoot }"
    ql/parse-schema))

;; ## Base Cases

(defmacro defcases-common
  [goal]
  `(defcases+ ~goal
     :simple-query
     "{ me { name, age } }"

     :simple-query-with-name
     "query Q { me { name, age } }"
     "Q"

     :simple-query-with-named-spread
     "{ me { ... PersonInfo } }
      fragment PersonInfo on Person { name, age }"

     :simple-query-with-inline-spread
     "{ me { ... on Person { name age } } }"

     :simple-query-with-chained-fragments
     "{ me { ... PersonInfo } }
      fragment PersonInfo on Person { ... PersonName, ...PersonAge }
      fragment PersonName on Person { name }
      fragment PersonAge on Person { age }"

     :nested-query
     "{ me { name, age, pets { name, owner { name, age } } } }"

     :nested-query-with-named-spread
     "{ me { ... PersonInfo, pets { name, owner { ... PersonInfo } } } }
      fragment PersonInfo on Person { name, age }"

     :nested-query-with-chained-fragments
     "{ me { ... PersonInfo, pets { name, owner { ... PersonInfo } } } }
      fragment PersonInfo on Person { ... PersonName, ...PersonAge }
      fragment PersonName on Person { name }
      fragment PersonAge on Person { age }"

     :parameterized-query
     "{ person(id: \"10\") { name, age } }"

     :parameterized-query-with-name
     "query Q { person(id: \"10\") { name, age } }"
     "Q"

     :parameterized-query-with-variable
     "query Q($id: ID!) { person(id: $id) { name, age } }"
     "Q"
     {"id" "10"}))
