;; We know that when you write Babashka scripts,
;; you can forego declaring a namespace if all your code is in one file,
;; like in the original version of journal.
;; However, once you start splitting your code into multiple files,
;; the normal rules of Clojure project organization apply:
;;
;; - Namespace names must correspond to filesystem paths.
;;     If you want to name a namespace journal.add,
;;     Babashka must be able to find it at journal/add.clj.
;; - You must tell Babashka where to look to find the files that correspond to namespaces.
;;     You do this by creating a bb.edn file and putting {:paths ["src"]} in it.

(ns journal.add
  (:require
   [journal.utils :as utils]))

(defn add-entry
  ;; [{:keys [opts]}]
  [opts]
  (let [entries (utils/read-entries)]
    (spit utils/ENTRIES-LOCATION
          (conj entries
                (merge {:timestamp (System/currentTimeMillis)} ;; default timestamp
                       opts)))))

(defn -main [& args]
  (println (type (System/currentTimeMillis)))
  (println (type 11N))
  (println (type (+ (System/currentTimeMillis) 11N)))
  )

;; In Python scripts there is a well-known pattern to check
;; if the current file was the file invoked from the command line,
;; or loaded from another file:
;; the __name__ == "__main__" pattern.
;; In babashka this pattern can be implemented with:
(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
