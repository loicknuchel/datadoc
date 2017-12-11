# DataDoc

Pages to mockup :

    - /
        - header with menu
        - big search in the middle (google like) (can search on schemas, tables & fields)
        - some stats about current state (number of schemas, tables, fields, comments...)
    - /schemas
        - list all schema & tables (cf: https://build.crto.in/job/vault-glup-post-submit/Generated_GLUP_documentation/index.html)
    - /schemas/$schema
        - list all tables in the schema + show a doc on the schema (purpose, owner)
    - /schemas/$schema/tables/$table
        - doc on table (purpose, owner, job links (source code & Cuttle admin), child & parent dependencies (table or field level))
        - list fields with doc (name, type, values, technical, functional, usage, child & parent dependencies)
        - allow to edit field doc & see doc history
    - /schemas/$schema/tables/$table/fields/$field
        - cf https://confluence.criteois.com/display/~m.vernet/DataDoc+-+AX+Use+Cases
    - /governance
        - not documented fields
        - recently documented fields
        - doc bulk edit
    - /admin
        - refresh
            - hive schema
            - fetch doc (hive comments, dataflow conf, datadisco)

Other info :

    - on each doc save metadata
        - create date
        - created by
        - (? team)
        - source (user, metastore, dataflow conf)
