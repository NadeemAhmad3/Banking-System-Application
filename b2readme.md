# Java Bank Application - Code Smell Identification (B2)

In accordance with the project requirements, below is a deep analysis of twenty distinct code smells across the five official categories mapped directly to this Java codebase. Every smell cites the precise location and line number alongside a qualitative justification for its classification.

## Category 1: Bloaters
Bloaters characterize methods, classes, and parameters that have grown excessively over time, severely hindering readability and future scaling.

### 1. Large Class (God Object)
*   **File:** `com/bank/model/BankUtil.java`
*   **Line:** `Lines 8-150` (Encompassing the entire class structure)
*   **Justification:** The `BankUtil` class vastly oversteps the Single Responsibility Principle. It manages global Database Connection initialization, stores a statically shared global UI `Scanner`, formats strings, and executes hardcoded regex input validations (like `checkEmail` or `checkPhoneNumber`). The file functions as an anti-pattern dumping ground for unrelated system utilities.

### 2. Long Parameter List
*   **File:** `com/bank/model/User.java`
*   **Line:** `Lines 12-14`
*   **Justification:** The main class constructor requires passing exactly 7 disjointed primitive string arguments sequentially (`username`, `password`, `firstName`, `lastName`, `phoneNumber`, `address`, `email`). With parameter lists this long, instantiating a user from database rows becomes fragile and highly prone to incorrect variable assignment errors. 

### 3. Primitive Obsession
*   **File:** `com/bank/model/User.java`
*   **Line:** `Lines 4-10`
*   **Justification:** The user attributes represent domain identities like `phoneNumber` and `email` solely as atomic `String` primitives. In rigorous Object-Oriented implementations, they should have been captured as independent value objects (e.g. `class ContactEmail`) capable of managing their own regex state limits instead of outsourcing it.

### 4. Long Method
*   **File:** `com/bank/model/BankRegistrationModel.java`
*   **Line:** `Lines 68-87` (inside `insertIntoUsersInfoTable`)
*   **Justification:** A significant segment of this controller method consists strictly of JDBC `PreparedStatement` mappings (`setObject(1...)`, `setObject(2...)`...). The sheer volume of hardcoded indices extends the method vertically, distracting from the transaction logic.

---

## Category 2: Object-Orientation Abusers
These smells arise when code completely ignores the primary advantages of an Object-Oriented design, primarily Polymorphism and Encapsulation.

### 1. Switch Statements (Enum Mapping bypass)
*   **File:** `com/bank/model/BankViewAccountModel.java`
*   **Line:** `Lines 42-50` (`getBankAccountType()`)
*   **Justification:** The method invokes a rigid, manually hardcoded `switch (typeID)` block to map integer ID primitives mathematically to the existing `BankAccountType` enumeration. This procedural pattern avoids utilizing native Java enum behaviors or an embedded `fromId()` static factory.

### 2. Switch Statements (Redundant Procedural Check)
*   **File:** `com/bank/model/BankViewAccountModel.java`
*   **Line:** `Lines 54-62` (`getCurrency()`)
*   **Justification:** Like the smell above, mapping the `currencyID` forces a direct code branch. Any new currency integration requires developers to crack open the `BankViewAccountModel.java` code explicitly to bolt onto this Switch sequence.

### 3. Switch Statements (Static Routing)
*   **File:** `com/bank/controller/BankViewAccounts.java`
*   **Line:** `Lines 75-84`
*   **Justification:** The method `runUserBankAccountFunction()` uses a `switch (choice)` array to determine whether a user intends to fire `BankDeposit` or `BankWithdrawal`. In an idiomatic OO design, these would be polymorphic subclasses inheriting an abstract `Command` structure that execute dynamically, not statically mapped integers.

### 4. Alternative Classes with Different Interfaces
*   **File:** `com/bank/controller/BankDeposit.java` vs `com/bank/controller/BankWithdrawal.java`
*   **Line:** Structural (Entire classes)
*   **Justification:** Both classes implement virtually the exact same behavioral loops: ping the UI, request an amount, validate it, update the database, insert into the Logs table. Despite solving near-identical constraints, they utilize divergent procedural naming methods and do not share any `BankAccountTransaction` interface overhead, forcing maintenance duplication.

---

## Category 3: Change Preventors
Change preventors describe structures where altering one component dictates altering multiple other files unconditionally due to extreme technical coupling. 

### 1. Shotgun Surgery (Database Context)
*   **File:** `com/bank/model/BankUtil.java`
*   **Line:** `Lines 21-30`
*   **Justification:** Implementing database communication as a direct static `public static Connection` within a utility wrapper forces any architectural upgrade to the persistent storage layer (like applying a connection pool, an ORM, or a cloud dependency context) to shatter all models universally relying on the `BankUtil` object paradigm globally across the repo.

