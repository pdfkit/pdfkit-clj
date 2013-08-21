# pdfkit-clj

A Clojure library for generating PDFs from HTML. Uses wkhtmltopdf, and insipred by [ring-wicked-pdf](https://github.com/gberenfield/ring-wicked-pdf).

## Install

Leiningen installation: https://clojars.org/pdfkit-clj.

## Usage

```clojure
(def html "<html><body>Hello!</body></html>")

(gen-pdf html)

#<BufferedInputStream java.io.BufferedInputStream@43bda1e0>
```

Options:

```clojure
(gen-pdf html
         :path "bin/wkhtmltopdf-amd64"
         :tmp "other/tmp")
```

Defaults:

```
:path "wkhtmltopdf"
:tmp "/tmp"
```

## License

Copyright © 2013 Banzai Inc.

Distributed under the Eclipse Public License, the same as Clojure.
