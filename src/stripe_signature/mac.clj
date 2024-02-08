(ns stripe-signature.mac
  (:import (org.apache.commons.codec.binary Hex)
           (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)))


(defn ->secret-key [s mac]
  (SecretKeySpec. (.getBytes s) (.getAlgorithm mac)))

(defn hmac [msg secret]
  (let [mac (Mac/getInstance "HmacSHA256")
        secret-key (->secret-key secret mac)
        msg-bytes (.getBytes msg)
        signed-bytes (.. (doto mac
                           (.init secret-key))
                         (doFinal msg-bytes))]
    (Hex/encodeHexString signed-bytes)))

(defn valid? [msg & {:keys [secret signature]}]
  (= (hmac msg secret) signature))
