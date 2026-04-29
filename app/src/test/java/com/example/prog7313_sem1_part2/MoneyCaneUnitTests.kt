package com.example.prog7313_sem1_part2

import android.graphics.Color
import org.junit.Test

import org.junit.Assert.*

//(Lackner, 2021)
class MoneyCaneUnitTests {

//WALLET FEATURE TESTS

    //creating method to test wallet feature from Wallet Fragments
    private fun ValidateWallet(name: String?, balance: String?): String?
    {
        if(name.isNullOrEmpty()) //error message if name is not entered
        {
            return "Please enter the wallet's name"
        }
        if(balance.isNullOrEmpty()) //error message if balance input is empty
        {
            return "Please enter initial wallet balance"
        }
        val Validbalance = balance.toDoubleOrNull() ?:
            return "Please enter valid (number) initial wallet balance" //balance must be a number thus double data type

        if (Validbalance <= 0.0) //error message if balance is not a valid number
        {
            return "Please enter valid (number) initial wallet balance"
        }
        return null
    }

    //method to calculate total of all wallets created
    private fun calculateTotalBalance(walletList:List<Wallet>): String {
        val total = walletList.sumOf { item -> item.balance }
        return "R${total.toInt()}"
    }

    //method to save wallet
    private fun SaveWallet(id: String, name:String, balance: Double): Wallet
    {
        return Wallet(id=id, name=name, balance=balance) //data is saved to DB
    }

    //method to edit existing wallet
    private fun EditWallet(existing: Wallet, name: String, balance: Double): Wallet
    {
        return existing.copy(name=name, balance=balance)
    }

    //method to filter wallets when typed on search bar
    private fun filterWallets(walletList:List<Wallet>, query: String?): List<Wallet>
    {
        return if (query.isNullOrEmpty())
            {
                walletList
            } else {
            //filtering letter by letter ignoring the letter casing
            walletList.filter { item -> item.name.contains(query, ignoreCase = true) }
        }
    }

    @Test
    fun walletNameIsEmptyTest()
    {
        val result = ValidateWallet(null, "6300")
        assertEquals("Please enter the wallet's name", result) //(Codetutor, 2025)
    }

    @Test
    fun walletBalanceIsEmptyTest()
    {
        val result = ValidateWallet("Bank Card", null)
        assertEquals("Please enter initial wallet balance", result)
    }

    @Test
    fun walletBalanceDoubleTest()
    {
        val result = ValidateWallet("Bitcoin", "hundred rand")
        assertEquals("Please enter valid (number) initial wallet balance", result)
    }

    //(Sonawane, 2019)
    @Test
    fun walletSavesTest()
    {
        val name="Cash"
        val balance= "2892"
        val result = ValidateWallet(name, balance)
        val wallet = SaveWallet(id="1", name=name, balance=balance.toDouble())
        assertEquals("Cash", wallet.name)
        assertEquals(2892.0, wallet.balance, 0.0)
    }

    @Test
    fun editWalletTest()
    {
        val current = Wallet(id="2", name="FNB Card", balance=16000.0)
        val change = EditWallet(current, name="FNB Card", balance = 25000.0)
        assertEquals("2", change.id)
        assertEquals("FNB Card", change.name)
        assertEquals(25000.0, change.balance, 0.0)
    }

    @Test
    fun totalWalletBalanceCalculationTest()
    {
        val wallets = listOf(
            Wallet(id="1", name="Bank", balance=18000.0),
            Wallet(id="2", name="Cash", balance=1000.0),
            Wallet(id="3", name="BitCoin", balance=5600.0)
        )
        val result = calculateTotalBalance(wallets)
        assertEquals("R24600", result)

    }

    @Test
    fun searchOneWalletTest()
    {
        val wallets = listOf(
            Wallet(id="1", name="Bank", balance=18000.0),
            Wallet(id="2", name="Cash", balance=1000.0),
            Wallet(id="3", name="BitCoin", balance=5600.0)
        )
        val result = filterWallets(wallets, "bit")
        assertEquals("BitCoin", result[0].name)

    }

//CATEGORY FEATURE TESTS

