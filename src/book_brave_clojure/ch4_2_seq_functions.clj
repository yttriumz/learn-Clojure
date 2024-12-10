(ns ch4-2-seq-functions)

;; ================================
;; map
;; ================================

;; You’ve seen many examples of map by now,
;; but this section shows map doing two new tasks:
;; taking multiple collections as arguments
;; and taking a collection of functions as an argument.
;; It also highlights a common map pattern:
;; using keywords as the mapping function.

;; You can also give map multiple collections.
(map str ["a" "b" "c"] ["A" "B" "C"])
;; It’s as if map does the following:
(list (str "a" "A") (str "b" "B") (str "c" "C"))

;; Another fun thing you can do with map is pass it a collection of functions.
;; You could use this if you wanted to perform a set of calculations
;; on different collections of numbers, like so:
(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(stats [3 4 10])
(stats [80 1 44 13 6])

;; Additionally, Clojurists often use map to retrieve the value associated with a keyword
;; from a collection of map data structures.
;; Because keywords can be used as functions,
;; you can do this succinctly. Here’s an example:
(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])
(map :real identities)

;; ================================
;; reduce
;; ================================

;; Chapter 3 showed how reduce processes each element in a sequence to build a result.
;; This section shows a couple of other ways to use it that might not be obvious.

;; The first use is to transform a map’s values,
;; producing a new map with the same keys but with updated values:
(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})
;; In this example, reduce treats the argument {:max 30 :min 10} as a sequence of vectors,
;; like ([:max 30] [:min 10]).
;; Then, it starts with an empty map (the second argument)
;; and builds it up using the first argument, an anonymous function.
;; It’s as if reduce does this:
(assoc
 (assoc
  {}
  :max (inc 30))
 :min (inc 10))
;; The function assoc takes three arguments:
;; a map, a key, and a value.
;; It derives a new map from the map you give it
;; by associating the given key with the given value.

;; Another use for reduce is to filter out keys from a map based on their value.
;; In the following example,
;; the anonymous function checks whether the value of a key-value pair is greather than 4.
;; If it isn’t, then the key-value pair is filtered out.
;; In the map {:human 4.1 :critter 3.9}, 3.9 is less than 4,
;; so the :critter key and its 3.9 value are filtered out.
(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1
         :critter 3.9})

;; The takeaway here is that reduce is a more flexible function than it first appears.
;; Whenever you want to derive a new value from a seqable data structure,
;; reduce will usually be able to do what you need.
;; If you want an exercise that will really blow your hair back,
;; try implementing map using reduce,
;; and then do the same for filter and some after you read about them later in this chapter.
(defn my-map
  [f xs]
  (reduce (fn [ys x]
            (conj ys (f x)))
          []
          xs))

(my-map inc [1 2 3 4])
(my-map #(>= % 3) [1 2 3 4])
(my-map #(inc (second %)) {:one 1 :two 2 :three 3})

;; ===============================
;; take, drop, take-while, and drop-while
;; ===============================

(take 3 [1 2 3 4 5 6 7 8 9 10])
(take 3 '(1 2 3 4 5 6 7 8 9 10))
(drop 3 [1 2 3 4 5 6 7 8 9 10])
(drop 3 '(1 2 3 4 5 6 7 8 9 10))

(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}
   {:month 1 :day 3 :human 5.3 :critter 2.3}
   {:month 1 :day 4 :human 5.1 :critter 2.0}
   {:month 2 :day 3 :human 4.9 :critter 2.1}
   {:month 2 :day 4 :human 5.0 :critter 2.5}])
(take-while #(< (:month %) 3) food-journal)
(drop-while #(< (:month %) 3) food-journal)
;; These are different from filter.

;; ===============================
;; filter and some
;; ===============================

(filter #(< (:month %) 3) food-journal)

(some #(> (:critter %) 5) food-journal)
(some #(> (:critter %) 3) food-journal)
;; Notice that the return value in the second example is true
;; and not the actual entry that produced the true value.
;; The reason is that the anonymous function #(> (:critter %) 3) returns true or false.
;; Here’s how you could return the entry:
(some #(and (> (:critter %) 3) %) food-journal)

;; ===============================
;; sort and sort-by
;; ===============================

(sort [3 1 2])
(sort ["aaa" "c" "bb"])
(sort-by count ["aaa" "c" "bb"])

;; ===============================
;; concat
;; ===============================

(concat [1 2] [3 4])
(concat [1 2] '(3 4))
