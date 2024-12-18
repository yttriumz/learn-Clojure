#!/usr/bin/env bb

;; Our journal file is at the point where we can add an entry by calling
;; ./02_file_io.clj "Flying\!\! But to Home Depot??".
;; This is almost what we want; we actually want to call
;; ./02_file_io.clj add --entry "Flying\!\! But to Home Depot??".
;; The assumption here is that we’ll want to have other commands
;; like ./journal list or ./journal delete.
;; ??? (You have to escape the exclamation marks otherwise bash interprets them as history commands.)

;; To accomplish this, we’ll need to handle the command line arguments in a more sophisticated way.
;; The most obvious and least-effort way to do this would be
;; to dispatch on the first argument to *command-line-args*, something like this:
;;
;; (let [[command _ entry] *command-line-args*]
;;   (case command
;;     "add" (add-entry entry)))
;;
;; This might be totally fine for your use case,
;; but sometimes you want something more robust. You might want your script to:
;; - List valid commands
;; - Give an intelligent error message when a user calls a command that doesn’t exist
;;     (e.g. if the user calls ./journal add-dream instead of ./journal add)
;; - Parse arguments, recognizing option flags
;;     and converting values to keywords, numbers, vectors, maps, etc

;; Generally speaking, you want a clear and consistent way to define an interface for your script.
;; This interface is responsible for
;; taking the data provided at the command line — arguments passed to the script,
;; as well as data piped in through stdin — and using that data to handle these three responsibilities:
;; - Dispatching to a Clojure function
;; - Parsing command-line arguments into Clojure data, and passing that to the dispatched function
;; - Providing feedback in cases where there’s a problem performing the above responsibilities.

;; The broader Clojure ecosystem provides at least two libraries for handling argument parsing:
;; - clojure.tools.cli
;; - nubank/docopt.clj

;; Babashka provides the babashka.cli library for both parsing options and dispatches subcommands.
;; We’re going to focus just on babashka.cli.

;; ================================
;; parsing options with babashka.cli
;; ================================

;; The babashka.cli docs do a good job of
;; explaining how to use the library to meet all your command line parsing needs.
;; Rather than going over every option,
;; I’ll just focus on what we need to build our dream journal.
;; To parse options, we require the babashka.cli namespace and we define a CLI spec:
;;
;; (require '[babashka.cli :as cli])
;; (def cli-opts
;;   {:entry     {:alias   :e
;;                :desc    "Your dreams."
;;                :require true}
;;    :timestamp {:alias  :t
;;                :desc   "A unix timestamp, when you recorded this."
;;                :coerce {:timestamp :long}}})

;; A CLI spec is a map where each key is a keyword,
;; and each value is an option spec.
;; This key is the long name of your option;
;; :entry corresponds to the flag --entry on the command line.

;; The option spec is a map you can use to further config the option.
;; :alias lets you specify a short name for you options,
;; so that you can use e.g. -e instead of --entry at the command line.
;; :desc is used to create a summary for your interface,
;; and :require is used to enforce the presence of an option.
;; :coerce is used to transform the option’s value into some other data type.

;; We can experiment with this CLI spec in a REPL.
;; There are many options for starting a Babashka REPL,
;; and the most straightforward is simply typing bb repl at the command line.
;; If you want to use CIDER,
;; first add the file bb.edn and put an empty map, {}, in it.
;; Then you can use cider-jack-in.
;; After that, you can paste in the code from the snippet above, then paste in this snippet:
;;
;; (cli/parse-opts ["-e" "The more I mowed, the higher the grass got :("] {:spec cli-opts})
;; => {:entry "The more I mowed, the higher the grass got :("}

;; Note that cli/parse-opts returns a map with the parsed options,
;; which will make it easy to use the options later.

;; Leaving out a required flag throws an exception:
;;
;; (cli/parse-opts [] {:spec cli-opts})
;;
;; exception gets thrown, this gets printed:
;; : Required option: :entry user

;; cli/parse-opts is a great tool for building an interface for simple scripts!
;; You can communicate that interface to the outside world with cli/format-opts.
;; This function will take an option spec
;; and return a string that you can print to aid people in using your program.
;; Behold:
;;
;; (println (cli/format-opts {:spec cli-opts}))
;; =>
;; -e, --entry     Your dreams.
;; -t, --timestamp A unix timestamp, when you recorded this.

;; ================================
;; dispatching subcommands with babashka.cli
;; ================================

;; babashka.cli goes beyond option parsing to also giving you a way to dispatch subcommands,
;; which is exactly what we want to get ./journal add --entry "…​" working.
;; Here’s the final version of journal:
(require '[babashka.cli :as cli])
(require '[babashka.fs :as fs])

(def ENTRIES-LOCATION "data/entries.edn")

(defn read-entries
  []
  (if (fs/exists? ENTRIES-LOCATION)
    (edn/read-string (slurp ENTRIES-LOCATION))
    []))

(defn add-entry
  [{opts :opts}]
  (let [entries (read-entries)]
    (spit ENTRIES-LOCATION
          (conj entries
                (merge {:timestamp (System/currentTimeMillis)} ;; default timestamp
                       opts)))))

(def cli-opts
  {:entry     {:alias   :e
               :desc    "Your dreams."
               :require true}
   :timestamp {:alias  :t
               :desc   "A unix timestamp, when you recorded this. Defaults to now."
               :coerce {:timestamp :long}}})

(defn help
  [_]
  (println
   (str "add\n"
        (cli/format-opts {:spec cli-opts}))))

(def table
  [{:cmds ["add"] :fn add-entry :spec cli-opts}
   {:cmds [] :fn help}])

(cli/dispatch table *command-line-args*)
;; Try it out with the following at your terminal:
;;
;; ./journal
;; ./journal add -e "dreamt they did one more episode of Firefly, and I was in it"
;;
;; The function cli/dispatch at the bottom takes a dispatch table as its first argument.
;; cli/dispatch figures out
;; which of the arguments you passed in at the command line correspond to commands,
;; and then calls the corresponding :fn.
;; If you type ./journal add …​, it will dispatch the add-entry function.
;; If you just type ./journal with no arguments, then the help function gets dispatched.

;; !!! The dispatched function receives a map as its argument,
;; and that map contains the :opts key.
;; This is a map of parsed command line options,
;; and we use it to build our dream journal entry in the add-entry function.

;; And that, my friends, is how you build an interface for your script!