    //creating method to test category feature from Category Fragments
    private fun ValidateCategory(name: String?, limit: String?): String?
    {
        if(name.isNullOrEmpty()) //error message if name is not entered
        {
            return "Please enter a category name"
        }
        if(limit.isNullOrEmpty()) //error message if amount input is empty
        {
            return "Please enter the category's monthly limit"
        }
        val ValidLimit = limit.toDoubleOrNull() ?:
        return "Please enter valid (number) amount" //amount must be a number thus double data type

        if (ValidLimit <= 0.0) //error message if balance is not a valid number
        {
            return "Please enter valid (number) amount"
        }
        return null
    }

        //method to save category
        private fun SaveCategory(id: String, name:String, limit: Double, type: String): Category
        {
            return Category(id=id, name=name, limit=limit, type=type) //data is saved to DB
        }

        //method to edit existing category
        private fun EditCategory(existing: Category, name: String, limit: Double, type: String): Category
        {
            return existing.copy(name=name, limit=limit, type=type)
        }

        @Test
        fun categoryNameIsEmptyTest()
        {
            val result = ValidateCategory(null, "6300")
            assertEquals("Please enter a category name", result)
        }

        @Test
        fun categoryLimitIsEmptyTest()
        {
            val result = ValidateCategory("Business", null)
            assertEquals("Please enter the category's monthly limit", result)
        }

        @Test
        fun categoryLimitDoubleTest()
        {
            val result = ValidateCategory("Food", "hundred rand")
            assertEquals("Please enter valid (number) amount", result)
        }

        @Test
        fun categorySavesTest()
        {
            val name="Groceries"
            val limit= "3600"
            val type = "expense"
            val result = ValidateCategory(name, limit)
            val category = SaveCategory(id="1", name=name, limit=limit.toDouble(), type=type)
            assertEquals("Groceries", category.name)
            assertEquals(3600.0, category.limit, 0.0)
            assertEquals("expense", category.type)
            assertEquals(0.0, category.spent,0.0)
        }

        @Test
        fun editCategoryTest()
        {
            val current = Category(id="2", name="Insurance", limit=6000.0, type="expense", spent = 2600.0)
            val change = EditCategory(current, name="Furniture", limit = 25000.0, type="expense")
            assertEquals("2", change.id)
            assertEquals("expense", change.type)
            assertEquals("Furniture", change.name)
            assertEquals(25000.0, change.limit, 0.0)
            assertEquals(2600.0, change.spent, 0.0)
        }

//BUDGET GOALS & ACHIEVEMENT FEATURE TESTS

    //method to test that goal inputs are valid before clicking the save goal button
    private fun ValidateGoal(minGoal: String?, maxGoal: String?): String?
    {
        if(minGoal.isNullOrEmpty()) //error message if min goal field is empty
        {
            return "Please enter minimum goal"
        }
        if(maxGoal.isNullOrEmpty()) //error message if max goal field is empty
        {
            return "Please enter maximum goal"
        }

        val min = minGoal.toDoubleOrNull()?: //error message if min goal field is not a number
            return "Please enter valid minimum goal"

        val max = maxGoal.toDoubleOrNull()?: //error message if min goal field is not a number
        return "Please enter valid maximum goal"

        if(min<= 0.0 || max <=0.0)
        {
            return "Please enter valid goal (more than zero)"
        }
        if(min>= max) //a valid goal should have a small minGoal value and bigger maxGoal value
        {
            return "Minimum goal must be less than maximum goal"
        }

        return null
    }

    //method to check that canes achieved increments
    private fun incrementCanes(oldCount: Int): Int
    {
        return oldCount +1
    }

