Category 1: Bloaters
Smell Name	File and Line Number	Evidence	Why Bloater?	Recommended
Large Class	com/bank/model/
BankUtil.java
Lines 8–150	The class manages a global java.sql.Connection singleton,
 a shared static Scanner instance,
 and string validation methods such as checkEmail() and 
checkPhoneNumber(), all within
 a single file.	The BankUtil class vastly 
oversteps the Single
 Responsibility Principle. 
It performs unrelated 
tasks — database 
connection management, 
global UI input handling, 
and regex validation — 
making it a dumping 
ground for unrelated system
 utilities that grows harder to maintain over time.	Extract Class — split into focused responsibilities: a DatabaseManager class, an InputReader class, and a ValidationUtil class.
Long Parameter List	com/bank/model/
User.java
Lines 12–14	The constructor signature accepts 7 sequential string parameters: public User(String username, String password, String firstName, String lastName, String phoneNumber, String address, String email)	With 7 disjointed primitive string arguments, instantiating a User object is fragile and error-prone. Passing strings in the wrong order silently produces incorrect results. The list grows as attributes are added, further violating maintainability.	Introduce Parameter Object — create a UserDetails or UserRegistrationRequest object to group these related parameters.
Primitive Obsession	com/bank/model/
User.java
Lines 4–10	Domain-meaningful attributes such as phoneNumber and email are stored exclusively as raw String primitives, with all validation logic delegated externally to BankUtil.	Rich domain concepts like email and phone number are represented as plain strings instead of small dedicated value objects. This forces validation to live outside the data it governs, creating scattered responsibility.	Replace Data Value with Object — create dedicated classes such as ContactEmail and PhoneNumber that encapsulate their own validation rules.
Long Method	com/bank/model/
BankRegistration-
Model.java
Lines 68–87	The method insertIntoUsersInfoTable() contains a long sequence of JDBC PreparedStatement mappings: setObject(1, ...), setObject(2, ...), continuing through multiple hardcoded index positions.	The sheer volume of hardcoded parameter indices extends the method vertically, embedding low-level database binding detail directly into what should be a clean transaction method. This makes the method difficult to read, test, or modify.	Extract Method — extract the
 parameter-binding logic into
 a dedicated helper method such as bindUserParameters
(PreparedStatement ps, User user).

Category 2: Object-Orientation Abusers
Smell Name	File and Line Number	Evidence	OO Principle Violated	Treatment
Switch Statements
(Enum mapping bypass)	com/bank/model/
BankViewAccount-
Model.java
Lines 42–50 (getBank-
AccountType())
Lines 54–62 (getCurrency())	Both methods use a raw switch(typeID) and switch(currencyID) on integer primitives to manually map IDs to the existing BankAccountType and Currency enumerations. Any new type or currency requires opening this class and bolting onto the switch block.	Polymorphism — Java enums natively support behaviour. Using manual switch statements bypasses this, creating fragile procedural mapping that violates the Open/Closed Principle.	Replace Conditional with Polymorphism — add a static factory method fromId(int id) inside each enum to encapsulate the mapping logic natively.
Switch Statements
(Static routing in controller)	com/bank/controller/
BankViewAccounts
.java Lines 75–84 (runUserBank-
AccountFunction())	A literal switch(choice) on an integer determines which functional controller runs: BankDeposit.run() or BankWithdrawal.run(). Adding a new banking function requires modifying this switch block directly.	Open/Closed Principle — the class must be opened and modified every time a new account operation is introduced, making it resistant to extension.	Replace with Polymorphism using a Command or Strategy pattern — each operation becomes a concrete class implementing a common BankAccountCommand interface, dispatched dynamically.
Alternative Classes with Different Interfaces	com/bank/controller/
BankDeposit.java 
vs 
com/bank/controller/
BankWithdrawal.java
Structural (entire classes)	Both classes implement virtually identical behavioural loops: prompt the user for an amount, validate it, update the database balance, and insert a log record — yet they share no common interface or superclass and use divergent method naming.	Abstraction and Reuse — 
near-identical behaviours 
are duplicated across two
 classes instead of being unified under a shared BankAccount
Transaction 
interface, forcing parallel maintenance.	Extract Superclass or common interface — define a BankAccountTransaction 
interface with a shared execute(User user, UserBankAccount account) method, and have both classes implement it.



Category 3: Change Preventors
Smell Name	File and Line Number	How Many Places Must Change?	Treatment
Shotgun Surgery
(Database context)	com/bank/model/
BankUtil.java
Lines 21–30 (static Connection
 and init() logic)	Because public static Connection connection is accessed directly by virtually every Model and Controller class, any change to the database layer — such as introducing a connection pool, switching to an ORM, or moving to cloud-managed credentials — requires parallel modifications scattered across all dependent classes in the application.	Move Field — encapsulate the Connection inside a dedicated DatabaseManager class with a controlled access method. All callers obtain connections through this single access point, meaning future changes affect only one place.
