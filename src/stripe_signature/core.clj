(ns stripe-signature.core
  (:require
   [clojure.string :as str]
   [stripe-signature.mac :as mac]))


(defn parse-signature [signature]
  (reduce (fn [acc s]
            (let [[k v] (str/split s #"=")]
              (assoc acc (keyword k) v)))
          {}
          (str/split signature #",")))

(defn -valid? [t v1 msg secret]
  (let [signed-payload (str t "." msg)]
    (mac/valid? signed-payload
                    :signature v1
                    :secret secret)))

(defn valid?
  "Compares the `signature` with the `message` hashed using the `secret`.
  Optionally checks that timestamp extracted from signature is newer than given
  one."
  [message {:keys [secret signature timestamp]}]
  (let [{:keys [v1 t]} (parse-signature signature)]
    (and (-valid? t v1 message secret)
         (or (nil? timestamp)
             (> (parse-long t) timestamp)))))
