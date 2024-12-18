(ns ch3-exercises)

;; ================================
;; E1
;; ================================

(str "Hello" " " "world" "!")
(vector :a :b :c)
(list :a :b :c)
(hash-map :a 1 :b 2)
(hash-set :a :b :c)

;; ================================
;; E2
;; ================================

(defn add100
  [num]
  (+ num 100))

;; ================================
;; E3
;; ================================

(defn dec-maker
  [num]
  #(- % num))

(def dec9 (dec-maker 9))
(dec9 10)

;; ================================
;; E4
;; ================================

(defn mapset
  [f args]
  (set (map f args)))

(mapset inc [1 1 2 2])

(defn mapset'
  [& args]
  (set (apply map args)))

(mapset' inc [1 1 2 2])
