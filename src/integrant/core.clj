(ns integrant.core
  (:refer-clojure :exclude [ref])
  (:require [com.stuartsierra.dependency :as dep]
            [clojure.walk :as walk]))

(defrecord Ref [key])

(defn ref [key]
  {:pre [(keyword? key) (namespace key)]}
  (->Ref key))

(defn ref? [x]
  (instance? Ref x))

(defn- find-refs [v]
  (cond
    (ref? v)  (list (:key v))
    (coll? v) (mapcat find-refs v)))

(defn dependencies [m]
  (reduce-kv (fn [g k v] (reduce #(dep/depend %1 k %2) g (find-refs v)))
             (dep/graph)
             m))