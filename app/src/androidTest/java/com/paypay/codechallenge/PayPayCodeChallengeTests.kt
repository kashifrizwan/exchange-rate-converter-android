package com.paypay.codechallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.paypay.codechallenge.database.ExchangeRatesDatabase
import com.paypay.codechallenge.models.ParsedExchangeRates
import com.paypay.codechallenge.repository.ExchangeRatesRepository
import com.paypay.codechallenge.repository.UserPreferences
import com.paypay.codechallenge.viewmodel.MainActivityViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.collections.ArrayList

@RunWith(MockitoJUnitRunner::class)
class PayPayCodeChallengeTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var lastUpdatedAtTestTime = 0L
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var exchangeRatesRepository: ExchangeRatesRepository

    @Before
    fun setUp() {
        mainActivityViewModel = MainActivityViewModel()
        exchangeRatesRepository = Mockito.mock(ExchangeRatesRepository::class.java)
        mainActivityViewModel.setMockRepositoryForTesting(exchangeRatesRepository)

        `when`(exchangeRatesRepository.getDataBaseInstance())
            .thenReturn(getDBInstanceWithMainThreadAccess())
    }

    /*
    - Set Input Milliseconds 5 minutes back of current time in milliseconds
    - Check if getLastUpdatedAt() returns the time difference is 5 minutes
    - Expected Result: 5
    */
    @Test
    fun checkLastUpdatedAtWithTestInput() {
        //Given
        lastUpdatedAtTestTime = Date(System.currentTimeMillis()).time - 300000 //** Minus 5 minutes from milliseconds **//
        UserPreferences.saveLastUpdatedAt(lastUpdatedAtTestTime)

        //When
        mainActivityViewModel.getLastUpdatedAt()
        val result = mainActivityViewModel.lastUpdatedAt.getOrAwaitValue()

        //Then
        assertEquals(result, 5)
    }

    /*
    - Testing Calculation of exchange rate between two currencies
    - Test input is the currency exchange rate of both currencies relative to USD
    - multipleFactor is the multiples of output currency.
    - E.g 25 PKR equals to how much INR with rates given in this test relative to USD
    */
    @Test
    fun checkExchangeRateCalculationWithTestInput() {
        //Given
        val inputCurrencyPKR = 158.77; val outputCurrencyINR = 74.84; val multipleFactor = 25.0
        val expectedResult = 11.78

        //When
        val result = mainActivityViewModel.calculateExchangeRateValue(inputCurrencyPKR, outputCurrencyINR, multipleFactor)

        //Then
        assertEquals(result.toInt(), expectedResult.toInt())
    }

    /*
    - Testing Currency Codes fetch Operation With No Existing Data
    - Using Mock Repository and Mocked Database Instance
    - Condition_1: When user has the empty database AND application has 0 milliseconds of the last record update
    - Fetching Currency Codes for the first time
    */
    @Test
    fun checkCurrencyCodesFetchOperationWithOutExistingData() = runBlocking {
        //Given
        UserPreferences.saveLastUpdatedAt(0L)
        `when`(exchangeRatesRepository.refreshExchangeRates()).thenReturn(getDummyExchangeRatesList())

        //When
        mainActivityViewModel.getCurrencyCodesFromRepo()
        val result = mainActivityViewModel.currencyCodes.getOrAwaitValue()

        //Then
        assertEquals(result.size, 3)
        assertNotNull(result.containsAll(listOf("PKR", "USD", "INR")))
    }

    /*
    - Testing Currency Codes fetch Operation With Existing Data
    - Using Mock Repository and Mocked Database Instance
    - Condition_2: When user already has currency codes in the database AND
      application has milliseconds of the last record update
    */
    @Test
    fun checkCurrencyCodesFetchOperationWithExistingData() = runBlocking {
        //Given -- Entered dummy milliseconds to reflect that DB already has data **//
        UserPreferences.saveLastUpdatedAt(300000L)
        exchangeRatesRepository.insertExchangeRatesToDB(getDummyExchangeRatesList())

        //When
        mainActivityViewModel.getCurrencyCodesFromRepo()
        val result = mainActivityViewModel.currencyCodes.getOrAwaitValue()

        //Then
        assertEquals(result.size, 3)
        assertNotNull(result.containsAll(listOf("PKR", "USD", "INR")))
    }

    /*
    - Testing Calculation of exchange rates with the respect to given currency and it's multiple factor
    - Below Test Case has PKR as input currency and 200 is it's multiple factor
    - All rates to be calculated equivalent to 200 PKR
    - Condition_1: With Outdated Database Record
    */
    @Test
    fun getCalculatedExchangeRatesWithNewData() = runBlocking {
        //Given
        UserPreferences.saveLastUpdatedAt(Date(System.currentTimeMillis()).time - 2000000)
        `when`(exchangeRatesRepository.refreshExchangeRates()).thenReturn(getDummyExchangeRatesList())
        exchangeRatesRepository.insertExchangeRatesToDB(getDummyExchangeRatesList())

        //When
        mainActivityViewModel.getCalculatedExchangeRates(200.0, 0)
        val result = mainActivityViewModel.exchangeRates.getOrAwaitValue()

        //Then
        assertEquals(result.size, 3)
        assertEquals(result[0].exchangeRate.toInt(), 200)
        assertEquals(result[1].exchangeRate.toInt(), 1)
        assertEquals(result[2].exchangeRate.toInt(), 93)
    }

    /*
    - Testing Calculation of exchange rates with the respect to given currency and it's multiple factor
    - Below Test Case has USD as input currency and 1.0 is it's multiple factor
    - All rates to be calculated equivalent to 1.0 USD
    - Condition_2: With Updated Database Record
    */
    @Test
    fun getCalculatedExchangeRatesWithExistingData() = runBlocking {
        //Given
        UserPreferences.saveLastUpdatedAt(Date(System.currentTimeMillis()).time - 1200000)
        exchangeRatesRepository.insertExchangeRatesToDB(getDummyExchangeRatesList())

        //When
        mainActivityViewModel.getCalculatedExchangeRates(1.0, 1)
        val result = mainActivityViewModel.exchangeRates.getOrAwaitValue()

        //Then
        assertEquals(result.size, 3)
        assertEquals(result[0].exchangeRate.toInt(), 157)
        assertEquals(result[1].exchangeRate.toInt(), 1)
        assertEquals(result[2].exchangeRate.toInt(), 74)
    }

    /*
    - Validate the Data Outdated Check
    - Condition_1: Pre Set the Outdated time in shared preferences
    */
    @Test
    fun validateIfExchangeRatesOutdated() {
        //Given
        val repository = ExchangeRatesRepository()
        UserPreferences.saveLastUpdatedAt(Date(System.currentTimeMillis()).time - 2000000)

        //When
        val result = repository.isExchangeRatesOutdated(mainActivityViewModel.timeDifferenceInMillis())

        //Then
        assertTrue(result)
    }

    /*
    - Validate the Data Outdated Check
    - Condition_2: Pre Set the valid time in shared preferences
    */
    @Test
    fun validateIfExchangeRatesNotOutdated() {
        //Given
        val repository = ExchangeRatesRepository()
        UserPreferences.saveLastUpdatedAt(Date(System.currentTimeMillis()).time - 1200000)

        //When
        val result = repository.isExchangeRatesOutdated(mainActivityViewModel.timeDifferenceInMillis())

        //Then
        assertFalse(result)
    }

    private fun getDBInstanceWithMainThreadAccess(): ExchangeRatesDatabase? {
        return Room.databaseBuilder(PayPayCodeChallenge.context!!, ExchangeRatesDatabase::class.java,
            "ExchangeRatesTestDB.db").allowMainThreadQueries().build()
    }

    private fun getDummyExchangeRatesList(): ArrayList<ParsedExchangeRates> {
        val inputTestDataList = ArrayList<ParsedExchangeRates>()
        inputTestDataList.add(ParsedExchangeRates("PKR", 157.8))
        inputTestDataList.add(ParsedExchangeRates("USD", 1.0))
        inputTestDataList.add(ParsedExchangeRates("INR", 74.0))
        return inputTestDataList
    }
}