# The Budget Tracker Android App: MONEY CANE 💰

## Purpose of Money Cane 🧩
- Money Cane is a South African budget app as it only accomodates ZAR currency.
- Money Cane aims to help its users make healthy spending habits and feel rewarded after completing their spending goals.
- It does not force users but rather offers a more guided and easy to learn UI that helps them see the bigger picture of where saving their money with Money Cane will have a good impact on their wallet
<br>

## Design Considerations 📐📏
- Money Cane has a light and dark mode feature
- It has separated all major features in different screen to nt overwhelm the user
- The original theme (light) has a color scheme of gold, green and white offering a cash themed color palatte
<br>

## Utilization of GitHub and GitHub Actions 👾
- GitHub was utilised to controlling merging side branches into the master branch
- It made solving merge conflicts easier because as a developer you can analyze which code you would like to keep that works best to accomplish the goal
- GitHub desktop was easy to navigate through because it always prompted for pulling the latest code in the master, sending the number of commits you are behind the master branch.
- In the GitHub actions we built a new build.yml that conducted automated testing. Github repeated the build after every merge to the master which made it easy to identify were the error has come from, solving issues quicker.
<br>

## Features 🛠️
<br>

### Login and Registration 🔒
- User can register by entering their email address, password and confirm their password to be registered successfully.
- Users will be able to log in with their email address and password.
- Users account data is being saved in firebase.
- Firebase has an authentication feature that checks if the user entered a valid email address and matching password.
<br>

### Adding Transactions 💸
- User can add transactions that occur during their day-to-day activities.
- User transactions will include the name, amount, description, category type, wallet type, date, transaction type and image upload.
- Category types are created by the user in the category view.
- Wallet types are created by the user in the wallet view
- Transaction type can either be an expense or income.
- When creating a transaction, the user can choose to add the transaction to a goal (optional)
- User can choose from which wallet they are subtracting (expense) or adding (income) to. 
- Users can choose to look at all transactions, all income or all expenses
- Transactions can be filtered by date specifically by seeing today's date or a certain month (example: see only january's expenses) (filtering monthly)
<br>

### Create Budgets 📌
- In the Budgets screen the user can see how much they have spent versus how much they budgeted
- User can create a budget by adding th budget name and budget amount
- The budget card in the home combines the amounts of all the budget entries and displays the progress to the user.
- The card also has a smart suggestion feature where the user is advised to spend a calculated amount per day to stay within budget. This value updates everytime the user enters transactions.
- In the budget screen- user can set the progress of each budget they created (Groceries, Shopping etc). It shows their budgeted amount and how much they have spent so far.

<br>

### On-Boarding Screen 
- This Page appears for first time users rigt after the splash screen.
- It informs the user of what to expect.
- It does not overwhelm the user with too much content but allows them to see how Money Cane will help them and the features to expect
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

### Shared-Wallets 🧍🏾‍♀️💰🧍🏾‍♂️  
- During the creation of a wallet, user has a choice of adding someone they would like to share the wallet with (optional)
- User can select multiple people to share a wallet with. The user can select someone from the list of people who have registered to use Money Cane
- User can search the recipient they would like to share the wallet with.
- Afetr creation- both parties can edit or delete the wallet
- The invited user will be notified when they are added to a shared wallet
<br>

### In-App Authentication: Passcode & Biometrics 🔑
- User can set a 4-digit passcode in the "More" screen
- User can choose to activate or deactive biometric authentication via toggle switch used
- User can either have passcode only, passcode & biometric
- Passcode is used as a fallback if biometric authentication is falling
- User can forget password then they will have to verify their identity using email and password to reset the PIN
<br>

### Rewards 🍭
- To earn rewards, the user must create a goal
- User can create an expense goal or a savings goal wihtin a user selected time frame
- When the user completed a goal they will earn a reward of 5 Candy Canes
- User can see their goal history
- The cancy canes earned can be used to unlock new plant patches in the Candy Garden
<br>

### Set Goal Target 🎯
- User can set a minimum weekly goal and maximum weekly expenditure limit
- The user will be able to see their progress in the home screen
<br>

## Unit Tests Passed
<br>
<img width="1129" height="684" alt="Image" src="https://github.com/user-attachments/assets/ec900b42-be23-4af2-b99b-11ddc6887473" />
