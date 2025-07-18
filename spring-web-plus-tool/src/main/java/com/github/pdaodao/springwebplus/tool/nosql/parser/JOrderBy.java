package com.github.pdaodao.springwebplus.tool.nosql.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JOrderBy {
    private String name;

    private Boolean asc;
}
