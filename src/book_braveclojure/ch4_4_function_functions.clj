(ns ch4-4-function-functions)

;; ================================
;; apply
;; ================================

;; apply explodes a seqable data structure
;; so it can be passed to a function that expects a rest parameter.
;; For example, max takes any number of arguments
;; and returns the greatest of all the arguments.
;; Here’s how you’d find the greatest number:
(max 1 2 3)
;; But what if you want to find the greatest element of a vector?
;; You can’t just pass the vector to max:
;;
;; (max [1 2 3])
;;
;; This doesn’t return the greatest element in the vector
;; because max returns the greatest of all the arguments passed to it,
;; and in this case you’re only passing it a vector containing all the numbers you want to compare,
;; rather than passing in the numbers as separate arguments.
;; apply is perfect for this situation:
(apply max [1 2 3])

;; Remember how we defined conj in terms of into earlier?
;; Well, we can also define into in terms of conj by using apply:
(defn my-into
  [target additions]
  (apply conj target additions))

(my-into [0] [1 2 3])
;; This call to my-into is equivalent to calling:
(conj [0] 1 2 3)

;; ================================
;; partial
;; ================================

;; partial takes a function and any number of arguments.
;; It then returns a new function.
;; When you call the returned function,
;; it calls the original function with the original arguments you supplied it
;; along with the new arguments.
(def add10 (partial + 10))
(add10 3)
(add10 5)

(def add-missing-elements
  (partial conj ["water" "earth" "air"]))
(add-missing-elements "unobtainium" "adamantium")

;; To help clarify how partial works, here’s how you might define it:
(defn my-partial
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

(def add20 (my-partial + 20))
(add20 3)

;; In general, you want to use partials
;; when you find you’re repeating the same combination of function and arguments
;; in many different contexts.
;; This toy example shows how you could use partial to specialize a logger,
;; creating a warn function:
(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

(def warn (partial lousy-logger :warn))
(warn "Red light ahead")

;; ================================
;; complement
;; ================================

;; Here’s how you might implement complement:
(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))

(def my-pos? (my-complement neg?))
(my-pos? 1)
(my-pos? -1)

;; As you can see, complement is a humble function.
;; It does one little thing and does it well.
