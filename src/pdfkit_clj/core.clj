(ns pdfkit-clj.core
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer :all]
            [clj-time.local :as local]
            [clj-time.format :as fmt]
            [clojure.string :as string]
            [net.cgrand.enlive-html :as e])
  (:import [org.apache.commons.lang3 StringEscapeUtils]))

(def ^{:private true} defaults {:tmp "/tmp"
                                :path "wkhtmltopdf"
                                :asset-path "public"
                                :margin {:top 10
                                         :right 10
                                         :bottom 10
                                         :left 10}})

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
  [stylesheets]
  (apply str (map #(slurp %) stylesheets)))

(defn- append-styles
  "Appends stylesheets to the HTML's head tag."
  [html stylesheets]
  (let [styles (concat-styles stylesheets)]
    (e/at html
          [:head] (e/append (e/html [:style styles])))))

(defmulti html-as-nodes class)

(defmethod html-as-nodes String
  [html]
  (e/html-snippet html))

(defmethod html-as-nodes :default [html] html)

(defn- html-as-string
  [html]
  (StringEscapeUtils/unescapeXml
    (StringEscapeUtils/escapeHtml4
      (apply str (e/emit* html)))))

(defn- top* [margin] (str (:top margin)))
(defn- right* [margin] (str (:right margin)))
(defn- bottom* [margin] (str (:bottom margin)))
(defn- left* [margin] (str (:left margin)))

(defn gen-pdf
  "Produces a PDF file given an html string."
  [html & {:keys [path tmp asset-path stylesheets margin]
           :or {path (:path defaults) tmp (:tmp defaults)
                asset-path (:asset-path defaults)
                margin {}}}]
  (let [margin (merge (:margin defaults) margin)
        tmp-file-name (rand-tmp-file-name tmp)
        stylesheets (map #(io/resource (str asset-path "/" %))
                         stylesheets)
        html (-> html
                 (html-as-nodes)
                 (append-styles stylesheets)
                 (html-as-string))]
    (sh path
        "-T" (top* margin) "-R" (right* margin)
        "-B" (bottom* margin) "-L" (left* margin)
        "-" tmp-file-name :in html)
    (io/as-file tmp-file-name)))

(defn as-stream
  "Given a file, returns PDF as stream. Helpful for Ring applications."
  [f]
  (io/input-stream f))

;; (def html "<html><head></head><body>Ugly&nbsp;&nbsp;Joe Nobody!&trade;</body></html>")
;; (sh "open" (str (gen-pdf html
;;                          :stylesheets ["stylesheets/test.css" "stylesheets/test_1.css"]
;;                          :margin {:top 50 :left 30})))
