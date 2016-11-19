(ns alumbra.benchmarks.canonicalizer
  (:require [perforate.core :refer [defgoal defcase]]
            [alumbra.parser :as ql]
            [alumbra.analyzer :as a]))

(def schema
  (a/analyze-schema
    "interface Pet { name: String!, owner: Person! }
     type Cat implements Pet { name: String!, meowVolume: Int! }
     type Dog implements Pet { name: String!, barkVolume: Int! }
     type Person { id: ID!, name: String!, age: Int!, pets: [Pet!] }
     type QueryRoot { me: Person!, person(id: ID!): Person }
     schema { query: QueryRoot }"
    ql/parse-schema))

(def canonicalize
  (partial a/canonicalize-operation schema))

(defgoal canonicalizer
  "Performance when canonicalizing operations.")

(defmacro defcase-canonicalizer
  [k query & params]
  `(let [ast# (ql/parse-document ~query)]
     (defcase canonicalizer ~k
       []
       (canonicalize ast# ~@params))))

(defcase-canonicalizer :simple-query
  "{ me { name, age } }")

(defcase-canonicalizer :simple-query-with-name
  "query Q { me { name, age } }"
  "Q")

(defcase-canonicalizer :simple-query-with-named-spread
  "{ me { ... PersonInfo } }
   fragment PersonInfo on Person { name, age }")

(defcase-canonicalizer :simple-query-with-inline-spread
  "{ me { ... on Person { name age } } }")

(defcase-canonicalizer :simple-query-with-chained-fragments
  "{ me { ... PersonInfo } }
   fragment PersonInfo on Person { ... PersonName, ...PersonAge }
   fragment PersonName on Person { name }
   fragment PersonAge on Person { age }")

(defcase-canonicalizer :nested-query
  "{ me { name, age, pets { name, owner { name, age } } } }")

(defcase-canonicalizer :nested-query-with-named-spread
  "{ me { ... PersonInfo, pets { name, owner { ... PersonInfo } } } }
   fragment PersonInfo { name, age }")

(defcase-canonicalizer :nested-query-with-chained-fragments
  "{ me { ... PersonInfo, pets { name, owner { ... PersonInfo } } } }
   fragment PersonInfo on Person { ... PersonName, ...PersonAge }
   fragment PersonName on Person { name }
   fragment PersonAge on Person { age }")

(defcase-canonicalizer :parameterized-query
  "{ person(id: \"10\") { name, age } }")

(defcase-canonicalizer :parameterized-query-with-name
  "query Q { person(id: \"10\") { name, age } }"
  "Q")

(defcase-canonicalizer :parameterized-query-with-variable
  "query Q($id: ID!) { person(id: $id) { name, age } }"
  "Q"
  {"id" "10"})
