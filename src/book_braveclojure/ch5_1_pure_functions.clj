(ns ch5-1-pure-functions)

;; Except for println and rand,
;; all the functions you’ve used up till now have been pure functions.
;; What makes them pure functions, and why does it matter?
;; A function is pure if it meets two qualifications:
;;
;; - It always returns the same result if given the same arguments.
;;   This is called referential transparency,
;;   and you can add it to your list of $5 programming terms.
;; - It can’t cause any side effects.
;;   That is, the function can’t make any changes that are observable outside the function itself—
;;   for example, by changing an externally accessible mutable object or writing to a file.
;;
;; When you use them, you don’t have to ask yourself, “What could I break by calling this function?”
;; They’re also consistent:
;; you’ll never need to figure out why passing a function the same arguments results in different return values,
;; because that will never happen.

;; ================================
;; Pure Functions Are Referentially Transparent
;; ================================

;; To return the same result when called with the same argument,
;; pure functions rely only on
;; 1) their own arguments and
;; 2) immutable values to determine their return value.

;; If a function relies on an immutable value, it’s referentially transparent.
;; The string ", Daniel-san" is immutable,
;; so the following function is also referentially transparent:
(defn wisdom
  [words]
  (str words ", Daniel-san"))

(wisdom "Always bathe on Fridays")

;; By contrast, the following functions do not yield the same result with the same arguments;
;; therefore, they are not referentially transparent.
;; Any function that relies on a random number generator cannot be referentially transparent:
(defn year-end-evaluation
  []
  (if (> (rand) 0.5)
    "You get a raise!"
    "Better luck next year!"))

;; If your function reads from a file,
;; it’s not referentially transparent because the file’s contents can change.
;; The following function, analyze-file, is not referentially transparent,
;; but the function analysis is:
(defn analysis
  [text]
  (str "Character count: " (count text)))

(defn analyze-file
  [filename]
  (analysis (slurp filename)))

;; When using a referentially transparent function,
;; you never have to consider what possible external conditions could affect the return value of the function.
;; This is especially important
;; if your function is used multiple places or
;; if it’s nested deeply in a chain of function calls.
;; In both cases, you can rest easy knowing that changes to external conditions won’t cause your code to break.

;; ================================
;; Pure Functions Have No Side Effects
;; ================================

;; To perform a side effect is to change the association between a name and its value within a given scope.
;; Of course, your program has to have some side effects.
;; It writes to a disk,
;; which changes the association between a filename and a collection of disk sectors;
;; it changes the RGB values of your monitor’s pixels; and so on.
;; Otherwise, there’d be no point in running it.

;; Side effects are potentially harmful, however,
;; because they introduce uncertainty about what the names in your code are referring to.
;; This leads to situations
;; where it’s very difficult to trace why and how a name came to be associated with a value,
;; which makes it hard to debug the program.
;; When you call a function that doesn’t have side effects,
;; you only have to consider the relationship between the input and the output.
;; You don’t have to worry about other changes that could be rippling through your system.

;; ================================
;; comp
;; ================================

;; You can derive new functions from existing functions
;; in the same way that you derive new data from existing data.
;; You’ve already seen one function, partial, that creates new functions.
;; There are two more functions, comp and memoize,
;; which rely on referential transparency, immutability, or both.

;; It’s always safe to compose pure functions,
;; because you only need to worry about their input/output relationship.
;; Composing functions is so common that Clojure provides a function, comp,
;; for creating a new function from the composition of any number of functions.
;; Here’s a simple example:
((comp inc *) 2 3)
;; Here, you create an anonymous function by composing the inc and * functions.
;; Then, you immediately apply this function to the arguments 2 and 3.
;; The function
;; multiplies the numbers 2 and 3
;; and then increments the result.
;; Using math notation, you’d say that, in general,
;; using comp on the functions f1, f2, ... fn,
;; creates a new function g
;; such that g (x1, x2, ... xn) equals f1 (f2 (fn (x1, x2, ... xn))).
;; Note that the first function applied—
;; * in the code shown here—
;; can take any number of arguments,
;; whereas the remaining functions must be able to take only one argument.

;; Here’s an example that shows how
;; you could use comp to retrieve character attributes in role-playing games:
(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})

(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-dex (comp :dexterity :attributes))
(c-int character)
(c-str character)
(c-dex character)
;; Instead of using comp, you could just have written something like this for each attribute:
(fn [c] (:strength (:attributes c)))
;; But comp is more elegant
;; because it uses less code to convey more meaning.
;; When you see comp,
;; you immediately know that
;; the resulting function’s purpose is to combine existing functions in a well-known way.

;; What do you do if one of the functions you want to compose needs to take more than one argument?
;; You wrap it in an anonymous function.
;; Have a look at this next snippet,
;; which calculates the number of spell slots your character has
;; based on her intelligence attribute:
(defn spell-slots
  [char]
  (int (inc (/ (c-int char) 2))))

(spell-slots character)
;; First, you divide intelligence by two,
;; then you add one,
;; and then you use the int function to round down.

;; Here’s how you could do the same thing with comp:
(def spell-slots-comp (comp int inc #(/ % 2) c-int))

(spell-slots-comp character)
;; To divide by two, all you needed to do was wrap the division in an anony­mous function.

;; Clojure’s comp function can compose any number of functions.
;; To get a hint of how it does this,
;; here’s an implementation that composes just two functions:
(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))
;; Also, try reimplementing Clojure’s comp function so you can compose any number of functions.

;; NOTE: Review for fun.
(defn my-comp
  [& fs]
  (fn [& args]
    (let [fs' (reverse fs)]
      (loop [[f & fs] fs' args args]
        (if (nil? f)
          (args 0)
          (recur fs (conj [] (apply f args))))))))

((my-comp inc *) 2 3)
((my-comp int inc #(/ % 2) c-int) character)

;; ================================
;; memoize
;; ================================

;; Another cool thing you can do with pure functions is memoize them
;; so that Clojure remembers the result of a particular function call.
;; You can do this because, as you learned earlier,
;; pure functions are referentially transparent.
;; For example, + is referentially transparent. You can replace
(+ 3 (+ 5 8))
;; with
(+ 3 13)
;; or
16
;; and the program will have the same behavior.

;; Memoization lets you take advantage of referential transparency
;; by storing the arguments passed to a function and the return value of the function.
;; That way, subsequent calls to the function with the same arguments can return the result immediately.
;; This is especially useful for functions that take a lot of time to run.
;; For example, in this unmemoized function,
;; the result is returned after one second:
(defn sleepy-identity
  "Returns the given value after 1 second"
  [x]
  (Thread/sleep 1000)
  x)
(time (sleepy-identity "Mr. Fantastico")) ; "Elapsed time: 1002.615132 msecs"
(time (sleepy-identity "Mr. Fantastico")) ; "Elapsed time: 1000.225086 msecs"

;; However, if you create a new,
;; memoized version of sleepy-identity with memoize,
;; only the first call waits one second; every subsequent function call returns immediately:
(def memo-sleepy-identity (memoize sleepy-identity))
(time (memo-sleepy-identity "Mr. Fantastico")) ; "Elapsed time: 1000.329865 msecs"
(time (memo-sleepy-identity "Mr. Fantastico")) ; "Elapsed time: 0.008208 msecs"
;; This implementation could be useful for functions that are computationally intensive
;; or that make network requests.
