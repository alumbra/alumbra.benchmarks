(ns alumbra.benchmarks.document-parser
  (:require [perforate.core :refer [defgoal defcase]]
            [alumbra.parser :as ql]))

(defgoal document-parser
  "Performance when parsing different kinds of GraphQL queries.")

(defcase document-parser :simple-query
  []
  (ql/parse-document "{ a { b { c,d } } }"))

(defcase document-parser :simple-query-with-explicit-operation
  []
  (ql/parse-document "query { a { b { c,d } } }"))

(defcase document-parser :simple-query-with-explicit-name
  []
  (ql/parse-document "query Q { a { b { c,d } } }"))

(defcase document-parser :query-with-fragment-spread
  []
  (ql/parse-document "{ a { b { ... fr } } } fragment fr on B { c,d }"))

(defcase document-parser :query-with-inline-spread
  []
  (ql/parse-document "{ a { b { ... on B { c,d } } } }"))

(defcase document-parser :query-with-parameterized-fields
  []
  (ql/parse-document "{ a { b(id: 10) { c,d } } }"))

(defcase document-parser :query-with-parameterized-fields-and-variables
  []
  (ql/parse-document "query Q($id: ID) { a { b(id: $id) { c,d } } }"))

(def complex-query
  "{
     newestUsers { name, image },
     topUser: firstUser (sort: \"rank\", order: \"desc\") {
       name,
       projects {
         __type,
         name,
         ...Spreadsheet,
         ...Painting
       }
     }
   }

   fragment Spreadsheet on SpreadsheetProject {
     rowCount,
     columnCount
   }

   fragment Painting on PaintingProject {
     dominantColor { name, hexCode }
   }
   ")

(defcase document-parser :complex-query
  []
  (ql/parse-document complex-query))
