(ns ch3-3-functions)

;; ================================
;; Calling functions
;; ================================

;; Remember that all Clojure operations have the same syntax:
;; opening parenthesis, operator, operands, closing parenthesis.
;; Function call is just another term for an operation
;; where the operator is a function or a function expression (an expression that returns a function).
;; This lets you write some pretty interesting code.
((or + -) 1 2 3)
((or - +) 1 2 3)
((and (= 1 1) +) 1 2 3)
((first [+ 0]) 1 2 3)

;; However, these aren’t valid function calls,
;; because numbers and strings aren’t functions:
;; (1 2 3 4)
;; ("test" 1 2 3)

;; Function flexibility doesn’t end with the function expression!
;; Syntactically, functions can take any expressions as arguments—including other functions.
;; Functions that can either take a function as an argument or return a function are called higher-order functions.
;; Programming languages with higher-order functions are said to support first-class functions
;; because you can treat functions as values in the same way you treat more familiar data types like numbers and vectors.

;; Take the map function (not to be confused with the map data structure), for instance.
;; map creates a new list by applying a function to each member of a collection.
;; Here, the inc function increments a number by 1:
(map inc [0 1 2 3.4])
;; (Note that map doesn’t return a vector,
;; even though we supplied a vector as an argument.
;; You’ll learn why in Chapter 4.
;; For now, just trust that this is okay and expected.)

;; The last detail that you need know about function calls is that
;; Clojure evaluates all function arguments recursively before passing them to the function.
;; Here’s how Clojure would evaluate a function call whose arguments are also function calls:
(+ (inc 199) (/ 100 (- 7 2)))
(+ 200 (/ 100 (- 7 2))) ; evaluated "(inc 199)"
(+ 200 (/ 100 5)) ; evaluated (- 7 2)
(+ 200 20) ; evaluated (/ 100 5)
220 ; final evaluation

;; ================================
;; Function Calls, Macro Calls, and Special Forms
;; ================================

;; In the previous section, you learned that function calls are expressions that have a function expression as the operator.
;; The two other kinds of expressions are macro calls and special forms.
;; You’ve already seen a couple of special forms: definitions and if expressions.

;; For now, the main feature that makes special forms “special” is that,
;; unlike function calls, they don’t always evaluate all of their operands.
;; Another feature that differentiates special forms is that
;; you can’t use them as arguments to functions.
;; In general, special forms implement core Clojure functionality that just can’t be implemented with functions.
;; Clojure has only a handful of special forms,
;; and it’s pretty amazing that such a rich language is implemented with such a small set of building blocks.

;; Macros are similar to special forms in that they evaluate their operands differently from function calls,
;; and they also can’t be passed as arguments to functions.

;; ================================
;; Defining Functions
;; ================================

;; Function definitions are composed of five main parts:
;; - defn
;; - Function name
;; - A docstring describing the function (optional)
;; - Parameters listed in brackets
;; - Function body

;; The docstring is a useful way to describe and document your code.
;; You can view the docstring for a function in the REPL with (doc fn-name)—for example, (doc map).
;; The docstring also comes into play if you use a tool to generate documentation for your code.

;; Clojure functions can be defined with zero or more parameters.
;; The values you pass to functions are called arguments,
;; and the arguments can be of any type.
;; The number of parameters is the function’s arity.
;; Here are some function definitions with different arities:
(defn no-params
  []
  "I take no parameters!")
(defn one-param
  [x]
  (str "I take one parameter: " x))
(defn two-params
  [x y]
  (str "Two parameters! That's nothing! Pah! I will smoosh them "
       "together to spite you! " x y))

;; Functions also support arity overloading.
;; This means that you can define a function so a different function body will run depending on the arity.
;; Here’s the general form of a multiple-arity function definition.
;; Notice that each arity definition is enclosed in parentheses and has an argument list:
(defn multi-arity
  ;; 3-arity arguments and body
  ([first-arg second-arg third-arg]
   (println first-arg second-arg third-arg))
  ;; 2-arity arguments and body
  ([first-arg second-arg]
   (println first-arg second-arg))
  ;; 1-arity arguments and body
  ([first-arg]
   (println first-arg)))

;; Arity overloading is one way to provide default values for arguments.
;; In the following example, "karate" is the default argument for the chop-type parameter:
(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
   (str "I " chop-type " chop " name "! Take that!"))
  ([name]
   (x-chop name "karate")))

;; Clojure also allows you to define variable-arity functions by including a rest parameter,
;; as in “put the rest of these arguments in a list with the following name.”
;; The rest parameter is indicated by an ampersand (&).
(defn codger-communication
  [whippersnapper]
  (str "Get off my lawn, " whippersnapper "!!!"))

