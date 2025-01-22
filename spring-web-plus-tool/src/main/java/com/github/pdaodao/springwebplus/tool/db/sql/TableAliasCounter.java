package com.github.pdaodao.springwebplus.tool.db.sql;

import lombok.Data;

@Data
public class TableAliasCounter {
    public static final String BaseChar = "ABCDEFGHJKMNPQRSTUVWXYZ";
    private Integer selectCount = 0;

    private static String convertToAlphanumeric(long number) {
        int base = BaseChar.length();
        final StringBuilder result = new StringBuilder();
        while (number >= 0) {
            int remainder = (int) (number % base);
            result.insert(0, BaseChar.charAt(remainder));
            number /= base;
            if(number < 1){
                break;
            }
        }
        return result.toString();
    }

    public String getTableAlias(){
        return convertToAlphanumeric(selectCount++);
    }

    public static void main(String[] args) {
        TableAliasCounter holder = new TableAliasCounter();
        for(int i = 0; i < 10; i++){
            System.out.println(holder.getTableAlias());
        }
    }
}