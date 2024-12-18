(ns journal.list
  (:require
   [journal.utils :as utils])
  (:import java.text.SimpleDateFormat
           java.util.Date))

(defn list-entries-raw
  [_]
  (let [entries (utils/read-entries)]
    (doseq [{:keys [timestamp entry]} (reverse entries)]
      (println timestamp)
      (println entry "\n"))))

(defn list-entries
  [_]
  (let [entries (utils/read-entries)]
    (doseq [{:keys [timestamp entry]} entries]
      ;; (println (SimpleDateFormat/.format simpleDateFormat (Date. timestamp)))
      ;; (println (. simpleDateFormat format (Date. timestamp)))
      ;; (println (. simpleDateFormat (format (Date. timestamp))))
      (println "=="
               (->> timestamp
                    Date.
                    format
                    (. (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss,SSS"))))
      (println entry "\n"))))
