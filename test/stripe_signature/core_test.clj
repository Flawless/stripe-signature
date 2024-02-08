(ns stripe-signature.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [stripe-signature.core :as subject]))

(deftest parse-signature-test
  (let [sample-signature "t=1707319738,v1=8bcdd41069009b95b7c3b51c18b651e664599fede1ca3898ee5686a3f1e0308b,v0=51d972275b4d9f5eb0b0ee7710810f3f0856215d423ddef48c96f7a99d69534b"
        expected {:t "1707319738"
                  :v1 "8bcdd41069009b95b7c3b51c18b651e664599fede1ca3898ee5686a3f1e0308b"
                  :v0 "51d972275b4d9f5eb0b0ee7710810f3f0856215d423ddef48c96f7a99d69534b"}
        actual (subject/parse-signature sample-signature)]
    (is (= expected actual))))

(deftest valid?-test
  (let [sample-message "{\n  \"id\": \"evt_3OhTXaAlrmzb9fVx0uUvoySG\",\n  \"object\": \"event\",\n  \"api_version\": \"2023-10-16\",\n  \"created\": 1707383106,\n  \"data\": {\n    \"object\": {\n      \"id\": \"pi_3OhTXaAlrmzb9fVx06CjBZ4v\",\n      \"object\": \"payment_intent\",\n      \"amount\": 2000,\n      \"amount_capturable\": 0,\n      \"amount_details\": {\n        \"tip\": {\n        }\n      },\n      \"amount_received\": 0,\n      \"application\": null,\n      \"application_fee_amount\": null,\n      \"automatic_payment_methods\": null,\n      \"canceled_at\": null,\n      \"cancellation_reason\": null,\n      \"capture_method\": \"automatic\",\n      \"client_secret\": \"pi_3OhTXaAlrmzb9fVx06CjBZ4v_secret_Nj5jLetnS4F4UqqXY06MzE7zj\",\n      \"confirmation_method\": \"automatic\",\n      \"created\": 1707383106,\n      \"currency\": \"usd\",\n      \"customer\": null,\n      \"description\": \"(created by Stripe CLI)\",\n      \"invoice\": null,\n      \"last_payment_error\": null,\n      \"latest_charge\": null,\n      \"livemode\": false,\n      \"metadata\": {\n      },\n      \"next_action\": null,\n      \"on_behalf_of\": null,\n      \"payment_method\": null,\n      \"payment_method_configuration_details\": null,\n      \"payment_method_options\": {\n        \"card\": {\n          \"installments\": null,\n          \"mandate_options\": null,\n          \"network\": null,\n          \"request_three_d_secure\": \"automatic\"\n        }\n      },\n      \"payment_method_types\": [\n        \"card\"\n      ],\n      \"processing\": null,\n      \"receipt_email\": null,\n      \"review\": null,\n      \"setup_future_usage\": null,\n      \"shipping\": {\n        \"address\": {\n          \"city\": \"San Francisco\",\n          \"country\": \"US\",\n          \"line1\": \"510 Townsend St\",\n          \"line2\": null,\n          \"postal_code\": \"94103\",\n          \"state\": \"CA\"\n        },\n        \"carrier\": null,\n        \"name\": \"Jenny Rosen\",\n        \"phone\": null,\n        \"tracking_number\": null\n      },\n      \"source\": null,\n      \"statement_descriptor\": null,\n      \"statement_descriptor_suffix\": null,\n      \"status\": \"requires_payment_method\",\n      \"transfer_data\": null,\n      \"transfer_group\": null\n    }\n  },\n  \"livemode\": false,\n  \"pending_webhooks\": 3,\n  \"request\": {\n    \"id\": \"req_tLfXXpxWN0MvJi\",\n    \"idempotency_key\": \"d5a3a415-14ce-4b1a-8fa5-48ec450d4358\"\n  },\n  \"type\": \"payment_intent.created\"\n}"

        signature "t=1707383108,v1=263d65da7eee0ea7fc54f5484db3d2f79b26faf45af4c63b7573be6b58501ff7,v0=f83d020881d8f1b06520d4030ba8fd36c6e4a4de478458ee913fd607e75059d7"

        secret "whsec_57ca0ed9de01fd784e284584a8b32c980601adf07edd47947e0d527e91837344"]
    (testing "valid signature"
      (is (subject/valid? sample-message
                          {:signature signature :secret secret})))
    (testing "invalid signature"
      (is (not (subject/valid? sample-message
                               {:signature signature :secret "invalid key"}))))
    (testing "message is too old"
      (is (not (subject/valid? sample-message
                               {:signature signature
                                :secret secret
                                :timestamp 1707420000}))))
    (testing "message is not too old"
      (is (subject/valid? sample-message
                          {:signature signature
                           :secret secret
                           :timestamp 1707319700})))))
