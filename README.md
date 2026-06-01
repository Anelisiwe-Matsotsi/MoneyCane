# The Budget Tracker Android App: MONEY CANE 💰

## Features 🛠️
<br>

### Login and Registration 🔒
- User can register by entering their email address, password and confirm their password to be registered successfully.
- Users will be able to log in with their email address and password.
- Users account data is being saved in firebase.
- Firebase has an authentication feature that checks if the user entered a valid email address and matching password.
<br>

### Adding Transactions 💸
- User can add transactions that occure during their day-to-day activities.
- User transactions will include the name, amount, description, category type, wallet type, date, transaction type and image upload.
- Category types are created by the user in the category view.
- Wallet types are created by the user in the wallet view
- Transaction type can either be an expense or income.
- User can choose from which wallet they are subtracting (expense) or adding (income) to. 
- Users can choose to look at all transactions, all income or all expenses
- Transactions can be filtered by date specifically by seeing today's date or a certain month (example: see only january's expenses)
<br>

### Set Goals and Budgets 🥅
- User can set their minimum and maximum spending goal
- After creating this the app will automatically suggest hwo much they should spend daily to stay within their monthly budget goal.
- If the user stays within their monthly budget then they will be rewarded with a green striped sugar cane (gamification feature)
<br>

### Create Categories 🧮
- User can create a category by adding its name and the category limit they want monthly
- User can edit their monthly limit
- Example: Category Name:Food & Limit: R6000
<br>

### Create Wallets 💳
- User can create a wallet
- A wallet is a type of account they have
- Example: Bank, Cash, Savings, Bitcoin
- The user will enter the wallets name and set the initial amount they have in that wallet
<br>

### In-App Authentication: Passcode & Biometrics 🔑
- User can set a 4-digit passcode in the "More" screen
- User can choose to activate or deactive biometric authentication via toggle switch used
- User can either have passcode only, passcode & biometric
- Passcode is used as a fallback if biometric authentication is falling
- User can forget password then they will have to verify their identity using email and password to reset the PIN
<br>

## Unit Tests Passed
<br>
<img width="1129" height="684" alt="Image" src="https://github.com/user-attachments/assets/ec900b42-be23-4af2-b99b-11ddc6887473" />
Made 32 JUnit tests. Created helper methods linked to fragment classes logic to create the tests.
