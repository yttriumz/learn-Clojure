(ns ch4-1-abstraction)

;; ================================
;; The Sequence Abstraction
;; ================================

;; elisp (Emacs Lisp) uses two different, data structure–specific functions
;; to implement the map operation,
;; but Clojure uses only one.
;; You can also call reduce on a map in Clojure,
;; whereas elisp doesn’t provide a function for reducing a hash map.

;; The reason is that Clojure defines map and reduce functions in terms of the sequence abstraction,
;; not in terms of specific data structures.
;; As long as a data structure responds to the core sequence operations
;; (the functions first, rest, and cons),
;; it will work with map, reduce, and oodles of other sequence functions for free.
;; This is what Clojurists mean by programming to abstractions,
;; and it’s a central tenet of Clojure philosophy.

;; MY NOTE: Similar to traits in Rust?

;; Similarly, map doesn’t care about how lists, vectors, sets, and maps are implemented.
;; It only cares about whether it can perform sequence operations on them.
;; Let’s look at how map is defined in terms of the sequence abstraction
;; so you can understand programming to abstractions in general.

;; ================================
;; The Sequence Abstraction - Treating Lists, Vectors, Sets, and Maps as Sequences
;; ================================

;; If you think about the map operation independently of any programming language,
;; or even of programming altogether,
;; its essential behavior is to derive a new sequence y
;; from an existing sequence x
;; using a function ƒ such that y1 = ƒ (x1), y2 = ƒ (x2), . . . yn = ƒ (xn) .

;; The term sequence here refers to
;; a collection of elements organized in linear order,
;; as opposed to, say, an unordered collection
;; or a graph without a before-and-after relationship between its nodes.

;; Absent from this description of mapping and sequences
;; is any mention of lists, vectors, or other concrete data structures.
;; Clojure is designed to allow us to think and program in such abstract terms as much as possible,
;; and it does this by implementing functions in terms of data structure abstractions.
;; In this case, map is defined in terms of the sequence abstraction.
;; In conversation, you would say map, reduce, and other sequence functions take a sequence or even take a seq.
;; In fact, Clojurists usually use seq instead of sequence,
;; using terms like seq functions and the seq library
;; to refer to functions that perform sequential operations.
;; Whether you use sequence or seq,
;; you’re indicating that the data structure in question will be treated as a sequence
;; and that what it actually is in its truest heart of hearts doesn’t matter in this context.

;; If the core sequence functions first, rest, and cons work on a data structure,
;; you can say the data structure implements the sequence abstraction.
;; Lists, vectors, sets, and maps all implement the sequence abstraction,
;; so they all work with map, as shown here:
(defn titleize
  [topic]
  (str topic " for the Brave and True"))

(map titleize ["Hamsters" "Ragnarok"])
(map titleize '("Empathy" "Decorating"))
(map titleize #{"Elbows" "Soap Carving"})
(map #(titleize (second %)) {:uncomfortable-thing "Winking"})

;; MY NOTE: Notice the use of conj and cons.
(conj '(1 2 3) 10)
(cons 10 '(1 2 3))
(conj [1 2 3] 10)
(cons 10 [1 2 3])

;; ================================
;; The Sequence Abstraction - Abstraction Through Indirection
;; ================================

;; How is a function like first able to work with different data structures.
;; Clojure does this using two forms of indirection.
;; In programming, indirection is a generic term for the mechanisms a language employs
;; so that one name can have multiple, related meanings.
;; In this case, the name first has multiple, data structure–specific meanings.
;; Indirection is what makes abstraction possible.

;; Polymorphism is one way that Clojure provides indirection.
;; Basically, polymorphic functions dispatch to different function bodies
;; based on the type of the argument supplied.
;; (It’s not so different from how multiple-arity functions dispatch to different function bodies
;; based on the number of arguments you provide.)

;; Note	Clojure has two constructs for defining polymorphic dispatch:
;; the host platform’s interface construct
;; and platform-independent protocols.

;; When it comes to sequences,
;; Clojure also creates indirection by doing a kind of lightweight type conversion,
;; producing a data structure that works with an abstraction’s functions.
;; Whenever Clojure expects a sequence—
;; for example, when you call map, first, rest, or cons—
;; it calls the seq function on the data structure in question
;; to obtain a data structure that allows for first, rest, and cons:
(seq '(1 2 3))
(seq [1 2 3])
(seq #{1 2 3})
(seq {:name "Bill Compton" :occupation "Dead mopey guy"})
(seq "Clojure")
;; There are two notable details here.
;; First, seq always returns a value that looks and behaves like a list;
;; you’d call this value a sequence or seq.
;; Second, the seq of a map consists of two-element key-value vectors.
;; That’s why map treats your maps like lists of vectors!

;; The takeaway here is that
;; it’s powerful to focus on what we can do with a data structure
;; and to ignore, as much as possible, its implementation.
;; Implementations rarely matter in and of themselves.
;; They’re just a means to an end.
;; In general, programming to abstractions gives you power
;; by letting you use libraries of functions on different data structure
;; regardless of how those data structures are implemented.

;; ================================
;; The Collection Abstraction
;; ================================

;; The collection abstraction is closely related to the sequence abstraction.
;; All of Clojure’s core data structures—vectors, maps, lists, and sets—take part in both abstractions.

;; The sequence abstraction is about operating on members individually,
;; whereas the collection abstraction is about the data structure as a whole.
;; For example, the collection functions count, empty?, and every? aren’t about any individual element;
;; they’re about the whole:
(empty? [])
(empty? ["no!"])

;; ================================
;; The Collection Abstraction - into
;; ================================

;; One of the most important collection functions is into,
;; which is great at taking two collections and adding all the elements from the second to the first.
;; As you now know, many seq functions return a seq rather than the original data structure.
;; You’ll probably want to convert the return value back into the original value,
;; and into lets you do that:
(map identity {:sunlight-reaction "Glitter!"})
(into {} (map identity {:sunlight-reaction "Glitter!"}))

;; This will work with other data structures as well:
(map identity [:garlic :sesame-oil :fried-eggs])
(into [] (map identity [:garlic :sesame-oil :fried-eggs]))

;; In the following example, we start with a vector with two identical entries,
;; map converts it to a list,
;; and then we use into to stick the values into a set.
(map identity [:garlic-clove :garlic-clove])
(into #{} (map identity [:garlic-clove :garlic-clove]))

;; The first argument of into doesn’t have to be empty.
;; Here, the first example shows how you can use into to add elements to a map,
;; and the second shows how you can add elements to a vector.
(into {:favorite-emotion "gloomy"} [[:sunlight-reaction "Glitter!"]])
(into ["cherry"] '("pine" "spruce"))
(into ["cherry"] "pine")

;; ================================
;; The Collection Abstraction - conj
;; ================================

;; conj also adds elements to a collection, but it does it in a slightly different way:
(conj [0] [1])
(into [0] [1])
(conj [0] 1)
;; Notice that the number 1 is passed as a scalar (singular, non-collection) value,
;; whereas into’s second argument must be a collection.

;; You can supply as many elements to add with conj as you want,
;; and you can also add to other collections like maps:
(conj [0] 1 2 3 4)
(conj {:time "midnight"} [:place "ye olde cemetarium"])

;; conj and into are so similar that you could even define conj in terms of into:
(defn my-conj
  [target & additions]
  (into target additions))

(my-conj [0] 1 2 3)
(my-conj [0] [1 2 3])

;; This kind of pattern isn’t that uncommon.
;; You’ll often see two functions that do the same thing,
;; except one takes a rest parameter (conj) and one takes a seqable data structure (into).