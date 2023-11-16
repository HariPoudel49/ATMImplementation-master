package com.progressoft.induction.atm;

import com.progressoft.induction.atm.Impl.ATMImpl;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String args[]) {
        // your code here

        ATMImpl atm = new ATMImpl();
        System.out.println(atm.checkBalance("123456789"));

        List<Banknote> banknotes = atm.withdraw("123456789", BigDecimal.valueOf(1000));
        banknotes.forEach(System.out::println);

        System.out.println(atm.checkBalance("123456789"));
    }
}
