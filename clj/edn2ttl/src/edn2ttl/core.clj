(ns edn2ttl.core
  (require [clojure.edn :as edn]
      [stencil.core :as tmpl ])
  (:gen-class :main true))

(defn uuid
  "Generates random UUID for pdgm terms"
  []
  (str (java.util.UUID/randomUUID))
)

(tmpl/render-string (str "Hi there. {{name}}.") {:name "Donald"})
(println "HI THERE!")

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
                             "@prefix {{pfx}}   <http://id.oi.uchicago.edu/aama/2013/{{lang}}/> .\n")
                        {:pfx sgpref :lang lang})
    (println "#SCHEMATA:")
    (tmpl/render-string (str (newline)
                             "aama:{{lang}} a aamas:Language .\n"
                             "aama:{{lang}} rdfs:label \"{{lang}}\" .\n")
                        {:lang lang})
    )
)

(defn do-props
  [schemata sgpref Lang]
  (doseq [[property valuelist] schemata]
    (let [prop (name property)
          Prop (clojure.string/capitalize prop)]
          ;; NB clojure.string/capitalize gives  wrong output with
          ;; terms like conjClass: =>Conjclass rather than ConjClass

    (tmpl/render-string (str
                         (newline)
                         "#schemata: {{prop}}\n"
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
                         :Prop Prop})

    (doseq [value valuelist]
      (let [val (name value)]
      (tmpl/render-string (str
                           "{{pfx}}:{{val}} aamas:lang aama:{{Lang}} .\n"
                           "{{pfx}}:{{val}} rdf:type {{pfx}}:{{Prop}} .\n"
                           "{{pfx}}:{{val}} rdfs:label \"{{val}}\" .\n")
                           {:pfx sgpref
                            :Lang Lang
                            :val val})
      )
    )
  )
 )   
)