(defn codger
  [& whippersnappers]
  (map codger-communication whippersnappers))

(codger "Billy" "Anne-Marie" "The Incredible Bulk")

;; As you can see, when you provide arguments to variable-arity functions,
;; the arguments are treated as a list.
;; You can mix rest parameters with normal parameters,
;; but the rest parameter has to come last:
(defn favorite-things
  [name & things]
  (str "Hi, " name ", here are my favorite things: "
       (clojure.string/join ", " things)))

(favorite-things "Doreen" "gum" "shoes" "kara-te")

;; Finally, Clojure has a more sophisticated way of defining parameters, called destructuring.
;; The basic idea behind destructuring is that
;; it lets you concisely bind names to values within a collection.
(defn my-first
  [[first-thing]] ; Notice that first-thing is within a vector
  first-thing)

(my-first ["oven" "bike" "war-axe"])
(my-first '("oven" "bike" "war-axe"))

(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is: " first-choice))
  (println (str "Your second choice is: " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (clojure.string/join ", " unimportant-choices))))

(chooser ["Marmalade", "Handsome Jack", "Pigpen", "Aquaman"])

;; You can also destructure maps.
;; In the same way that you tell Clojure to destructure a vector or list by providing a vector as a parameter,
;; you destructure maps by providing a map as a parameter:
(defn announce-treasure-location
  [{lat :lat lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(announce-treasure-location {:lat 28.22 :lng 81.33})

;; We often want to just break keywords out of a map,
;; so there’s a shorter syntax for that.
;; This has the same result as the previous example:
(defn announce-treasure-location'
  [{:keys [lat lng]}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(announce-treasure-location' {:lat 28.22 :lng 81.33})

;; You can retain access to the original map argument by using the :as keyword.
;; In the following example, the original map is accessed with treasure-location:
(defn receive-treasure-location
  [{:keys [lat lng] :as treasure-location}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng))

  ;; One would assume that this would put in new coordinates for your ship:
  ;; (steer-ship! treasure-location)
  )

;; The function body can contain forms of any kind.
;; Clojure automatically returns the last form evaluated.

;; All Functions Are Created Equal
;; One final note: Clojure has no privileged functions.
;; + is just a function, - is just a function, and inc and map are just functions.
;; They’re no better than the functions you define yourself.
;; So don’t let them give you any lip!

;; ==================================
;; Anonymous Functions
;; ==================================

;; You create anonymous functions in two ways. The first is to use the fn form:
;;
;; (fn [param-list]
;;   function body)
;;
;; Looks a lot like defn, doesn’t it? Let’s try a couple of examples:
((fn [x] (* x 3)) 8)
(map (fn [name] (str "Hi, " name)) ["Darth Vader" "Mr. Magoo"])

;; You can treat fn nearly identically to the way you treat defn.
;; The parameter lists and function bodies work exactly the same.
;; You can use argument destructuring, rest parameters, and so on.
;; You could even associate your anonymous function with a name,
;; which shouldn’t come as a surprise (if that does come as a surprise, then . . . Surprise!):
(def my-special-multiplier (fn [x] (* x 3)))
(my-special-multiplier 12)

;; Clojure also offers another, more compact way to create anonymous functions.
;; Here’s what an anonymous function looks like:
#(* % 3)
(#(* % 3) 8)
(map #(str "Hi, " %) ["Darth Vader" "Mr. Magoo"])
;; This strange-looking style of writing anonymous functions is made possible by a feature called reader macros.

;; You can see that this syntax is definitely more compact,
;; but it’s also a little odd.
;; Let’s break it down.
;; This kind of anonymous function looks a lot like a function call,
;; except that it’s preceded by a hash mark, #:
(* 8 3) ; Function call
#(* % 3) ; Anonymous function

;; The percent sign, %, indicates the argument passed to the function.
;; If your anonymous function takes multiple arguments,
;; you can distinguish them like this: %1, %2, %3, and so on. % is equivalent to %1:
(#(str %1 " and " %2) "cornbread" "butter beans")

;; You can also pass a rest parameter with %&:
(#(identity %&) 1 "blarg" :yip)
(#(str %1 ", " %2 ", and the rest: " (apply str %&))
 "Marmalade" "Handsome Jack" "Pigpen" "Aquaman")

;; ================================
;; Returning Functions
;; ================================

;; By now you’ve seen that functions can return other functions.
;; The returned functions are closures,
;; which means that they can access all the variables that were in scope when the function was created.
;; Here’s a standard example:
(defn inc-maker
  "Create a custom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-maker 3))
(inc3 7)
