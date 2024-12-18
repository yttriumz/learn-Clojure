;; Run it with bb, the babashka executable:
;;
;; bb 00_hello.clj
;;
;; You should see it print the text "Hello inner world!".
;; There are a few things here to point out for experienced Clojurians:
;; -  You didn’t need a deps.edn file or project.clj
;; -  There’s no namespace declaration; we use (require …​)
;; -  It’s just Clojure
(require '[clojure.string :as str])
(prn (str/join " " ["Hello" "inner" "world!"]))
;; Notice that the quotes are maintained when the value is printed.
;; bb will print the stringified representation of your data structure.
;; If you updated hello.clj to read
"Hello, inner world!"
(prn ["It's" "me," "your" "wacky" "subconscious!"])
;; Then ["It’s" "me," "your" "wacky" "subconscious!"] would get printed,
;; and "Hello, inner world!" would not.
;; You must use a printing function on a form for it to be sent to stdout
;; If you want to print a string without the surrounding quotes, you can use
(println "Hello, inner world!")
