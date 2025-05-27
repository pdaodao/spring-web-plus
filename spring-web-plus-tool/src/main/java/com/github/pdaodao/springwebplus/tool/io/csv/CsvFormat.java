package com.github.pdaodao.springwebplus.tool.io.csv;

import lombok.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;

@Data
public class CsvFormat {
    private String delimiter = ",";
    private Character escape = null;
    private boolean skipHeaderRecord = true;
    private Boolean ignoreEmptyLines = true;
    private Character quote = '"';
    private String recordSeparator = "\r\n";
    private String nullString = "\\N";
    private QuoteMode quoteMode = QuoteMode.MINIMAL;
    private String opName = "__op";

    public static CsvFormat of(){
        return new CsvFormat();
    }

    public CSVFormat toFormat(){
        return CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter)
                .setEscape(escape)
                .setIgnoreEmptyLines(ignoreEmptyLines)
                .setQuote(quote)
                .setSkipHeaderRecord(skipHeaderRecord)
                .setRecordSeparator(recordSeparator)
                .setNullString(nullString)
                .setQuoteMode(quoteMode).get();
    }
}
