# text-Query

Given a directory of text documents, determines TF-IDF of each term and creates a searchable index

The program will read through the given directory of text or dat files and parse out each unique word a track it's TF and IDF

Once done parsing, a query term can be given in the form of a single word with or without a wildcard query

example: dog, DOG -> both will search for the term 'dog'   while the query do* will return 'dog' but also would search for 'down' or 'done'

The alldocs zip folder contains 2200 DAT files to read from as input
