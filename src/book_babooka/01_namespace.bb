;; Namespace is optional.
;; As for the lack of namespace: this is part of what makes Babashka useful as a scripting tool.
;; When you’re in a scripting state of mind,
;; you want to start hacking on ideas immediately;
;; you don’t want to have to deal with boilerplate just to get started.
;; Babashka has your babacka.

;; You can define a namespace
;; (we’ll look at that more when we get into project organization),
;; but if you don’t then Babashka uses the user namespace by default.
(prn (str "Hello from " *ns* ", inner world!"))
;; Running it will print "Hello from user, inner world!".
;; This might be surprising because there’s a mismatch between filename and namespace name.
;; In other Clojure implementations,
;; the current namespace strictly corresponds to the source file’s filename,
;; but Babashka relaxes that a little bit in this specific context.
;; It provides a scripting experience
;; that’s more in line with what you’d expect from using other scripting languages.

;; You might want to include a namespace declaration
;; because you want to require some namespaces.
;; With JVM Clojure and Clojurescript, you typically require namespaces like this:
;;
;; (ns user
;;   (:require
;;    [clojure.string :as str]))
;;
;; It’s considered bad form
;; to require namespaces by putting (require '[clojure.string :as str]) in your source code.
;; That’s not the case with Babashka.
;; You’ll see (require …​) used liberally in other examples,
;; and it’s OK for you to do that too.
