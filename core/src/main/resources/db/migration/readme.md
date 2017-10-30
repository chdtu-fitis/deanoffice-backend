This folder contains SQL for database migration.

Each versioned migration must have a unique version and a description.

Each file should have next format:
    _V[version]__ [any description].sql_
    
    Example: V1__Cars.sql
    
A version must have the following structure:    
* One or more numeric parts 
* Separated by a dot (.) or an underscore (_)
* Underscores are replaced by dots at runtime
* Leading zeroes are ignored in each part

