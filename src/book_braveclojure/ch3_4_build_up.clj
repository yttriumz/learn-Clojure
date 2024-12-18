(ns ch3-4-build-up)

;; ================================
;; The Shire’s Next Top Model
;; ================================

(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])

(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))

(symmetrize-body-parts asym-hobbit-body-parts)

;; ================================
;; let
;; ================================

;; let binds names to values.
(let [x 3]
  x)
(def dalmatian-list
  ["Pongo" "Perdita" "Puppy 1" "Puppy 2"])
(let [dalmatians (take 2 dalmatian-list)]
  dalmatians)

;; let also introduces a new scope:
(def x 0)
(let [x 1] x)
;; In this code snippet, you’re saying,
;; “I want x to be 0 in the global context,
;; but within the context of this let expression, it should be 1.”

;; You can reference existing bindings in your let binding:
(let [x (inc x)] x)
;; In this example, the x in (inc x) refers to the binding created by (def x 0).

;; You can also use rest parameters in let, just like you can in functions:
(let [[pongo & dalmatians] dalmatian-list]
  [pongo dalmatians])
;; Notice that the value of a let form is the last form in its body that is evaluated.
;; let forms follow all the destructuring rules introduced in “Calling Functions”.
;; In this case, [pongo & dalmatians] destructured dalmatian-list,
;; binding the string "Pongo" to the name pongo
;; and the list of the rest of the dalmatians to dalmatians.
;; The vector [pongo dalmatians] is the last expression in let,
;; so it’s the value of the let form.

;; let forms have two main uses.
;; First, they provide clarity by allowing you to name things.
;; Second, they allow you to evaluate an expression only once and reuse the result.
;; This is especially important when you need to reuse the result of an expensive function call,
;; like a network API call.
;; It’s also important when the expression has side effects.

;; ================================
;; loop
;; ================================

;; loop provides another way to do recursion in Clojure.
(loop [iteration 0]
  (println (str "Iteration " iteration))
  (if (> iteration 3)
    (println "Goodbye!")
    (recur (inc iteration))))
;; The first line, loop [iteration 0], begins the loop
;; and introduces a binding with an initial value.
;; On the first pass through the loop, iteration has a value of 0.
;; Next, it prints a short message.
;; Then, it checks the value of iteration.
;; If the value is greater than 3, it’s time to say Goodbye.
;; Otherwise, we recur.
;; It’s as if loop creates an anonymous function with a parameter named iteration,
;; and recur allows you to call the function from within itself, passing the argument (inc iteration).

;; You could in fact accomplish the same thing by just using a normal function definition:
(defn recursive-printer
  ([]
   (recursive-printer 0))
  ([iteration]
   (println iteration)
   (if (> iteration 3)
     (println "Goodbye!")
     (recursive-printer (inc iteration)))))

(recursive-printer)
;; But as you can see, this is a bit more verbose.
;; Also, loop has much better performance.

;; ================================
;; Regular Expressions
;; ================================

;; The literal notation for a regular expression is to place the expression in quotes after a hash mark:
(re-find #"^left-" "left-eye")
(re-find #"^left-" "cleft-chin")
(re-find #"^left-" "wongleblart")

;; Here are a couple of examples of matching-part using a regex to replace "left-" with "right-":
(matching-part {:name "left-eye" :size 1})
(matching-part {:name "head" :size 3})

;; ================================
;; Better Symmetrizer with reduce
;; ================================

;; The pattern of
;; process each element in a sequence and build a result
;; is so common that there’s a built-in function for it called reduce.

;; Here’s a simple example:
(reduce + [1 2 3 4])
;; This is like telling Clojure to do this:
(+ (+ (+ 1 2) 3) 4)

;; reduce also takes an optional initial value. The initial value here is 15:
(reduce + 15 [1 2 3 4])
;; If you provide an initial value,
;; reduce starts by applying the given function to the initial value and the first element of the sequence
;; rather than the first two elements of the sequence.

;; One detail to note is that, in these examples,
;; reduce takes a collection of elements, [1 2 3 4], and returns a single number.
;; Although programmers often use reduce this way,
;; you can also use reduce to return an even larger collection than the one you started with,
;; as we’re trying to do with symmetrize-body-parts.
;; reduce abstracts the task “process a collection and build a result,”
;; which is agnostic about the type of result returned.

;; To further understand how reduce works, here’s one way that you could implement it:
(defn my-reduce
  ([f initial coll]
   (loop [result initial
          remaining coll]
     (if (empty? remaining)
       result
       (recur (f result (first remaining)) (rest remaining)))))
  ([f [head & tail]]
   (my-reduce f head tail)))

;; We could reimplement our symmetrizer as follows:
(defn better-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts
                  (set [part (matching-part part)])))
          []
          asym-body-parts))
;; One immediately obvious advantage of using reduce is that you write less code overall.
;; Using reduce is also more expressive.
;; Finally, by abstracting the reduce process into a function that takes another function as an argument,
;; your program becomes more composable.

;; ================================
;; Hobbit Violence
;; ================================

;; Here’s a function that determines which part of a hobbit is hit:
(defn hit
  [asym-body-parts]
  (let [sym-parts (better-symmetrize-body-parts asym-body-parts)
        body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    (loop [[part & remaining] sym-parts
           accumulated-size (:size part)]
      (if (> accumulated-size target)
        part
        (recur remaining (+ accumulated-size (:size (first remaining))))))))

(hit asym-hobbit-body-parts)
