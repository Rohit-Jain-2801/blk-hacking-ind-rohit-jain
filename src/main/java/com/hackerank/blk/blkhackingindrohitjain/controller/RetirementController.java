package com.hackerank.blk.blkhackingindrohitjain.controller;

import com.hackerank.blk.blkhackingindrohitjain.model.InvestmentModel;
import com.hackerank.blk.blkhackingindrohitjain.model.InvestmentTransaction;
import com.hackerank.blk.blkhackingindrohitjain.model.Transaction;
import com.hackerank.blk.blkhackingindrohitjain.model.TransactionType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class RetirementController {

    @PostMapping("/transactions:parse")
    public List<InvestmentTransaction> parseTransactions(@RequestBody List<Transaction> transactions) {
        return transactions.stream().map(InvestmentTransaction::new).toList();
    }

    @PostMapping("/transactions:validator")
    public Map<TransactionType, List<InvestmentTransaction>> validateTransactions(@RequestBody InvestmentModel investmentModel) {
        return investmentModel.getTransactions()
                              .stream()
                              .map(InvestmentTransaction::new)
                              .map(transaction -> transaction.validate(investmentModel.getWage()))
                              .collect(Collectors.groupingBy(InvestmentTransaction::getType, Collectors.toList()));
    }
}