(defn do-morphemes
  [morphemes sgpref Lang]
  (println  "\n#MORPHEMES:\n")
  (doseq [[morpheme featurelist] morphemes]
    (let [morph (name morpheme)]
      (tmpl/render-string (str
                           "aama:{{Lang}}-{{morph}} a aamas:Muterm ;\n"
                           "\taamas:lang aama:{{Lang}} ;\n"
                           "\trdfs:label \"{{morph}}\" ;\n")
                          {:Lang Lang
                           :morph morph})
      ) ;; (let [morph (name morpheme)]
    (doseq [[feature value] featurelist]
      (let [mprop (name feature)
            mval (name value)]
      
         (cond (= mprop "gloss")
               (tmpl/render-string (str "\trdfs:comment \"{{mval}}\" ;\n") {:mval mval})
               (= mprop "lemma")
               (tmpl/render-string (str"\trdfs:comment \"{{mval}}\" ;\n") {:mval mval})
               (re-find #"^\"" mval)
               (tmpl/render-string (str"\t{{pfx}}:{{mprop}} \"{{mval}}\" ;\n") {:pfx sgpref :mval mval :mprop mprop})
               :else
               (tmpl/render-string (str"\t{{pfx}}:{{mprop}} {{pfx}}:{{mval}}s ;\n") {:pfx sgpref :mval mval :mprop mprop})
         )
      ) ;; (let [mprop (name feature)
     ) ;; (doseq [[feature value] featurelist]
  (println "\t.")
  ) ;; (doseq [[morpheme featurelist] morphemes]
 ) ;; (defn do-morphemes


(defn do-lexemes
  [lexemes sgpref Lang]
  (println  "\n#LEXEMES:")
  (doseq [[lexeme featurelist] lexemes]
    (let [lex (name lexeme)]
      (tmpl/render-string (str
                           "aama:{{Lang}}-{{lex}} a aamas:Lexeme ;\n" 
                           "\taamas:lang aama:{{Lang}} ;\n" 
                           "\trdfs:label \"{{lex}}\" ;\n")
                          {:Lang Lang
                           :lex lex})
    (doseq [[feature value] featurelist]
      (def lprop (name feature))
      (def lval (name value))
      ;;(tmpl/render-string (str
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
    )
 ) ;; (doseq [[lexeme featurelist] lexemes
) ;; (do-lexemes)

(defn do-lexterms
  [lexterms sgpref Lang]
  (doseq [termcluster lexterms]
    (let [label (:label termcluster)]
      (println "\n#TERMCLUSTER: " label)
    )
    ;;(def x (map println [
    ;;                     (format "\n#TERMCLUSTER: %s\n"  label)])
    ;;  )
    ;;(doall x)
    (let [terms (:terms termcluster)
          schema (first terms)
          data (next terms)
          common (:common termcluster)]
    ;; Need to build up string which can then be println-ed with each term of cluster
    (doseq [term data]
      (let [termid (uuid)]
        (tmpl/render-string (str (newline)
                            "aama:ID{{termid}} a aamas:Term ;\n"
                            "\taamas:lang aama:{{Lang}} ;\n")
                          {:Lang Lang
                           :uuid uuid})
      )
      (doseq [[feature value] common]
        (let [cprop (name feature)
              cval (name value)]
        ;;(tmpl/render-string (str
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
        )
      (let [termmap (apply assoc {}
                          (interleave schema term))]
      (doseq [tpropval termmap]
        (let [tprop (name (key tpropval))
              tval (name (val tpropval))]
        ;;(tmpl/render-string (str
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
        )
      (println "\t.")
      ) ;; (let [termmap apply assoc {}
    ) ;;(doseq [term data]
  ) ;;(let [terms (:terms termcluster)
) ;;(doseq [termcluster lexterms]
) ;;(defn do-lexterms

(defn do-muterms
  [muterms sgpref Lang]
  (doseq [ mutermcluster muterms]
    (let [label (:label mutermcluster)]
      (println "\n#MUTERMCLUSTER: "  label)
    )
    (let [terms (:terms mutermcluster)
          schema (first terms)
          data (next terms)
          common (:common mutermcluster)]
    ;; Need to build up string which can then be println-ed with each term of cluster
    (doseq [term data]
	(let [termid (uuid)]
	  (def w (map println [
		;(format "\n"  )
		(format "aama:ID%s a aamas:Muterm ;" termid)
		(format "\taamas:lang aama:%s ;" Lang)]))
          (doall w)
        )
	(doseq [[feature value] common]
	    (let [cprop (name feature)
                  cval (name value)]
              (def x ( map println [
			 (cond (= cprop "morpheme")
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
	)
	(let [termmap (apply assoc {} (interleave schema term))]
	(doseq [tpropval termmap]
		(let [tprop (name (key tpropval))
                      tval (name (val tpropval))]
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
                          (= tprop "morpheme")
                               (format "\taamas:%s aama:%s-%s ;" tprop Lang tval)
			   :else
			     (format "\t%s:%s %s:%s ;" sgpref tprop sgpref tval)
                        )
			])
                  )
		(doall y)
               )
	 )
        (println "\t.")
      ) ;; (let [termmap apply assoc {}
     ) ;; (doseq [term data]
    ) ;; (let [terms (:terms mucluster)
  ) ;; (doseq [mutermcluster muterms]
) ;; (defn do-muterms

  (defn -main
    "Calls the functions that transform the keyed maps of a pdgms.edn to a pdgms.ttl"
    [& file]

    (let [inputfile (first file)
          pdgmstring (slurp inputfile)
          pdgm-map (edn/read-string pdgmstring)
          lang (name (pdgm-map  :lang))
          Lang (clojure.string/capitalize lang)
          sgpref (pdgm-map :sgpref)
          ]

      (do-prelude inputfile pdgm-map)

      (do-props (pdgm-map :schemata) sgpref Lang)

      (do-morphemes (pdgm-map :morphemes) sgpref Lang)

      (do-lexemes (pdgm-map :lexemes) sgpref Lang)

      (do-lexterms (pdgm-map :lxterms) sgpref Lang)

      (do-muterms (pdgm-map :muterms) sgpref Lang)
      )
     )
