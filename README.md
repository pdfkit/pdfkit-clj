# pdfkit-clj

A Clojure library for generating PDFs from HTML. Uses wkhtmltopdf, and insipred by Ruby's [pdfkit](https://github.com/pdfkit/pdfkit) and [ring-wicked-pdf](https://github.com/gberenfield/ring-wicked-pdf).

### Install

Leiningen installation:

```
[pdfkit-clj "0.1.5"]
```

### Usage

```clojure
(require '[pdfkit-clj.core :refer :all])
(def html "<html><body>Hello!</body></html>")

(gen-pdf html)

#<File /tmp/pdfkit-20130821T161228935Z.pdf>
```

You can also convert your file to an InputStream, ready for consumption by a browser (helpful for Ring applications):

```clojure
(as-stream (gen-pdf html))

#<BufferedInputStream java.io.BufferedInputStream@43bda1e0>
```

pdfkit-clj's `gen-pdf` can also accept HTML nodes (e.g. Enlive):

```clojure
(defsnippet my-snippet
  ...)

(gen-pdf (my-snippet) ...)
```

### Options:

```clojure
(gen-pdf html
         :asset-path "public" ; Relative to your "resources" directory
         :stylesheets ["stylesheets/main.css"
                       "stylesheets/invoices.css"]
         :path "bin/wkhtmltopdf-amd64"
         :margin {:top 20 :right 15 :bottom 50 :left 15}
         :tmp "other/tmp")
```

#### Defaults:

```clojure
:path "wkhtmltopdf"
:tmp "/tmp"
:asset-path "resources/public"
:margin {:top 10 :right 10 :bottom 10 :left 10} ;; in mm
```

### Images

Right now, pdfkit-clj requires your image tags reference an absolute URL or URI on disk. Simply upload your image to S3, for example, and wkhtmltopdf will have access to it via the file's full URL.

### Heroku

If you're like me, everything must work on Heroku. Here's the setup:

#### 1. Download wkhtmltopdf to the `./bin` directory of your Leiningen project.

```
mkdir bin
cd bin
wget https://wkhtmltopdf.googlecode.com/files/wkhtmltopdf-0.9.9-static-amd64.tar.bz2
```

Finally, Unzip the file and rename it to `wkhtmltopdf`.

#### 2. Call `gen-pdf` with appropriate paths:

```clojure
(gen-pdf html :path "bin/wkhtmltopdf")
```

### License

Copyright © 2013 Banzai Inc.

Distributed under the Eclipse Public License, the same as Clojure.
