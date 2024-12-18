(ns ch4-3-lazy-seqs)

;; Many functions, including map and filter, return a lazy seq.
;; A lazy seq is a seq whose members aren’t computed until you try to access them.
;; Computing a seq’s members is called realizing the seq.
;; Deferring the computation until the moment it’s needed makes your programs more efficient,
;; and it has the surprising benefit of allowing you to construct infinite sequences.

;; ================================
;; Demonstrating Lazy Seq Efficiency
;; ================================

(def vampire-database
  {0 {:makes-blood-puns? false, :has-pulse? true  :name "McFishwich"}
   1 {:makes-blood-puns? false, :has-pulse? true  :name "McMackson"}
   2 {:makes-blood-puns? true,  :has-pulse? false :name "Damon Salvatore"}
   3 {:makes-blood-puns? true,  :has-pulse? true  :name "Mickey Mouse"}})

(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))
       record))

(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))

(time (vampire-related-details 0)) ; "Elapsed time: 1000.285581 msecs"

;; Because map is lazy,
;; it doesn’t actually apply vampire-related-details to Social Security numbers
;; until you try to access the mapped element.
;; In fact, map returns a value almost instantly:
(time (def mapped-details (map vampire-related-details (range 0 1000000)))) ; "Elapsed time: 0.023426 msecs"

;; You can think of a lazy seq as consisting of two parts:
;; a recipe for how to realize the elements of a sequence
;; and the elements that have been realized so far.
;; When you use map, the lazy seq it returns doesn’t include any realized elements yet,
;; but it does have the recipe for generating its elements.
;; Every time you try to access an unrealized element,
;; the lazy seq will use its recipe to generate the requested element.

;; In the previous example, mapped-details is unrealized.
;; Once you try to access a member of mapped-details,
;; it will use its recipe to generate the element you’ve requested,
;; and you’ll incur the one-second-per-database-lookup cost:
(time (first mapped-details)) ; "Elapsed time: 32008.255577 msecs"
;; This operation took about 32 seconds.
;; That’s much better than one million seconds,
;; but it’s still 31 seconds more than we would have expected.
;; After all, you’re only trying to access the very first element,
;; so it should have taken only one second.
;; The reason it took 32 seconds is that Clojure chunks its lazy sequences,
;; which just means that whenever Clojure has to realize an element,
;; it preemptively realizes some of the next elements as well.
;; In this example, you wanted only the very first element of mapped-details,
;; but Clojure went ahead and prepared the next 31 as well.
;; Clojure does this because it almost always results in better performance.

;; Thankfully, lazy seq elements need to be realized only once.
;; Accessing the first element of mapped-details again takes almost no time:
(time (first mapped-details)) ; "Elapsed time: 0.003865 msecs"

;; With all this newfound knowledge,
;; you can efficiently mine the vampire database to find the fanged culprit:
(time (identify-vampire (range 0 1000000))) ; "Elapsed time: 32008.142127 msecs"

;; ==================================
;; Infinite Sequences
;; ==================================

(concat (take 8 (repeat "na")) ["Batman!"])
(take 3 (repeatedly (fn [] (rand-int 10))))

;; A lazy seq’s recipe doesn’t have to specify an endpoint.
;; Functions like first and take,
;; which realize the lazy seq,
;; have no way of knowing what will come next in a seq,
;; and if the seq keeps providing elements, well, they’ll just keep taking them.
;; You can see this if you construct your own infinite sequence:
(defn even-numbers
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(take 10 (even-numbers))
