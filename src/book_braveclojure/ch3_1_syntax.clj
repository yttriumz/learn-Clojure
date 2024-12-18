(ns ch3-1-syntax)

;; ================================
;; Forms
;; ================================

;; All Clojure code is written in a uniform structure.
;; Clojure recognizes two kinds of structures:
;; - Literal representations of data structures (like numbers, strings, maps, and vectors)
;; - Operations
;; We use the term form to refer to valid code.
;; Clojure evaluates every form to produce a value.

;; These literal representations are all valid forms:
1
"a string"
["a" "vector" "of" "strings"]

;; Your code will rarely contain free-floating literals, of course,
;; because they don’t actually do anything on their own.
;; Instead, you’ll use literals in operations.
;; Operations are how you do things.
;; All operations take the form opening parenthesis, operator, operands, closing parenthesis:
;;
;; (operator operand1 operand2 ... operandn)
;;
;; Notice that there are no commas.
;; Clojure uses whitespace to separate operands,
;; and it treats commas as whitespace.

;; ================================
;; if
;; ================================

(if true
  "By Zeus's hammer!"
  "By Aquaman's trident!")

(if false
  "By Zeus's hammer!"
  "By Aquaman's trident!")

;; You can also omit the else branch.
;; If you do that and the Boolean expression is false,
;; Clojure returns nil, like this:
(if false
  "By Odin's Elbow!")

(if true
  (do (println "Success!")
      "By Zeus's hammer!")
  (do (println "Failure!")
      "By Aquaman's trident!"))

;; ================================
;; when
;; ================================

;; The when operator is like a combination of if and do, but with no else branch.
;; Use when if you want to do multiple things when some condition is true,
;; and you always want to return nil when the condition is false.
(when true
  (println "Success!")
  "abra cadabra!")

;; ================================
;; nil, true, false, Truthiness, Equality, and Boolean Expressions
;; ================================

;; Clojure has true and false values. nil is used to indicate no value in Clojure.
(nil? 1)
(nil? nil)

;; Both nil and false are used to represent logical falsiness,
;; whereas all other values are logically truthy.
;; Truthy and falsey refer to how a value is treated in a Boolean expression.

;; Clojure uses the Boolean operators or and and.
;; or returns either the first truthy value or the last value.
;; and returns the first falsey value or, if no values are falsey, the last truthy value.
(and :feelin_super_cool nil false)
(and :free_wifi :hot_coffee)
(or false nil :large_I_mean_venti :why_cant_I_just_say_large)
(or (= 0 1) (= "yes" "no"))
(or nil)

;; ===============================
;; Naming Values with def
;; ===============================

;; You use def to bind a name to a value in Clojure:
(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])
failed-protagonist-names

;; Notice that I’m using the term bind,
;; whereas in other languages you’d say you’re assigning a value to a variable. \
;; Those other languages typically encourage you to perform multiple assignments to the same variable.
;; You might be tempted to do this in Clojure:
;;
;; (def severity :mild)
;; (def error-message "OH GOD! IT'S A DISASTER! WE'RE ")
;; (if (= severity :mild)
;;   (def error-message (str error-message "MILDLY INCONVENIENCED!"))
;;   (def error-message (str error-message "DOOOOOOOMED!")))
;;
;; As you learn Clojure, you’ll find that you’ll rarely need to alter a name/value association.
;; Here’s one way you could write the preceding code:
(defn error-message
  [severity]
  (str "OH GOD! IT'S A DISASTER! WE'RE "
       (if (= severity :mild)
         "MILDLY INCONVENIENCED!"
         "DOOOOOOOMED!")))

(error-message :mild)