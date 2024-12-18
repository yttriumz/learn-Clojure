#!/usr/bin/env bb

;; Shell scripts often need to read input from the command line
;; and produce output somewhere,
;; and our dream journal utility is no exception.
;; It’s going to store entries in the file entries.edn.
;; The journal will be a vector,
;; and each entry will be a map with the keys :timestamp and :entry
;; (the entry has linebreaks for readability):
;;
;; [{:timestamp 0
;;   :entry     "Dreamt the drain was clogged again, except when I went to unclog
;;               it it kept growing and getting more clogged and eventually it
;;               swallowed up my little unclogger thing"}
;;  {:timestamp 1
;;   :entry     "Bought a house in my dream, was giving a tour of the backyard and
;;               all the... topiary? came alive and I had to fight it with a sword.
;;               I understood that this happens every night was very annoyed that
;;               this was not disclosed in the listing."}]
;;
;; To write to the journal, we want to run the command
;; ./journal add --entry "Hamsters. Hamsters everywhere.
;; Again.". The result should be that a map gets appended to the vector.

(require '[babashka.fs :as fs])

(def ENTRIES-LOCATION "data/entries.edn")

(defn read-entries
  []
  (if (fs/exists? ENTRIES-LOCATION)
    (edn/read-string (slurp ENTRIES-LOCATION))
    []))

(defn add-entry
  [text]
  (let [entries (read-entries)]
    ;; By default, spit will overwrite a file;
    ;; if you want to append to it, you would call it like
    ;;   (spit "entries.edn" {:timestamp 0 :entry ""} :append true)
    ;; Maybe overwriting the whole file is a little dirty, but that’s the scripting life babyyyyy!
    (spit ENTRIES-LOCATION
          (conj entries {:timestamp (System/currentTimeMillis)
                         :entry     text}))))

;; *command-line-args* is a sequence
;; containing all the command line arguments that were passed to the script.
(add-entry (first *command-line-args*))