package com.progressoft.induction.atm.Impl;

import com.progressoft.induction.atm.ATM;
import com.progressoft.induction.atm.Banknote;
import com.progressoft.induction.atm.exceptions.InsufficientFundsException;
import com.progressoft.induction.atm.exceptions.NotEnoughMoneyInATMException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public class ATMImpl implements ATM {
    private final BankingSystemImpl bankingSystem = new BankingSystemImpl();

    @Override
    public List<Banknote> withdraw(String accountNumber, BigDecimal amount) {
        // check required amount of money is exits in atm or not
        BigDecimal amountInAtm = bankingSystem.sumOfMoneyInAtm();
        if (amountInAtm.compareTo(amount) < 0) {
            throw new NotEnoughMoneyInATMException("Not enough money to withdraw !");
        }
        BigDecimal accountBalance = checkBalance(accountNumber);
        // check account balance is valid for withdraw or not
        if (accountBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough money to withdraw in your account !");
        }
        bankingSystem.debitAccount(accountNumber, amount);

        List<Banknote> withdrawnBanknotes = calculateWithdrawalBanknotes(amount);

        updateAtmCashMap(withdrawnBanknotes);

        return withdrawnBanknotes;
    }

    @Override
    public BigDecimal checkBalance(String accountNumber) {
        return bankingSystem.getAccountBalance(accountNumber);
    }

    private List<Banknote> calculateWithdrawalBanknotes(BigDecimal amount) {
        EnumMap<Banknote, Integer> atmCashMap = bankingSystem.atmCashMap;
        List<Banknote> banknotes = new ArrayList<>();


        List<Banknote> sortedBanknotes = new ArrayList<>(atmCashMap.keySet());
        Collections.sort(sortedBanknotes, Collections.reverseOrder());

        for (Banknote banknote : sortedBanknotes) {
            int count = amount.divideToIntegralValue(banknote.getValue()).intValue();
            count = Math.min(count, atmCashMap.get(banknote));
            for (int i = 0; i < count; i++) {
                banknotes.add(banknote);
            }
            amount = amount.subtract(banknote.getValue().multiply(BigDecimal.valueOf(count)));
        }

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            // If there is still an amount remaining, it means the ATM doesn't have enough banknotes
            throw new NotEnoughMoneyInATMException("Not enough money withdraw !");
        }

        return banknotes;
    }

    private void updateAtmCashMap(List<Banknote> withdrawnBanknotes) {
        EnumMap<Banknote, Integer> atmCashMap = bankingSystem.atmCashMap;

        for (Banknote banknote : withdrawnBanknotes) {
            atmCashMap.put(banknote, atmCashMap.get(banknote) - 1);
        }
    }
}
