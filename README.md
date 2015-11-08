# inventoryManager
A simple inventory manager for loanable items. 

Scope
-----
Inventory manager is built such that it will log loans, returns and stock for a small store. It works on the basis that all users will have an account and the store will be able to run itself.  
This program has no ability to calculate bills or other matters regarding payment or due dates. This may be a future goal, in which case it will be implemented in a similar way to TOC19 below. 

Method
------
Three types of user will be provided in the system, with scope for expansion:
* USER: level 0, no access outside loaning items from the system. 
* Admin: level 2, access to most data, returning items and managing users. 
* ROOT: level 3, full access. 

Interface
---------
All aspects of the interface will be written in JavaFX, with touch in mind. Interface elements are only to be within the Interface class with helper methods from WorkingUser. 
The interface may later be extended into multiple classes to aid the expansion of the admin role. 

Security
--------
This system maintains a database of hashed, salted passwords for each user. These passwords can be changed without the existing password, but this requires an admin to allow the change and will be logged. 
All hashing will be done through java internal functions, rather than using a hand written version. 
Salts are stored in the database with the passwords.

Code Base
--------
* Origional code was taken from https://github.com/jarrah-95/TOC19/tree/GUI-Rebuild
* Project has substantially changed since then. Moving to SQLite and catering better to the needs of Inventory Manager. 

Further Information
-------------------
If you would like further information, feel free to contact me through github or raise an issue on this repository. 