Shotgun Surgery
(Input scanning))	com/bank/model/
BankUtil.java
Line 18 (public static Scanner scanner)	Every controller references BankUtil.scanner.nextLine() directly. Migrating the application from a console interface to a GUI or REST API would require hunting through and replacing scanner calls across all 15+ controllers simultaneously.	Move Field and introduce an InputReader abstraction — controllers call a generic InputReader.readLine() method, allowing the underlying input mechanism to be swapped in one place without touching individual controllers.
Divergent Change
(Mixed presentation and validation logic)	com/bank/controller/
BankRegistration.java
Lines 46–52	This single class must be edited for two unrelated reasons: (1) if the UI prompts change, and (2) if the database validation or exception routing changes, since it handles both Scanner prompting and catch(SQLException | Exception ex) flows together.	Extract Class — separate the console presentation logic into a BankRegistrationView class and keep only transaction coordination inside the controller, so each class changes for only one reason.
Divergent Change
(Action orchestration)	com/bank/controller/
BankModifyProfile.java
Lines 28–36 (runChangeOption())	The method must be modified both when the UI flow changes and when business rules around profile mutation change, since it simultaneously manages UI redirection strings and executes business logic loops such as changePassword(user).	Extract Class — delegate business mutation logic to a BankModifyProfileModel, keeping the controller responsible only for UI flow.

Category 4: Dispensables
Smell Name	File and Line Number	What Makes It Dispensable?	Treatment
Data Class	com/bank/model/
User.java
Lines 4–13	The User class contains only seven private string fields and their associated getters/setters. It has no independent validations, no state-resolving rules, and no meaningful behaviour. It exists purely as a passive data carrier passed between controllers and models.	Move Method — move relevant validation and business rules (e.g., password validation, email checking) into the User class itself so it becomes a proper domain object rather than an anemic data container.
Data Class	com/bank/model/
UserBankAccount
.java
Lines 4–13	Similar to User, UserBankAccount is an anemic DTO holding balance and account type fields. All deposit math and withdrawal logic is implemented externally in controller classes rather than being encapsulated here where the data lives.	Move Method — bring deposit and withdrawal calculation logic into UserBankAccount so the object manages its own state transitions.
Lazy Class	com/bank/controller/
BankApplication
.java
Lines 11–14	The BankApplication class exists solely to execute a 3-line loop delegating immediately to BankApplicationView.display(). It performs no meaningful coordination, adds no logic of its own, and creates unnecessary navigation overhead.	Inline Class — merge the trivial run logic directly into the application entry point or the View class it delegates to, eliminating the redundant layer.
Duplicate Code)	com/bank/controller/
BankDeposit.java
 Vs
 com/bank/controller/
BankWithdrawal.java
Structural (entire transaction loops)	Both classes repeat the same logic sequence: prompt for amount → validate → update balance → insert log record. Fixing a bug in the log insertion of one class will not automatically fix the same bug in the other, introducing a subtle inconsistency risk.	Extract Method — pull the shared transaction loop into a common abstract base class or utility method called by both, so the logic lives in exactly one place.

Category 5: Couplers
Smell Name	File and Line Number	Description of the Coupling Problem
	Treatment
Middle Man	com/bank/controller/
BankUserProfile.java
Lines 32–40 (runProfileFunction())	The method adds no local logic whatsoever.
 It reads the user's choice and immediately 
forwards it downstream:
 BankModifyProfile.run(user) or BankViewAccounts
.run(user). It is a mindless conveyor belt that exists purely as an unnecessary navigation layer.	Remove Middleman — callers should invoke BankModifyProfile and BankViewAccounts directly, eliminating the intermediary class or inlining its trivial routing logic at the call site.
Feature Envy	com/bank/controller/
BankModifyProfile.java
Lines 87–90 (validation control loops)	The controller repeatedly reaches into external classes to implement its logic: while(!BankUtil.checkEmail(email)) calls BankUtil's data, and the method also interrogates the User object's attributes to enforce business rules. The method's intelligence clearly belongs in the objects it is envying.	Move Method — relocate email/password validation logic into the User model or a dedicated validation class, so the controller only orchestrates flow rather than implementing data-level rules
Inappropriate Intimacy
(Database autocommit scoping)	com/bank/model/
BankRegistration-Model.java
Line 43, Line 85	The model directly manipulates global JDBC state by calling BankUtil.connection.setAutoCommit(false). This breaches encapsulation — the model knows and controls the internal configuration of BankUtil's connection object rather than treating the database layer as an abstraction.	Encapsulate Field — wrap transaction management (begin, commit, rollback) inside BankUtil or a DatabaseManager class, exposing only clean methods like beginTransaction() and commitTransaction().
Inappropriate Intimacy
(Input coupling)	com/bank/controller/
BankLoan.java
Line 26	String choice = BankUtil.scanner.nextLine() — the controller reaches directly into BankUtil's static state to access hardware-level console input. This tightly binds the controller to the specific input mechanism and prevents dependency injection or testing with mock inputs.	Encapsulate Field — introduce an InputReader abstraction injected into controllers, so they call inputReader.readLine() without knowing the underlying mechanism.

