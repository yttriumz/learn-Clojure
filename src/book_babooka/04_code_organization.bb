#!/usr/bin/env bb

;; You can organize your Babashka projects just like your other Clojure projects,
;; splitting your codebase into separate files,
;; with each file defining a namespace
;; and with namespaces corresponding to file names.
;; Let’s reorganize our current codebase a bit,
;; making sure everything still works,
;; and then add a namespace for listing entries.

;; One way to organize our dream journal project would be to create the following file structure:
;;
;; ./journal
;; ./src/journal/add.clj
;; ./src/journal/utils.clj
;;
;; Already, you can see that this looks both similar to typical Clojure project file structures,
;; and a bit different.
;; We’re placing our namespaces in the src/journal directory,
;; which lines up with what you’d see in JVM or ClojureScript projects.
;; What’s different in our Babashka project is that
;; we’re still using ./journal to serve as the executable entry point for our program,
;; rather than the convention of using ./src/journal/core.clj or something like that.
;; This might feel a little weird but it’s valid and it’s still Clojure.

;; And like other Clojure environments,
;; you need to tell Babashka to look in the src directory when you require namespaces.
;; You do that by creating the file bb.edn in the same directory as journal and putting this in it:
;;
;; {:paths ["src"]}

;; bb.edn is similar to a deps.edn file in that
;; one of its responsibilities is telling Babashka how to construct your classpath.
;; The classpath is the set of the directories
;; that Babashka should look in when you require namespaces,
;; and by adding "src" to it you can use (require '[journal.add]) in your project.
;; Babashka will be able to find the corresponding file.

;; Note that there is nothing special about the "src" directory.
;; You could use "my-code" or even "." if you wanted,
;; and you can add more than one path.
;; "src" is just the convention preferred by discerning Clojurians the world over.

;; With this in place, we’ll now update journal so that it looks like this:
(require '[babashka.cli :as cli]
         '[journal.add :as add]
         '[journal.list :as list])

(declare help)

(def spec-add
  {:entry     {:alias   :e
               :desc    "Your dreams."
               :require true}
   :timestamp {:alias  :t
               :desc   "A unix timestamp, when you recorded this."
               :coerce {:timestamp :long}}})

(def spec-list
  {:reverse {:desc "Reverse the order"
             :coerce  :boolean}})

(defn help
  [_]
  (println
   (str "add" "\n"
        (cli/format-opts {:spec spec-add}) "\n"
        "list"))
  ;; (System/exit 1)
  )

(def table
  [;; {:cmds ["add"] :fn add/add-entry :spec cli-opts}
   {:cmds ["add"], :fn #(add/add-entry (:opts %)), :spec spec-add}
   {:cmds ["list"], :fn #(list/list-entries %)}
   {:cmds [], :fn #(do (help %) (System/exit 0))}
   ])

(cli/dispatch table *command-line-args* {:error-fn #(do (help %) (System/exit 1))})
;; Now the file is only responsible for parsing command line arguments
;; and dispatching to the correct function.
;; The add functionality has been moved to another namespace.
