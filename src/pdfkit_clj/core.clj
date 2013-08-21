(ns pdfkit-clj.core
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer :all]
            [clj-time.local :as local]
            [clj-time.format :as fmt]
            [clojure.string :as string]
            [net.cgrand.enlive-html :as e]))

(def ^{:private true} defaults {:tmp "/tmp"
                                :path "wkhtmltopdf"
                                :asset-path "resources/public"})

(defn- rand-tmp-file-name
  [tmp-dir]
  (str tmp-dir "/"
       "pdfkit-"
       (string/replace
         (fmt/unparse (fmt/formatters :basic-date-time)
                      (local/local-now)) #"\." "")
       ".pdf"))

(defn- concat-styles
  "Takes a list of files and produces a single stylesheet."
  [stylesheets absolute-path]
  (apply str (map #(slurp (str absolute-path "/" %))
                  stylesheets)))

(defn- append-styles
  "Appends stylesheets to the HTML's head tag."
  [html stylesheets asset-path]
  (let [p (str (System/getProperty "user.dir") "/" asset-path)
        styles (concat-styles stylesheets p)]
    (e/at html
          [:head] (e/content (e/html [:style styles])))))

(defmulti html-as-nodes class)

(defmethod html-as-nodes String
  [html]
  (e/html-resource (java.io.StringReader. html)))

(defmethod html-as-nodes :default [html] html)

(defn- html-as-string
  [html]
  (apply str (e/emit* html)))

(defn gen-pdf
  "Produces a PDF file given an html string."
  [html & {:keys [path tmp asset-path stylesheets]
           :or {path (:path defaults) tmp (:tmp defaults)
                asset-path (:asset-path defaults)}}]
  (let [tmp-file-name (rand-tmp-file-name tmp)
        html (-> html
                 (html-as-nodes)
                 (append-styles stylesheets asset-path)
                 (html-as-string))]
    (sh path "-" tmp-file-name :in html)
    (io/as-file tmp-file-name)))

(defn as-stream
  "Given a file, returns PDF as stream. Helpful for Ring applications."
  [f]
  (io/input-stream f))

;; (def html "<html><head></head><body>Ugly Joe Nobody!</body></html>")
;; (def stylesheets ["stylesheets/test.css" "stylesheets/test_1.css"])
;; (def asset-path (:asset-path defaults))

;; (sh "open" (str (gen-pdf html :stylesheets stylesheets)))
