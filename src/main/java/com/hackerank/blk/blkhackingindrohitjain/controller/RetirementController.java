package com.hackerank.blk.blkhackingindrohitjain.controller;

import com.hackerank.blk.blkhackingindrohitjain.model.ContraintPeriod;
import com.hackerank.blk.blkhackingindrohitjain.model.InvestmentModel;
import com.hackerank.blk.blkhackingindrohitjain.model.InvestmentTransaction;
import com.hackerank.blk.blkhackingindrohitjain.model.Transaction;
import com.hackerank.blk.blkhackingindrohitjain.model.TransactionType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class RetirementController {

    // TODO: Add service layer for logic handling

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

    // TODO: add validator logic
    @PostMapping("/transactions:filter")
    public List<InvestmentTransaction> filterTransactions(@RequestBody InvestmentModel investmentModel) {
        List<InvestmentTransaction> processedTransactions = investmentModel.getTransactions().stream().map(InvestmentTransaction::new).toList();

        processedTransactions.forEach(transaction -> {
                    // Apply q rules (Fixed override)
                    // If multiple q periods match, the one with the latest start date wins.
                    Optional.ofNullable(investmentModel.getQ())
                            .flatMap(rules ->
                                rules.stream()
                                     .filter(rule -> rule.getFixed() != null && rule.getStart() != null && rule.getEnd() != null)
                                     .filter(rule -> !transaction.getDate().isBefore(rule.getStart()) && !transaction.getDate().isAfter(rule.getEnd()))
                                     .max(Comparator.comparing(ContraintPeriod::getStart))
                            ).ifPresent(rule -> transaction.setRemanent(rule.getFixed()));

                    // Apply p rules (Extra addition)
                    // p rules are cumulative and apply after q rules.
                    Optional.ofNullable(investmentModel.getP())
                            .flatMap(rules -> Optional.of(
                                rules.stream()
                                     .filter(rule -> rule.getExtra() != null && rule.getStart() != null && rule.getEnd() != null)
                                     .filter(rule -> !transaction.getDate().isBefore(rule.getStart()) && !transaction.getDate().isAfter(rule.getEnd()))
                                     .mapToDouble(ContraintPeriod::getExtra)
                                     .sum()
                            )).ifPresent(extraAmount -> transaction.setRemanent(transaction.getRemanent() + extraAmount));
                });

        // Apply k rules (Filtering/Grouping)
        // If k periods are defined, only return transactions that fall within any of them.
        if (investmentModel.getK() != null && !investmentModel.getK().isEmpty()) {
            return processedTransactions.stream()
                    .filter(transaction ->
                            investmentModel.getK()
                                           .stream()
                                           .filter(rule -> rule.getStart() != null && rule.getEnd() != null)
                                           .anyMatch(rule -> !transaction.getDate().isBefore(rule.getStart()) && !transaction.getDate().isAfter(rule.getEnd()))
                    ).toList();
        }

        return processedTransactions;
    }

    @PostMapping("/returns:nps")
    public Map<String, Object> calculateNpsReturns(@RequestBody List<InvestmentTransaction> transactions) {
        double totalInvested = transactions.stream().mapToDouble(InvestmentTransaction::getRemanent).sum();
        double rate = 0.0711;       // NPS Logic: 7.11% annual return
        double futureValue = totalInvested * (1 + rate); 
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalInvested", totalInvested);
        response.put("futureValue", futureValue);
        response.put("taxBenefit", Math.min(totalInvested, 200000));
        return response;
    }

    @PostMapping("/returns:index")
    public Map<String, Object> calculateIndexReturns(@RequestBody List<InvestmentTransaction> transactions) {
        double totalInvested = transactions.stream().mapToDouble(InvestmentTransaction::getRemanent).sum();
        double rate = 0.1449;       // Index Fund Logic: 14.49% annual return
        double futureValue = totalInvested * (1 + rate);

        Map<String, Object> response = new HashMap<>();
        response.put("totalInvested", totalInvested);
        response.put("futureValue", futureValue);
        return response;
    }

    @GetMapping("/performance")
    public Map<String, Object> getPerformance() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalMemory", runtime.totalMemory());
        metrics.put("freeMemory", runtime.freeMemory());
        metrics.put("maxMemory", runtime.maxMemory());
        metrics.put("availableProcessors", runtime.availableProcessors());
        return metrics;
    }
}
