(ns command.core
  (:require [babashka.fs :as fs]
            [babashka.process :refer [shell] :as proc]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            ;; My lib
            [bling.core :refer [bling]]))

(defn- sys-red [& args]
  (->> args
       (apply str)
       (conj [:system-red])
       bling))

(defn- sys-yellow [& args]
  (->> args
       (apply str)
       (conj [:system-yellow])
       bling))

(def default-execute-opts
  {:dir           "."
   :capture-out   false
   :expected-exit 0
   :continue      true})

(defn- execute-args-valid? [opts cmd-vec]
  (cond
    (some nil? (vals opts))              (println (sys-red "[X] Found nil in execute options:") opts)
    (not (fs/exists? (:dir opts)))       (println (sys-red "[X] Execute option :dir does not exist:") (str (fs/absolutize (:dir opts))))
    (not (boolean? (:capture-out opts))) (println (sys-red "[X] Execute option :capture-out must be a boolean."))
    (not (int? (:expected-exit opts)))   (println (sys-red "[X] Execute option :expected-exit must be an integer."))
    (not (boolean? (:continue opts)))    (println (sys-red "[X] Execute option :continue must be a boolean."))
    (empty? cmd-vec)                     (println (sys-red "[X] Command cannot be empty."))
    (empty? (first cmd-vec))             (println (sys-red "[X] Command cannot be empty."))
    :else true))

(defn execute!
  "Executes a command and returns a map with `:exit`, `:expected-exit`, and `:success` status.
  Accepts an optional options map as the first argument.

  Usage:\n
  - `(execute! \"ls\" \"-l\")`
  - `(execute! {:dir \"/tmp\"} \"ls\" \"-l\")`"
  [& args]
  (let [[opts cmd-vec] (if (map? (first args))
                         [(first args) (rest args)]
                         [{} args])
        cmd-str (str/join " " cmd-vec)
        opts (merge default-execute-opts opts)]
    (if-not (execute-args-valid? opts cmd-vec)
      {:success false}
      (try
        (let [{:keys [dir capture-out expected-exit continue]} opts
              shell-opts {:dir dir
                          :out (if capture-out :string :inherit)
                          :continue continue}
              {:keys [exit out]} (apply shell shell-opts cmd-vec)
              return {:success false
                      :exit exit
                      :expected-exit expected-exit
                      :out (if capture-out out nil)}]
          (cond
            (= exit expected-exit)
            (assoc return :success true)

            (and (> exit 128) (< exit 255))
            (do (println (sys-yellow "[!] Likely terminated by SIGTERM " (- exit 128) ":") cmd-str)
                (assoc return :success false))

            :else
            (do (println (sys-red "[X] Exited with code " exit ", expected " expected-exit ":") cmd-str)
                (assoc return :success false))))

        (catch java.io.IOException e
          (println (sys-red "[X] I/O exception during execution:") cmd-str)
          (println (sys-red "    Message:") (ex-message e))
          {:success false :e e})

        (catch Exception e
          (println (sys-red "[X] Execution failed:") cmd-str)
          (println (sys-red "    Exception:"))
          (pprint e)
          {:success false :e e})))))

(defn- debug! [execute-opts & cmd-vec]
  (loop []
    (print (sys-yellow "(debug)> "))
    (flush)
    (let [option (str/trim (read-line))]
      (condp contains? option
        #{"h" "help"}
        (do (println "Available options:\n"
                     " h help   Print this help message.\n"
                     " p print  Print the command and expected exit code.\n"
                     " r redo   Redo the command. If successful, exit debugger.\n"
                     " s shell  Invoke an interactive shell.\n"
                     "   pass   Pass this step and continue the original program.\n"
                     "   fail   Fail this step and continue the original program.\n"
                     " q quit   Quit the whole program.")
            (recur))

        #{"p" "print"}
        (do (println "Execute command:" (str/join " " cmd-vec))
            (println "Execute options:" execute-opts)
            (recur))

        #{"r" "redo"}
        (or (:success (apply execute! execute-opts cmd-vec))
            (recur))

        #{"s" "shell"}
        (do (shell {:continue true} (or (System/getenv "SHELL") "/usr/bin/env bash"))
            (recur))

        #{"pass"} true

        #{"fail"} false

        #{"q" "quit"} (System/exit 1)

        (do (println (str "Unknown option '" option "'. Use 'help' for help."))
            (recur))))))

(def default-attempt-opts
  (merge default-execute-opts
         {:max-attempts 3
          :delay-ms 3000
          :debug true}))

(defn- attempt-args-valid? [opts]
  (cond
    (some nil? (vals opts))                             (println (sys-red "[X] Found nil in attempt options:") opts)
    (not ((every-pred int? pos?) (:max-attempts opts))) (println (sys-red "[X] Attempt option :max-attempts must be a positive integer."))
    (not ((every-pred int? pos?) (:delay-ms opts)))     (println (sys-red "[X] Attempt option :delay-ms must be a positive integer."))
    (not (boolean? (:debug opts)))                      (println (sys-red "[X] Attempt option :debug must be a boolean."))
    :else true))

(defn attempt!
  "Attempts a command up to :max-attempts times.
   Returns `true` if success or manual pass in debugger."
  [& args]
  (let [[opts cmd-vec] (if (map? (first args))
                         [(first args) (rest args)]
                         [{} args])
        cmd-str (str/join " " cmd-vec)
        opts (merge default-attempt-opts opts)]
    (when (attempt-args-valid? opts)
      (let [{:keys [dir expected-exit continue max-attempts delay-ms debug]} opts
            execute-opts {:dir dir
                          :capture-out false
                          :expected-exit expected-exit
                          :continue continue}]
        (loop [attempt 1]
          (println (str "[*] Attempting (" attempt "/" max-attempts "): " cmd-str))
          (or (:success (apply execute! execute-opts cmd-vec))
              (if (< attempt max-attempts)
                (do (println "[*] Retry in" (float (/ delay-ms 1000)) "seconds.")
                    (Thread/sleep delay-ms)
                    (recur (inc attempt)))
                (do (println (sys-yellow "[!] Max attempts reached."))
                    (when debug
                      (println "[*] Invoking debugger. Use 'help' for help.")
                      (apply debug! execute-opts cmd-vec))))))))))
