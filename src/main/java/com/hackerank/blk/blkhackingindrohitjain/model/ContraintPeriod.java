package com.hackerank.blk.blkhackingindrohitjain.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ContraintPeriod {

    private Double fixed;
    private Double extra;

    // TODO: Validate input date
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    // TODO: Validate input date
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
}
