(ns journal.utils
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]))

;; Note that this path is relative to the main script file.
(def ENTRIES-LOCATION "data/entries.edn")

(defn read-entries
  []
  (if (fs/exists? ENTRIES-LOCATION)
    (edn/read-string (slurp ENTRIES-LOCATION))
    []))
