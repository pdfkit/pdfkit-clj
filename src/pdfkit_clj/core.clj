(ns pdfkit-clj.core
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer :all]
            [clj-time.local :as local]
            [clj-time.format :as fmt]
            [clojure.string :as string]))

(def ^{:private true} defaults {:tmp "/tmp"
                                :path "wkhtmltopdf"})

(defn- rand-tmp-file-name
  [tmp-dir]
  (str
    tmp-dir "/"
    "pdfkit-"
    (string/replace
      (fmt/unparse (fmt/formatters :basic-date-time)
                   (local/local-now)) #"\." "")
    ".pdf"))

(defn gen-pdf
  "Produces PDF output given an html string."
  [html & {:keys [path tmp]
           :or {path (:path defaults) tmp (:tmp defaults)}}]
  (let [tmp-file-name (rand-tmp-file-name tmp)]
    (sh path "-" tmp-file-name :in html)
    (io/input-stream tmp-file-name)))