    //method to test automated budgeting suggestion after submitting min&max goal
    private fun calculateStatusText(currentSpending: Double, minGoal: Double, maxGoal: Double): String
    {
        val percentage = ((currentSpending * 100.0)/maxGoal).toInt()

        return when {
            percentage > 100 -> "Budget Exceeded" //when transactions are more than monthly maxGoal
            percentage == 100 -> "At Limit" //when transactions reach monthly max Goal
            currentSpending >= minGoal && currentSpending <= maxGoal -> "Goal Achieved!" //user acheves goal if transactions stay within goal set

            //user is given advice on good spending habits to achieve rewards/goals
            else -> "Spend R%.2f/day to stay safe".format((maxGoal - currentSpending) / 30)
        }
    }

    @Test
    fun minGoalIsEmptyTest()
    {
        val result = ValidateGoal(null,"10000")
        assertEquals("Please enter minimum goal", result)
    }

    @Test
    fun maxGoalIsEmptyTest()
    {
        val result = ValidateGoal("3000", null)
        assertEquals("Please enter maximum goal", result)
    }

    @Test
    fun minIsBiggerThanMaxTest()
    {
        val result = ValidateGoal("10000", "3000")
        assertEquals("Minimum goal must be less than maximum goal", result)
    }

    @Test
    fun statusTextCalculationTest()
    {
        val result = calculateStatusText(2400.0, 1000.0, 6000.0)
        assertEquals("Goal Achieved!", result)
    }

    @Test
    fun automatedStatusCalculationTest()
    {
        val result = calculateStatusText(2400.0, 3000.0, 6000.0)
        assertEquals("Spend R120.00/day to stay safe", result)
    }

    @Test
    fun caneIncrementsTest()
    {
        val result = incrementCanes(3)
        assertEquals(4, result)
    }


//TRANSACTIONS FEATURE TESTS

    //creating method to test transaction feature from Transaction_Fragments
    private fun ValidateTransaction(name: String?, amount: String?, content: String?, categoryId:String?): String?
    {
        if(name.isNullOrEmpty()) //error message if name is not entered
        {
            return "Enter transaction name"
        }
        if(content.isNullOrEmpty()) //error message if content input is empty
        {
            return "Enter Description"
        }
        if(categoryId.isNullOrEmpty()) //error message if category ID input is empty
        {
            return "Please pick a category for your transaction"
        }
        if(amount.isNullOrEmpty()) //error message if amount is empty
        {
            return "Enter Amount"
        }
        val validAmount = amount.toDoubleOrNull() ?:
        return "Please enter amount more than R0" //amount must be a number thus double data type

        if (validAmount <= 0.0) //error message if amount is not a valid number
        {
            return "Please enter amount more than R0"
        }
        return null
    }

    //method to save transaction
    private fun SaveTransaction(id: String, name:String, amount: Double, content: String, type: String, date: String, imageUrl:String, categoryId: String): Transaction
    {
        return Transaction(id=id, name=name, amount=amount, content=content, type=type, date=date, imageUrl=imageUrl, categoryId=categoryId) //data is saved to DB
    }

    //method for uploading an image. Image URL is saved to firebase. Actual Image is in supabase bucket
    private fun ImageUpload(selectedImageUri: String?): String?
    {
        if(selectedImageUri.isNullOrEmpty())
        {
            return null
        }
        return "https://image.storage.supabase.co/storage/v1/object/public/images/demo"
    }

    @Test
    fun transactionNameIsEmptyTest()
    {
        val result = ValidateTransaction(null, "200", content = "Purple Dress", categoryId = "Clothes")
        assertEquals("Enter transaction name", result)
    }

    @Test
    fun transactionAmountIsEmptyTest()
    {
        val result = ValidateTransaction("Petrol", null, content="Poured full tank", categoryId = "Fuel")
        assertEquals("Enter Amount", result)
    }

    @Test
    fun transactionAmountDoubleTest()
    {
        val result = ValidateTransaction("Petrol", "1200.0", content="Engen Fuel Station", categoryId = "Diesel")
        assertNull(result) //(Codetutor, 2025)
    }

    @Test
    fun transactionContentIsEmptyTest()
    {
        val result = ValidateTransaction("Bought Lunch", "70", content = null, categoryId = "Food")
        assertEquals("Enter Description", result)
    }

