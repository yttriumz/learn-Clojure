(ns braveclojure.ch3-2-data-structures)

;; ================================
;; Strings
;; ================================

;; Notice that Clojure only allows double quotes to delineate strings.
;; 'Lord Voldemort', for example, is not a valid string.
;; Also notice that Clojure doesn’t have string interpolation.
;; It only allows concatenation via the str function:
(def somename "Chewbacca")
(println (str "\"Uggllglglglglglglglll\" - " somename))

;; ================================
;; Maps & Keywords
;; ================================

;; Maps are similar to dictionaries or hashes in other languages.
;; They’re a way of associating some value with some other value.
;; The two kinds of maps in Clojure are hash maps and sorted maps.
;; I’ll only cover the more basic hash maps.
{}
{:first-name "Charlie"
 :last-name "McFishwich"}
{"string-key" +}
{:name {:first "John" :middle "Jacob" :last "Jingleheimerschmidt"}}

;; Besides using map literals, you can use the hash-map function to create a map:
(hash-map :a 1 :b 2)

;; You can look up values in maps with the get function:
;; get will return nil if it doesn’t find your key,
;; or you can give it a default value to return, such as "unicorns?":
(get {:a 0 :b 1} :b)
(get {:a 0 :b {:c "ho hum"}} :b)
(get {:a 0 :b 1} :c)
(get {:a 0 :b 1} :c "unicorns?")

;; The get-in function lets you look up values in nested maps:
(get-in {:a 0 :b {:c "ho hum"}} [:b :c])

;; Another way to look up a value in a map
;; is to treat the map like a function with the key as its argument:
({:name "The Human Coffeepot"} :name)

;; Clojure keywords are best understood by seeing how they’re used.
;; They’re primarily used as keys in maps.

;; Keywords can be used as functions that look up the corresponding value in a data structure.
;; For example, you can look up :a in a map:
(:a {:a 1 :b 2 :c 3})
;; => (get {:a 1 :b 2 :c 3} :a)

;; You can provide a default value, as with get:
(:d {:a 1 :b 2 :c 3} "No gnome knows homes like Noah knows")

;; ================================
;; Vectors
;; ================================
;; A vector is similar to an array, in that it’s a 0-indexed collection.
;; You can see that vector elements can be of any type, and you can mix types.
;; Also notice that we’re using the same get function as we use when looking up values in maps.
[3 2 1]
(get [3 2 1] 0)
(get ["a" {:name "Pugsley Winterbottom"} "c"] 1)

;; You can create vectors with the vector function:
(vector "creepy" "full" "moon")

;; You can use the conj function to add additional elements to the vector.
;; Elements are added to the end of a vector:
(conj [1 2 3] 4)

;; ================================
;; Lists
;; ================================

;; Lists are similar to vectors in that they’re linear collections of values.
;; But there are some differences.
;; For example, you can’t retrieve list elements with get.
;; To write a list literal, just insert the elements into parentheses and use a single quote at the beginning:
'(1 2 3 4)

;; If you want to retrieve an element from a list, you can use the nth function:
(nth '(:a :b :c) 0)
(nth '(:a :b :c) 2)

;; It’s good to know that using nth to retrieve an element from a list
;; is slower than using get to retrieve an element from a vector.
;; This is because Clojure has to traverse all n elements of a list to get to the nth,
;; whereas it only takes a few hops at most to access a vector element by its index.

;; List values can have any type, and you can create lists with the list function:
(list 1 "two" {3 4})

;; Elements are added to the beginning of a list:
(conj '(1 2 3) 4)

;; When should you use a list and when should you use a vector?
;; A good rule of thumb is that
;; if you need to easily add items to the beginning of a sequence or if you’re writing a macro,
;; you should use a list.
;; Otherwise, you should use a vector.

;; ================================
;; Sets
;; ================================

;; Sets are collections of unique values.
;; Clojure has two kinds of sets: hash sets and sorted sets.
;; I’ll focus on hash sets because they’re used more often.
#{"kurt vonnegut" 20 :icicle}

;; You can also use hash-set to create a set:
(hash-set 1 1 2 2)

;; Note that multiple instances of a value become one unique value in the set.
;; If you try to add a value to a set that already contains that value,
;; it will still have only one of that value:
(conj #{:a :b} :b)

;; You can also create sets from existing vectors and lists by using the set function:
(set [3 3 3 4 4])
(set '(3 3 3 4 4))

;; You can check for set membership using the contains? function,
;; by using get,
;; or by using a keyword as a function with the set as its argument.
;; contains? returns true or false,
;; whereas get and keyword lookup will return the value if it exists, or nil if it doesn’t.
(contains? #{:a :b} :a)
(contains? #{:a :b} 3)
(contains? #{nil} nil)
(get #{:a :b} :a)
(get #{:a nil} nil)
(get #{:a :b} "kurt vonnegut")
(:a #{:a :b})

;; Notice that using get to test whether a set contains nil will always return nil, which is confusing.
;; contains? may be the better option when you’re testing specifically for set membership.

;; ================================
;; Simplicity
;; ================================

;; You may have noticed that the treatment of data structures so far
;; doesn’t include a description of how to create new types or classes.
;; The reason is that Clojure’s emphasis on simplicity encourages you to reach for the built-in data structures first.

;; It is better to have 100 functions operate on one data structure than 10 functions on 10 data structures.
;; —Alan Perlis
