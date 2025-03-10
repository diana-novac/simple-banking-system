# Project Assignment POO  - J. POO Morgan - Phase Two
### Copyright Diana Novac - 321CA

## Project Structure
* src/
    * commands/ - Contains classes that handle user commands
        * actions/ - Implements ActionCommand interface for commands that modify
          the global state
        * outputs/ - Implements OutputCommand interface for commands that generate responses
        * CommandRegistry.java - Central registry for all commands
    * commerciants/ - Contains the commerciant class and implementations of the CashbackStrategy interface
    * data/ - Manages application data and initialization
    * main/
        * App.java - Core application logic and flow controller
    * models/ - Represents core entities in the system
        * accounts/ - Contains different types of accounts: classic, savings, business
        * roles/ - Contains implementations of the Role interface, like Employee, Manager, Owner
    * plans/ - Contains implementations of the AccountPlan interface
    * utils/ - Utility classes for common tasks
    * exceptions/ - Custom exception classes
## Design Patterns

### 1. Factory Pattern

* **Used in**: `AccountFactory.java`, `CardFactory.java`, `RoleFactory.java`, `AccountPlanFactory.java`,
                `StrategyFactory.java`
* **Purpose**: The Factory Pattern allows the creation of objects like accounts, cards, user roles or cashback strategies.
  This approach ensures scalability, making it easy to add new types of accounts, cards, roles, and strategies
  without changing existing code.

### 2. Command Pattern

* **Used in**: `commands/actions/`, `commands/outputs/`
* **Purpose**: Each action or debugging command is represented as a separate class.
  These commands are registered in `CommandRegistry`, which ensures the correct logic is executed
  for each action. This pattern allows new commands to be added without modifying the core logic
  of the application.

### 3. Strategy Pattern
* **Used in**: `commerciants`, `plans`, `roles`
* **Purpose**: The Strategy Pattern is used to encapsulate algorithms and behaviors, allowing them to be  
easily interchangeable without modifying the client code.

#### **Implementations**

1. **Cashback Strategy (`commerciants`)**
    - **Implementation**:
        - **Interface**: `CashbackStrategy` defines methods for applying cashback (`applyCashback`) and checking eligibility (`isEligible`).
        - **Concrete Strategies**:
            - `NumberOfTransactionsStrategy` — Cashback is based on the number of transactions made by the user.
            - `SpendingThresholdStrategy` — Cashback is applied based on the total spending amount of the user.
        - **Factory**: `StrategyFactory` creates the appropriate strategy based on the commerciant input.

2. **Account Plans (`plans`)**
    - **Context**: Different account plans offer various benefits, such as transaction fees and cashback rates.
    - **Implementation**:
        - **Interface**: `AccountPlan` defines behaviors like `getTransactionFee`, `automaticUpgrade`, and `getCashbackRate`.
        - **Concrete Strategies**:
            - `StandardPlan`
            - `StudentPlan`
            - `SilverPlan`
            - `GoldPlan`
        - **Factory**: `AccountPlanFactory` creates account plans based on user input.

3. **Role-Based Access (`roles`)**
    - **Context**: In business accounts, users have roles (`Role`) that define what actions they can perform.
    - **Implementation**:
        - **Interface**: `Role` specifies methods like `canSetLimits` and `canPerformTransaction`.
        - **Concrete Strategies**:
            - `Owner`
            - `Manager`
            - `Employee`
        - **Factory**: `RoleFactory` assigns roles dynamically based on the given commands.

### 4. Builder pattern

* **Used in**: `TransactionBuilder.java`
* **Purpose**: The Builder pattern simplifies the creation of complex transaction objects 
through method chaining.

## App Flow - Relationships between classes

### Initialization

* `App.java` initializes users, accounts, and commands.
* `CommandRegistry` registers all available commands for quick access during execution.

### Command Processing

* Commands are received and processed through `CommandInput` objects.
* Depending on whether the command modifies the system state or generates output,
  it is delegated to the `ActionCommand` or `OutputCommand` maps in `CommandRegistry`.

### Transaction Management

* `TransactionHandler` handles the creation and logging of transactions for both users and accounts.
* Utility classes like `TransactionBuilder` simplify the creation of complex transaction objects.
## End of Execution

* At the end of execution, the system outputs results for all commands.
