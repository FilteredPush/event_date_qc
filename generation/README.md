time_tests.csv and ident_time_tests.csv are the subset of TDWG BDQ TG2 tests related to TIME (temporal) terms, excluding the broad measure tests.

    grep "IE Category" ../../bdq/tg2/core/TG2_tests.csv > ident_time_tests.csv
    grep DATEIDENTIFIED ../../bdq/tg2/core/TG2_tests.csv >> ident_time_tests.csv
    grep "IE Category" ../../bdq/tg2/core/TG2_tests.csv > time_tests.csv
    grep TIME ../../bdq/tg2/core/TG2_tests.csv  | grep -v DATEIDENTIFIED | grep -v DATAGENERALIZATIONS | grep -v AllDarwinCoreTerms  >> time_tests.csv

Turtle RDF generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as event_date_qc) with:

   ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCEventDQ_kurator_ffdq.config -in ../event_date_qc/generation/time_tests.csv -out ../event_date_qc/generation/time_tests.ttl
   ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCOtherDQ_kurator_ffdq.config -in ../event_date_qc/generation/ident_time_tests.csv -out ../event_date_qc/generation/ident_time_tests.ttl 


Stub Java classes generated using kurator-ffdq (from a kurator-ffdq directory in the same parent directory as event_date_qc) with: 

    ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCEventDQ_stubs_kurator_ffdq.config -in ../event_date_qc/generation/time_tests.csv -out ../event_date_qc/generation/time_tests.ttl -srcDir ../event_date_qc/src/main/java -generateClass
    ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCOtherDQ_stubs_kurator_ffdq.config -in ../event_date_qc/generation/ident_time_tests.csv -out ../event_date_qc/generation/ident_time_tests.ttl -srcDir ../event_date_qc/src/main/java -generateClass


Add comments to the end of Java classes noting out of date implementations using kurator-ffdq (from a kurator-ffdq directory in the same parent directory as event_date_qc) with: 

    ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCEventDQ_kurator_ffdq.config -in ../event_date_qc/generation/time_tests.csv -out ../event_date_qc/generation/time_tests.ttl -srcDir ../event_date_qc/src/main/java -checkVersion -appendClass
    ./test-util.sh -config ../event_date_qc/generation/event_date_qc_DwCOtherDQ_kurator_ffdq.config -in ../event_date_qc/generation/ident_time_tests.csv -out ../event_date_qc/generation/ident_time_tests.ttl -srcDir ../event_date_qc/src/main/java -checkVersion -appendClass

