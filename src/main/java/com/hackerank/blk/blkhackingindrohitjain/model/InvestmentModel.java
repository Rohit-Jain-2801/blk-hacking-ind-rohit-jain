package com.hackerank.blk.blkhackingindrohitjain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentModel {

    private Integer age;
    private Double wage;
    private Double inflation;
    private List<ContraintPeriod> q;
    private List<ContraintPeriod> p;
    private List<ContraintPeriod> k;
    private List<Transaction> transactions;
}