    @Test
    fun transactionCategoryIsEmptyTest()
    {
        val result = ValidateTransaction("Bought Lunch", "70", content = "Angelos Cafe", categoryId = null)
        assertEquals("Please pick a category for your transaction", result)
    }

    @Test
    fun transactionSavesTest()
    {
        val transaction = SaveTransaction(id="1", name="New Audi", amount = 6000000.0, content="Birthday Gift", type="expense", date="29/4/2026", imageUrl="https://car/reciept/image.png", categoryId = "Car")

        //assert equals each variable
        assertEquals("1", transaction.id)
        assertEquals("New Audi", transaction.name)
        assertEquals(6000000.0, transaction.amount, 0.0)
        assertEquals("Birthday Gift", transaction.content)
        assertEquals("expense", transaction.type)
        assertEquals("29/4/2026", transaction.date)
        assertEquals("https://car/reciept/image.png", transaction.imageUrl)
        assertEquals("Car", transaction.categoryId)
    }

    @Test
    fun imageUploadsTest()
    {
        val result = ImageUpload("selected/image/uri/demo2")
        assertNotNull(result)
    }

//REGISTRATION AND LOGIN FEATURE TESTS

    private fun ValidLogin(email: String?, password: String?): String?
    {
        //error message if email or password is empty
        if(email.isNullOrEmpty() || password.isNullOrEmpty())
        {
            return "please enter credentials"
        }
        return null
    }

    private fun ValidRegister(email: String?, password: String?, confirmPassword: String?): String?
    {
        //error message if email or password is empty
        if(email.isNullOrEmpty() || password.isNullOrEmpty())
        {
            return "please enter credentials"
        }

        //error message if password entered does not match the confirm password field
        if(password != confirmPassword)
        {
            return "Passwords do not match, please try again"
        }

        //if everything is correct then user is registered and can login
        if(email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword)
        {
            return "Registration successful!"
        }

        return null
    }

    @Test
    fun loginCredentialsEmptyTest()
    {
        val result = ValidLogin(null, null)
        assertEquals("please enter credentials", result)
    }

    @Test
    fun loginEmailEmptyTest()
    {
        val result = ValidLogin(null, "password@123")
        assertEquals("please enter credentials", result)
    }

    @Test
    fun loginPasswordEmptyTest()
    {
        val result = ValidLogin("johndoe@gmail.com", null)
        assertEquals("please enter credentials", result)
    }

    @Test
    fun loginValidTest()
    {
        val result = ValidLogin("johndoe@gmail.com", "password@123")
        assertNull(result)
    }

    @Test
    fun registerCredentialsEmptyTest()
    {
        val result = ValidRegister(null, null, null)
        assertEquals("please enter credentials", result)
    }

    @Test
    fun registerPasswordMismatchTest()
    {
        val result = ValidRegister("johndoe@gmail.com", "password@123", "PASSWORD!123")
        assertEquals("Passwords do not match, please try again", result)
    }

    @Test
    fun registerValidTest()
    {
        val result = ValidRegister("johndoe@gmail.com", "password@123", "password@123")
        assertEquals("Registration successful!", result)
    }

}

/*
References

Codetutor. 2025. Explained: all junit assertions you need with examples | android unit testing. (version 8) [source code]. Available at: < https://youtu.be/Kmu3i68A8fg?si=ZPW7BwJNrx_xXhrJ > [Accessed 26 April 2026]
Lackner, P. 2021. Writing our first unit tests - Testing on Android - Part 3. (Version 8) [source code]. Available at: < https://youtu.be/W0ag98EDhGc?si=am2HGbNVkPJA3j3B > [Accessed 29 April 2026]
Sonawane, P. 2019. Better Kotlin unit testing with mock helper functions. (version 8) [source code]. Available at: < https://medium.com/@june.pravin/better-kotlin-unit-testing-with-mock-helper-functions-38dc1a6c4684 > [Accessed 29 April 2026]

*/