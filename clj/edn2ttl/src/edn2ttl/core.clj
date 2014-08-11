(ns edn2ttl.core
  (require [clojure.edn :as edn]
           [stencil.core :as tmpl])
  (:gen-class :main true))

(defn uuid
  "Generates random UUID for pdgm terms"
  []
  (str (java.util.UUID/randomUUID)))

(defn do-prelude
  [inputfile pdgm-map]
  (println "\n#TTL FROM INPUT FILE:\n#" inputfile)
  (let [lang (name (pdgm-map  :lang))
        Lang (clojure.string/capitalize lang)
        sgpref (pdgm-map :sgpref)]
    (tmpl/render-string (str "@prefix rdf:	 <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
                             "@prefix rdfs:	 <http://www.w3.org/2000/01/rdf-schema#> .\n"
                             "@prefix aama:	 <http://id.oi.uchicago.edu/aama/2013/> .\n"
                             "@prefix aamas:	 <http://id.oi.uchicago.edu/aama/2013/schema/> .\n"
                             "@prefix {{s}}   <http://id.oi.uchicago.edu/aama/2013/{{lang}}/> .\n")
                        {:s sgpref :lang lang})
    (tmpl/render-string (str (newline)
                             "#SCHEMATA:\n"
                             "aama:{{lang}} a aamas:Language .\n"
                             "aama:{{lang}} rdfs:label \"{{lang}}\" .\n")
                        {:lang Lang})))

(defn do-props
  [schemata]
  (doseq [[property valuelist] schemata]
    (let [prop (name property)
          Prop (clojure.string/capitalize prop)]
          ;; NB clojure.string/capitalize gives  wrong output with
          ;; terms like conjClass: =>Conjclass rather than ConjClass

    (tmpl/render-string (str
                         (newline)
                         "#SCHEMATA: {{prop}}\n"
                         "{{pfx}}:{{prop}} aamas:lang aama:{{Lang}} .\n"
                         "{{pfx}}:{{Prop}} aamas:lang aama:{{Lang}} .\n"
                         "{{pfx}}:{{prop}} rdfs:domain aamas:Term .\n"
                         "{{pfx}}:{{Prop}} rdfs:label \"{{prop}} exponents\" .\n"
                         "{{pfx}}:{{prop}} rdfs:label \"{{prop}}\" .\n"
                         "{{pfx}}:{{prop}} rdfs:range {{prop}}:{{Prop}} .\n"
                         "{{pfx}}:{{Prop}} rdfs:subClassOf {{pfx}}:MuExponent .\n"
                         "{{pfx}}:{{prop}} rdfs:subPropertyOf {{pfx}}:muProperty .\n")
                        {:pfx sgpref
                         :Lang Lang
                         :prop prop
                         :Prop Prop})))

    (def vallist valuelist)
    (doseq [value vallist]
      (def valname (name value))
      (def y ( map println [
                            (format "%s:%s aamas:lang aama:%s ." sgpref valname Lang)
                            (format "%s:%s rdf:type %s:%s ." sgpref valname sgpref Prop)
                            (format "%s:%s rdfs:label \"%s\"." sgpref valname valname)
                            ])
        )
      (doall y)
      )
    )

