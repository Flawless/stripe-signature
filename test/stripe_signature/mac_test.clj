(ns stripe-signature.mac-test
  (:require
   [clojure.test :refer [deftest is]]
   [stripe-signature.mac :as subject]))

(deftest valid?-test
  (is (subject/valid? "The quick brown fox jumps over the lazy dog"
                      {:signature "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
                       :secret "key"}))
  (is (not
       (subject/valid? "The quick brown fox jumps over the lazy dog"
                       {:signature "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
                        :secret "invalid-key"}))))

(deftest hmac-test
  (is (= "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
         (subject/hmac "The quick brown fox jumps over the lazy dog"
                       "key")))
  (is (= "e3f1badad57012b97d005d55f346c435541296a30dfa9f77d4f83e356eb04e96"
         (subject/hmac "signed-message" "secret-key"))))
