time_tests.csv and ident_time_tests.csv are the subset of TDWG BDQ TG2 tests related to TIME (temporal) terms, excluding the broad measure tests.

Turtle RDF generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as event_date_qc) with:

   ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCEventDQ_kurator_ffdq.config -in ../event_date_qc/generation/time_tests.csv -out ../event_date_qc/generation/time_tests.ttl
   ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCOtherDQ_kurator_ffdq.config -in ../event_date_qc/generation/ident_time_tests.csv -out ../event_date_qc/generation/ident_time_tests.ttl 
