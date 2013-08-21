# pdfkit-clj

A Clojure library for generating PDFs from HTML. Uses wkhtmltopdf, and insipred by Ruby's pdfkit and [ring-wicked-pdf](https://github.com/gberenfield/ring-wicked-pdf).

## Install

Leiningen installation:

```
[pdfkit-clj "0.1.1"]
```

## Usage

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
(deftemplate my-template
  ...)

(gen-pdf (my-template) ...)
```

## Options:

```clojure
(gen-pdf html
         :asset-path "resources/public"
         :stylesheets ["stylesheets/main.css"
                       "stylesheets/invoices.css"]
         :path "bin/wkhtmltopdf-amd64"
         :tmp "other/tmp")
```

## Images

Right now, pdfkit-clj requires your image tags reference an absolute URL or URI on disk. Simply upload your image to S3, for example, and wkhtmltopdf will have access to it via the file's full URL.

Defaults:

```
:path "wkhtmltopdf"
:tmp "/tmp"
:asset-path "resources/public"
```

## Heroku

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

## License

Copyright © 2013 Banzai Inc.

Distributed under the Eclipse Public License, the same as Clojure.
