(ns clobill.client
  "A mostly generated wrapper for the hostbill API"
  (:require [clojure.string    :as str]
            [clojure.data.json :as json]
            [http.async.client :as http])
  (:import java.net.URLEncoder))

(defn quote-plus
  "Encode URL and replace + signs by %20"
  [s]
  (str/replace (URLEncoder/encode (if (keyword? s) (name s) s)) "+" "%20"))

(defn format-url-args
  "Sanitize given arguments"
  [args]
  (str
   (when (not-empty args)
     (let [format-arg #(format "%s=%s" (name (key %)) (quote-plus (val %)))]
       (str "&" (str/join "&" (map format-arg args)))))))

(defn get-envopt
  "Fetch option either from system properties or environment variables"
  [n]
  (or (System/getProperty n)
      (System/getenv (-> n
                         (str/replace "." "_")
                         str/upper-case))))

(defn api-name
  "Given a hyphenated name, yield a camel case one"
  [s]
  (let [[prelude & rest] (str/split (name s) #"-")
        capitalizer      #(if (#{"pdf" "kb" "api"} %)
                            (str/upper-case %)
                            (str/capitalize %))]
    (apply str prelude (map capitalizer rest))))

(defn parse-response
  "Given a complete HTTP object, deserialize"
  [resp]
  (case (quot (:code (http/status resp)) 100)
    2 (json/read-json (http/string resp))
    4 (http/string resp)
    5 (http/string resp)))

(defprotocol HostbillClient
  (url-for       [this opcode args] "Get a fully formed URL")
  (request       [this opcode args] "Synchronous request to a hostbill instance")
  (async-request [this opcode args] "Asynchronous request to a hostbill instance"))

(defrecord HostbillHTTPClient [api-id api-key endpoint client]
  HostbillClient
  (url-for [this opcode args]
    (format "%s?api_id=%s&api_key=%s&call=%s%s"
            endpoint
            api-id
            api-key
            (name opcode)
            (format-url-args args)))
  (async-request [this opcode args]
    (http/GET client (url-for this opcode args)))
  (request [this opcode args]
    (-> (async-request this opcode args)
        (http/await)
        (parse-response))))

(defn http-client
  "Create an HTTP client"
  [& {:keys [api-id api-key endpoint http]
      :or   {api-id   (get-envopt "hostbill.api.id")
             api-key  (get-envopt "hostbill.api.key")
             endpoint (get-envopt "hostbill.endpoint")
             http     (http/create-client)}}]
  (HostbillHTTPClient. api-id api-key endpoint http))

(defn call-module
  "Utility function to call API functions provided by hostbill modules"
  [client module-opcode args]
  (request client :module (assoc args :fn (api-name module-opcode))))

(defmacro defreq
  {:no-doc true}
  [sym]
  `(do
     (defn ~sym
       "See http://api.hostbillapp.com"
       [~'client & {:as ~'args}]
       (request ~'client ~(api-name sym) ~'args))
     (defn ~(symbol (str "async-" sym))
       "See http://api.hostbillapp.com"
       [~'client & {:as ~'args}]
       (async-request ~'client ~(api-name sym) ~'args))))

(defreq get-api-methods)
(defreq get-host-billversion)
(defreq get-client-details)
(defreq set-client-details)
(defreq get-clients)
(defreq get-client-orders)
(defreq get-client-contacts)
(defreq get-client-stats)
(defreq get-client-accounts)
(defreq get-client-transactions)
(defreq get-client-invoices)
(defreq get-client-domains)
(defreq get-client-emails)
(defreq get-invoices)
(defreq get-invoices-pdf)
(defreq get-invoice-details)
(defreq get-orders)
(defreq get-order-details)
(defreq get-accounts)
(defreq get-account-details)
(defreq get-addons)
(defreq get-addon-details)
(defreq get-domains)
(defreq get-domain-details)
(defreq get-transactions)
(defreq get-transaction-details)
(defreq get-tickets)
(defreq get-ticket-details)
(defreq get-news)
(defreq get-news-item)
(defreq get-kb-categories)
(defreq get-kb-article)
(defreq get-app-groups)
(defreq get-app-servers)
(defreq get-server-details)
(defreq get-order-pages)
(defreq get-products)
(defreq get-product-details)
(defreq get-product-applicable-addons)
(defreq get-product-upgrades)
(defreq get-estimates)
(defreq get-estimate-details)
(defreq get-currencies)
(defreq get-client-tickets)
(defreq get-popular-predefined-replies)
(defreq get-predefined-reply)
(defreq get-predefined-replies)
(defreq get-ticket-depts)
(defreq get-payment-modules)
(defreq set-ticket-status)
(defreq set-ticket-priority)
(defreq set-invoice-status)
(defreq set-estimate-status)
(defreq set-order-pending)
(defreq set-order-active)
(defreq set-order-cancel)
(defreq set-order-fraud)
(defreq account-create)
(defreq account-suspend)
(defreq account-unsuspend)
(defreq account-terminate)
(defreq edit-account-details)
(defreq edit-invoice-details)
(defreq edit-estimate-details)
(defreq delete-ticket)
(defreq delete-invoice)
(defreq delete-estimate)
(defreq delete-client)
(defreq delete-client-contact)
(defreq delete-order)
(defreq send-message)
(defreq send-invoice)
(defreq send-estimate)
(defreq add-ticket-reply)
(defreq add-ticket-notes)
(defreq add-invoice)
(defreq add-estimate)
(defreq add-invoice-item)
(defreq add-invoice-payment)
(defreq add-ticket-dept)
(defreq add-client)
(defreq add-client-contact)
(defreq charge-credit-card)
(defreq edit-client-credit-card)
(defreq add-order)
(defreq order-upgrade)
(defreq order-config-upgrade)
(defreq verify-client-login)
(defreq add-language-lines)
(defreq add-ticket)
(defreq metered-add-usage)
(defreq metered-get-usage)
(defreq metered-get-variables)
(defreq add-client-credit)
(defreq send-mobile-client-notify)
(defreq send-mobile-staff-notify)
(defreq get-client-files)
(defreq add-client-file)
(defreq delete-client-file)
(defreq tokenize-client-card)
(defreq module)