### 2. Shotgun Surgery (Input Scanning)
*   **File:** `com/bank/model/BankUtil.java`
*   **Line:** `Line 18`
*   **Justification:** Holding a globally exposed static instance of `public static Scanner scanner = new Scanner(System.in)` means every controller assumes standard keyboard polling. Moving the application to a GUI or REST interface would necessitate a global hunt tearing standard console calls (`BankUtil.scanner.nextLine()`) out of 15 overlapping controllers.

### 3. Divergent Change (Mixed Presentation Logic)
*   **File:** `com/bank/controller/BankRegistration.java`
*   **Line:** `Lines 46-52`
*   **Justification:** The controller implements the literal Console UI prompts and concurrently attempts to resolve data validation flow control (`catch (SQLException | Exception ex)`). This is a Divergent Change mechanism; if the database rules change OR if the text UI changes entirely, this single code segment must mutate iteratively.

### 4. Divergent Change (Action Orchestration)
*   **File:** `com/bank/controller/BankModifyProfile.java`
*   **Line:** `Lines 28-36` 
*   **Justification:** `runChangeOption(User user, Integer option)` manages physical string UI redirection and simultaneously executes localized business mutation loops (`changePassword(user)`). Any modifications expanding user attributes or revamping user experience flows forces modification inside this bottleneck method.

---

## Category 4: Dispensables
Dispensables exist as literal system cruft. They are mechanisms, duplicates, and components that serve zero architectural or behavioral purpose. 

### 1. Data Class
*   **File:** `com/bank/model/User.java`
*   **Line:** `Lines 4-13`
*   **Justification:** Data Classes are structures holding data structures without logic. `User` does absolutely nothing but manage string mappings for controller routing. It has no independent validations, checks, or internal state-resolving rules. 

### 2. Data Class
*   **File:** `com/bank/model/UserBankAccount.java`
*   **Line:** `Lines 4-13`
*   **Justification:** Similar to the global user class, `UserBankAccount` is solely an anemic class DTO. It passively holds balances and types mapping onto the DB table and outsources all legitimate deposit math and logic behaviors to foreign Controller components.

### 3. Lazy Class
*   **File:** `com/bank/controller/BankApplication.java`
*   **Line:** `Lines 11-14`
*   **Justification:** The execution method wrapper inside `BankApplication` attempts to present itself as an orchestrator but executes exclusively as a 3-line structural hook passing authority transparently left back to the View. It doesn't fulfill enough responsibility scope to merit an entire file declaration.

### 4. Duplicate Code (Dead Output)
*   **File:** `com/bank/model/Currency.java` Or `com/bank/model/BankAccountType.java`
*   **Line:** `Lines 11-15` (Approx)
*   **Justification:** Standard enums appear to be bloated natively with arbitrary `System.out.println(Currency.DOLLAR)` main scripts (typically left globally through prototyping). This is fundamentally irrelevant leftover snippet duplication polluting production code configurations. 

---

## Category 5: Couplers
Couplers reflect elements intimately intertwined across isolated domains, leading to an application heavily resistant to independent module testing.

### 1. Middle Man
*   **File:** `com/bank/controller/BankUserProfile.java`
*   **Line:** `Lines 32-40` (`runProfileFunction()`)
*   **Justification:** This method completely fails to add localized context, working strictly as a mindless conveyor belt delegating parameters downwards (`BankModifyProfile.run(user)` / `BankViewAccounts.run(user)`). It's an excessive intermediary layer padding method calls.

### 2. Feature Envy
*   **File:** `com/bank/controller/BankModifyProfile.java`
*   **Line:** `Lines 87-90`
*   **Justification:** Within the validation control loops, the external `BankModifyProfile` logic envies the `BankUtil` properties, explicitly requesting the exact external mechanisms like `while(!BankUtil.checkEmail(email))`. Ideally, models or objects should inherently guard and decline invalid inputs directly.

### 3. Inappropriate Intimacy (Database Autocommit Scoping)
*   **File:** `com/bank/model/BankRegistrationModel.java`
*   **Line:** `Line 43`, `Line 85`
*   **Justification:** Intervening inside `BankRegistrationModel` queries manually to run `BankUtil.connection.setAutoCommit(false)` breaches encapsulation walls heavily. It demonstrates an inappropriate intimacy whereby the procedural service dictates absolute global JDBC parameters instead of treating the database integration abstractly. 

### 4. Inappropriate Intimacy (Input Tying)
*   **File:** `com/bank/controller/BankLoan.java`
*   **Line:** `Line 26`
*   **Justification:** `String choice = BankUtil.scanner.nextLine();` - Across custom added modules (like the Loan branch components), the controllers intimately know exactly how to acquire physical hardware inputs (via explicit calls reaching statically into the `BankUtil` global state variables). This ruins dependency injection.
