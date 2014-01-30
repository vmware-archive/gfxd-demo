connect client 'localhost:1527';

CALL SYSCS_UTIL.IMPORT_TABLE_EX ('APP', 'LOAD_AVERAGES', '${load_averages_csv}', ',', null, null, 0, 
 0 /* don't lock the table */,
 6 /* threads to use for import */,
 0 /* case insensitive table name */,
 null /* use the default import implementation */,
 null /* unused, null required */)