(defn do-morphemes
  [morphemes]
  (println	(format "\n#MORPHEMES:\n"))
  (doseq [[morpheme featurelist] morphemes]
    (def morph (name morpheme))
    (def x ( map println [
                          (format "aama:%s-%s a aamas:Muterm ;" Lang morph)
                          (format "\taamas:lang aama:%s ;" Lang)
                          (format "\trdfs:label \"%s\" ;" morph)
                          ]))
    (doall  x)
    (doseq [[feature value] featurelist]
      (def mprop (name feature))
      (def mval (name value))
      (def y ( map println [
                            (cond (= mprop "gloss")
                                  (format "\trdfs:comment \"%s\" ;" mval)
                                  (= mprop "lemma")
                                  (format "\trdfs:comment \"%s\" ;" mval)
                                  (re-find #"^\"" mval)
                                  (format "\t%s:%s \"%s\" ;" sgpref mprop mval)
                                  :else
                                  (format "\t%s:%s %s:%s ;" sgpref mprop sgpref mval)
                                  )
                            ]))
      (doall y))
    (println "\t.")
    ))

(defn do-lexemes
  [lexemes]
  (println	(format "\n#LEXEMES:\n"))
  (doseq [[lexeme featurelist] lexemes]
    (def lex (name lexeme))
    (def x ( map println [
                          (format "aama:%s-%s a aamas:Lexeme ;" Lang lex)
                          (format "\taamas:lang aama:%s ;" Lang)
                          (format "\trdfs:label \"%s\" ;" lex)
                          ]))
    (doall  x)
    (doseq [[feature value] featurelist]
      (def lprop (name feature))
      (def lval (name value))
      (def y ( map println [(cond (= lprop "gloss")
                                  (format "\taamas:%s \"%s\" ;" lprop lval)
                                  (= lprop "lemma")
                                  (format "\taamas:%s \"%s\" ;" lprop lval)
                                  (re-find #"^token" lprop)
                                  (format "\t%s:%s \"%s\" ;" sgpref lprop lval )
                                  (re-find #"^note" lprop)
                                  (format "\t%s:%s \"%s\" ;" sgpref lprop lval)
                                  :else
                                  (format "\t%s:%s %s:%s ;" sgpref lprop sgpref lval))
                            ]))
      (doall y)
      )
    (println "\t.")
    ))

(defn do-lexterms
  [lexterms]
  (doseq [termcluster lexterms]
    (def label (:label termcluster))
    (def x (map println [
                         (format "\n#TERMCLUSTER: %s\n"  label)])
      )
    (doall x)
    (def terms (:terms termcluster))
    (def schema (first terms))
    (def data (next terms))
    (def common (:common termcluster))
    ;; Need to build up string which can then be println-ed with each term of cluster
    (doseq [term data]
      (def termid (uuid))
      (def w (map println [
                                        ;(format "\n"  )
                           (format "aama:ID%s a aamas:Term ;" termid)
                           (format "\taamas:lang aama:%s ;" Lang)]))
      (doall w)
      (doseq [[feature value] common]
        (def cprop (name feature))
        (def cval (name value))
        (def x ( map println [
                              (cond (= cprop "lexeme")
                                    (format "\taamas:%s aama:%s-%s ;" cprop Lang cval)
                                    (re-find #"^token" cprop)
                                    (format "\t%s:%s \"%s\" ;" sgpref cprop cval )
                                    (re-find #"^note" cprop)
                                    (format "\t%s:%s \"%s\" ;" sgpref cprop cval )

                                    :else
                                    (format "\t%s:%s %s:%s ;" sgpref cprop sgpref cval)
                                    )
                              ])
          )
        (doall x)
        )
      (def termmap (apply assoc {}
                          (interleave schema term)))
      (doseq [tpropval termmap]
        (def tprop (name (key tpropval)))
        (def tval (name (val tpropval)))
        (def y (map println [
                             (cond (re-find #"^\"" tval)
                                   (format "\t%s:%s \"%s\" ;" sgpref lprop lval)
                                   ;;  following redundant if previous clause works
                                   (re-find #"^token" tprop)
                                   (format "\t%s:%s \"%s\" ;" sgpref tprop tval )
                                   (re-find #"^note" tprop)
                                   (format "\t%s:%s \"%s\" ;" sgpref tprop tval )
                                   (re-find #"^gloss" tprop)
                                   (format "\taamas:%s \"%s\" ;" tprop tval)
                                   (= tprop "lexeme")
                                   (format "\taamas:%s aama:%s-%s ;" tprop Lang tval)
                                   :else
                                   (format "\t%s:%s %s:%s ;" sgpref tprop sgpref tval)
                                   )
                             ])
          )
        (doall y)
        )
      (def z (map println [
                           (format "\t." )])
        )
      (doall z)
      )
    )

  (defn -main
    "Calls the functions that transform the keyed maps of a pdgms.edn to a pdgms.ttl"
    [& file]

    (let [inputfile (first file)
          pdgmstring (slurp inputfile)
          pdgm-map (edn/read-string pdgmstring)
          ;; lang (name (pdgm-map  :lang))
          ;; Lang (clojure.string/capitalize lang)
          ;; sgpref (pdgm-map :sgpref)
          ]

      (do-prelude inputfile pdgm-map)

      (do-props (pdgm-map :schemata))

      (do-morphemes (pdgm-map :morphemes))

      (do-lexemes (pdgm-map :lexemes))

      (do-lexterms (pdgm-map :lxterms))

      )
