(ns ch5-2-immu-data-structures)

;; ================================
;; Recursion Instead of for/while
;; ================================

;; Look at the following example in JS:
;;
;;
;; var wrestlers = getAlligatorWrestlers();
;; var totalBites = 0;
;; var l = wrestlers.length;
;;
;; for(var i=0; i < l; i++){
;;   totalBites += wrestlers[i].timesBitten;
;; }
;;
;;
;; Notice that the example induces side effects on the looping variable i,
;; as well as a variable outside the loop (totalBites).
;; Using side effects this way—mutating internal variables—is pretty much harmless.
;; You’re creating new values,
;; as opposed to changing an object you’ve received from elsewhere in your program.

;; The example builds a sum.
;; Clojure has no assignment operator.
;; You can’t associate a new value with a name without creating a new scope:
(def great-baby-name "Rosanthony")
great-baby-name
(let [great-baby-name "Bloodthunder"]
  great-baby-name)
great-baby-name
;; In this example,
;; you first bind the name great-baby-name to "Rosanthony" within the global scope.
;; Next, you introduce a new scope with let.
;; Within that scope, you bind great-baby-name to "Bloodthunder".
;; Once Clojure finishes evaluating the let expression, you’re back in the global scope,
;; and great-baby-name evaluates to "Rosanthony" once again.

;; Clojure lets you work around this apparent limitation with recursion.
;; The following example shows the general approach to recursive problem solving:
(defn sum
  ([vals] (sum vals 0))
  ([vals accumulating-total]
   (if (empty? vals)
     accumulating-total
     (sum (rest vals) (+ (first vals) accumulating-total)))))
;; Here’s what the recursive function call might look like
;; if we separate out each time it recurs:
(sum [39 5 1]) ; single-arity body calls two-arity body
(sum [39 5 1] 0)
(sum [5 1] 39)
(sum [1] 44)
(sum [] 45) ; base case is reached, so return accumulating-total
; => 45
;; Each recursive call to sum creates a new scope
;; where vals and accumulating-total are bound to different values,
;; all without needing to alter the values originally passed to the function
;; or perform any internal mutation.
;; As you can see, you can get along fine without mutation.

;; Note that you should generally use recur when doing recursion for performance reasons.
;; The reason is that Clojure doesn’t provide tail call optimization.
(defn sum'
  ([vals]
   (sum vals 0))
  ([vals accumulating-total]
   (if (empty? vals)
     accumulating-total
     (recur (rest vals) (+ (first vals) accumulating-total)))))
;; Using recur isn’t that important if you’re recursively operating on a small collection,
;; but if your collection contains thousands or millions values,
;; you will definitely need to whip out recur
;; so you don’t blow up your program with a stack overflow.

(defn fact
  [num]
  (loop [n num prod 1]
    (if (= n 1)
      prod
      (recur (dec n) (* prod n)))))

(defn fact'
  [num]
  (if (= 1 num)
    1
    (* num (fact' (dec num)))))

(defn fact''
  [num]
  (if (= 1 num)
    1
    (* num (recur (dec num)))))

(defn fact'''
  ([num] (fact''' num 1))
  ([num prod]
   (if (= 1 num)
     prod
     (recur (dec num) (* num prod)))))

(time (fact 100000N)) ; This one runs normally.
(time (fact' 100000N)) ; This one will stack overflow.
(time (fact'' 100000N)) ; This one is wrong.
(time (fact''' 100000N)) ; This one runs normally.

;; One last thing! You might be saying,
;; “Wait a minute—what if I end up creating thousands of intermediate values?
;; Doesn’t this cause the program to thrash because of garbage collection or whatever?”
;; The answer is no!
;; The reason is that, behind the scenes,
;; Clojure’s immutable data structures are implemented using structural sharing,
;; which is totally beyond the scope of this book.
;; It’s kind of like Git!
;; Read this great article if you want to know more:
;; http://hypirion.com/musings/understanding-persistent-vector-pt-1.

;; ================================
;; Function Composition Instead of Attribute Mutation
;; ================================

;; Another way you might be used to using mutation is
;; to build up the final state of an object.
;; In the following Ruby example,
;; the GlamourShotCaption object
;; uses mutation to clean input by removing trailing spaces and capitalizing "lol":
;;
;;
;; class GlamourShotCaption
;;   attr_reader :text
;;   def initialize(text)
;;     @text = text
;;     clean!
;;   end
;;
;;   private
;;   def clean!
;;     text.trim!
;;     text.gsub!(/lol/, "LOL")
;;   end
;; end
;;
;; best = GlamourShotCaption.new("My boa constrictor is so sassy lol!  ")
;; best.text
;;
;;
;; In this code, the class GlamourShotCaption
;; encapsulates the knowledge of how to clean a glamour shot caption.
;; On creating a GlamourShotCaption object,
;; you assign text to an instance variable and progressively mutate it.
;; This is how you might do it in Clojure:
(require '[clojure.string :as s])
(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(clean "My boa constrictor is so sassy lol!  ")
;; No mutation required.
;; Instead of progressively mutating an object,
;; the clean function works by passing an immutable value, text, to a pure function, s/trim,
;; which returns an immutable value
;; ("My boa constrictor is so sassy lol!"; the spaces at the end of the string have been trimmed).
;; That value is then passed to the pure function s/replace,
;; which returns another immutable value
;; ("My boa constrictor is so sassy LOL!").

;; Combining functions like this—
;; so that the return value of one function is passed as an argument to another—
;; is called function composition.
;; In fact, this isn’t so different from the previous example,
;; which used recursion,
;; because recursion continually passes the result of a function to another function;
;; it just happens to be the same function.
;; In general, functional programming encourages you to build more complex functions by combining simpler functions.

;; This comparison also starts to reveal some limitations of object-oriented programming (OOP).
;; In OOP, one of the main purposes of classes is to protect against unwanted modification of private data—
;; something that isn’t necessary with immutable data structures.
;; You also have to tightly couple methods with classes,
;; thus limiting the reusability of the methods.
;; In the Ruby example, you have to do extra work to reuse the clean! method.
;; In Clojure, clean will work on any string at all.
;; By both
;; a) decoupling functions and data, and
;; b) programming to a small set of abstractions,
;; you end up with more reusable, composable code.
;; You gain power and lose nothing.