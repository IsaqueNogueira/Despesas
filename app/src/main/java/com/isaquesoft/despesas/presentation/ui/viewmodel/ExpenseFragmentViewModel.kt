package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.ExpenseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class ExpenseFragmentViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private var categoriesSave = false
    private var calendar = Calendar.getInstance()
    private val _expenseState by lazy { MutableLiveData<ExpenseState>() }
    val expenseState: LiveData<ExpenseState>
        get() = _expenseState

    fun clickPrevButton() {
        calendar.add(Calendar.MONTH, -1)
        _expenseState.postValue(ExpenseState.DateText(calendar))
    }

    fun setCalendarSelected(calendarSelected: Calendar?) {
        if (calendarSelected != null) {
            calendar = calendarSelected
        }
    }

    fun clickNextButton() {
        calendar.add(Calendar.MONTH, 1)
        _expenseState.postValue(ExpenseState.DateText(calendar))
    }

    fun initCalendar() {
        _expenseState.postValue(ExpenseState.DateText(calendar))
    }

    fun getAllExpense(minDate: Long, maxDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val listExpensePrimary = expenseRepository.getAllExpense(minDate, maxDate)

            for (expense in listExpensePrimary) {
                if (expense.repeat && expense.installments == 0) {
                    val date = expense.date
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(date)
                    calendar.add(Calendar.MONTH, 1)

                    val expensesNextMonth = expenseRepository.getAllExpense(
                        calendar.timeInMillis,
                        calendar.timeInMillis,
                    )
                    val itemsRepeat = expensesNextMonth.filter { it.repeat && it.installments == 0 }

                    // Verifica se já existe uma despesa fixa para o mês seguinte
                    val existingExpense =
                        itemsRepeat.firstOrNull { it.description == expense.description && it.value == expense.value }
                    if (existingExpense == null) {
                        val newExpense = Expense(
                            description = expense.description,
                            value = expense.value,
                            dateCreated = expense.dateCreated,
                            date = calendar.timeInMillis,
                            repeat = expense.repeat,
                            installments = expense.installments,
                            category = expense.category,
                            iconPosition = expense.iconPosition,
                            corIcon = expense.corIcon,
                        )

                        expenseRepository.insertExpense(newExpense)
                    }
                }
            }
            val listExpense = expenseRepository.getAllExpense(minDate, maxDate)
            _expenseState.postValue(ExpenseState.ShowExpenses(listExpense))
        }
    }

    fun getAllExpenseByDescriptionAsc(minDate: Long, maxDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val listExpensePrimary =
                expenseRepository.getAllExpenseByDescriptionAsc(minDate, maxDate)

            for (expense in listExpensePrimary) {
                if (expense.repeat && expense.installments == 0) {
                    val date = expense.date
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(date)
                    calendar.add(Calendar.MONTH, 1)

                    val expensesNextMonth = expenseRepository.getAllExpenseByDescriptionAsc(
                        calendar.timeInMillis,
                        calendar.timeInMillis,
                    )
                    val itemsRepeat = expensesNextMonth.filter { it.repeat && it.installments == 0 }

                    // Verifica se já existe uma despesa fixa para o mês seguinte
                    val existingExpense =
                        itemsRepeat.firstOrNull { it.description == expense.description && it.value == expense.value }
                    if (existingExpense == null) {
                        val newExpense = Expense(
                            description = expense.description,
                            value = expense.value,
                            dateCreated = expense.dateCreated,
                            date = calendar.timeInMillis,
                            repeat = expense.repeat,
                            installments = expense.installments,
                            category = expense.category,
                            iconPosition = expense.iconPosition,
                            corIcon = expense.corIcon,
                        )

                        expenseRepository.insertExpense(newExpense)
                    }
                }
            }
            val listExpense = expenseRepository.getAllExpenseByDescriptionAsc(minDate, maxDate)
            _expenseState.postValue(ExpenseState.ShowExpenses(listExpense))
        }
    }

    fun getAllExpenseDateDesc(minDate: Long, maxDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val listExpensePrimary = expenseRepository.getAllExpenseDateDesc(minDate, maxDate)

            for (expense in listExpensePrimary) {
                if (expense.repeat && expense.installments == 0) {
                    val date = expense.date
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(date)
                    calendar.add(Calendar.MONTH, 1)

                    val expensesNextMonth = expenseRepository.getAllExpenseDateDesc(
                        calendar.timeInMillis,
                        calendar.timeInMillis,
                    )
                    val itemsRepeat = expensesNextMonth.filter { it.repeat && it.installments == 0 }

                    // Verifica se já existe uma despesa fixa para o mês seguinte
                    val existingExpense =
                        itemsRepeat.firstOrNull { it.description == expense.description && it.value == expense.value }
                    if (existingExpense == null) {
                        val newExpense = Expense(
                            description = expense.description,
                            value = expense.value,
                            dateCreated = expense.dateCreated,
                            date = calendar.timeInMillis,
                            repeat = expense.repeat,
                            installments = expense.installments,
                            category = expense.category,
                            iconPosition = expense.iconPosition,
                            corIcon = expense.corIcon,
                        )

                        expenseRepository.insertExpense(newExpense)
                    }
                }
            }
            val listExpense = expenseRepository.getAllExpenseDateDesc(minDate, maxDate)
            _expenseState.postValue(ExpenseState.ShowExpenses(listExpense))
        }
    }

    fun getAllExpenseDateCres(minDate: Long, maxDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val listExpensePrimary = expenseRepository.getAllExpenseDateCres(minDate, maxDate)

            for (expense in listExpensePrimary) {
                if (expense.repeat && expense.installments == 0) {
                    val date = expense.date
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(date)
                    calendar.add(Calendar.MONTH, 1)

                    val expensesNextMonth = expenseRepository.getAllExpenseDateCres(
                        calendar.timeInMillis,
                        calendar.timeInMillis,
                    )
                    val itemsRepeat = expensesNextMonth.filter { it.repeat && it.installments == 0 }

                    // Verifica se já existe uma despesa fixa para o mês seguinte
                    val existingExpense =
                        itemsRepeat.firstOrNull { it.description == expense.description && it.value == expense.value }
                    if (existingExpense == null) {
                        val newExpense = Expense(
                            description = expense.description,
                            value = expense.value,
                            dateCreated = expense.dateCreated,
                            date = calendar.timeInMillis,
                            repeat = expense.repeat,
                            installments = expense.installments,
                            category = expense.category,
                            iconPosition = expense.iconPosition,
                            corIcon = expense.corIcon,
                        )

                        expenseRepository.insertExpense(newExpense)
                    }
                }
            }
            val listExpense = expenseRepository.getAllExpenseDateCres(minDate, maxDate)
            _expenseState.postValue(ExpenseState.ShowExpenses(listExpense))
        }
    }

    fun getFirstAndLastDayOfMonth(calendar: Calendar): ArrayList<Long> {
        val localDate =
            LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1)
        val firstDayOfMonth = localDate.toEpochDay() * 86400000
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth)
        val lastDayOfMonthMillis = calendar.timeInMillis
        return arrayListOf(firstDayOfMonth, lastDayOfMonthMillis)
    }

    fun fullExpenseSum(expenses: List<Expense>) {
        val deviceLocale = Locale.getDefault()
        val currencySymbol = Currency.getInstance(deviceLocale).symbol
        var totalValue = BigDecimal.ZERO
        var totalBalance = BigDecimal.ZERO
        for (expense in expenses) {
            val originalString = expense.value
            val onlyLetters = originalString.replace(Regex("[\\d.,\\s]"), "")
            if (onlyLetters != currencySymbol) {
            } else {
                val valueString = expense.value.replace(currencySymbol, "").trim().replace(",", ".")
                val lastDotIndex = valueString.lastIndexOf('.')
                val formattedValueString = if (lastDotIndex >= 0) {
                    valueString.substring(0, lastDotIndex).replace(".", "") + valueString.substring(
                        lastDotIndex,
                    )
                } else {
                    valueString
                }
                val value = BigDecimal(formattedValueString)
                totalValue += value

                if (expense.paidOut == false) {
                    totalBalance += value
                }
            }
        }
        val numberFormat = NumberFormat.getCurrencyInstance(deviceLocale)
        val formattedFullValue = numberFormat.format(totalValue)
        val formattedBalance = numberFormat.format(totalBalance)
        _expenseState.postValue(
            ExpenseState.ShowValueEndBalance(
                formattedFullValue,
                formattedBalance,
            ),
        )
    }

    fun deleteAllExpense(expense: List<Expense>) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.deleteAllExpense(expense)
        }
    }

    fun updateExpense(expense: Expense, startDate: Long, endDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.updateExpense(expense)
            val listExpense = expenseRepository.getAllExpense(startDate, endDate)
            fullExpenseSum(listExpense)
        }
    }

    fun deleteExpense(expense: Expense, startDate: Long, endDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.deleteExpense(expense)
            val listExpense = expenseRepository.getAllExpense(startDate, endDate)
            fullExpenseSum(listExpense)
        }
    }

    fun getAllCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val category: List<Category> = expenseRepository.getAllCategory()
            _expenseState.postValue(ExpenseState.ShowAllCategory(category))
        }
    }

    fun insertAllCategory(category: List<Category>) {
        if (!categoriesSave) {
            viewModelScope.launch(Dispatchers.IO) {
                expenseRepository.insertAllCategory(category)
                categoriesSave = true
            }
        }
    }
}
