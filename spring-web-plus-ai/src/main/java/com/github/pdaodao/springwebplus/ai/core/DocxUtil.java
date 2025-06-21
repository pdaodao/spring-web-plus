package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import org.apache.poi.xwpf.usermodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocxUtil {
    private static final Pattern TITLE_PATTERN1 = Pattern.compile("^(\\d+\\.)*\\d+");
    private static final Pattern TITLE_PATTERN2 = Pattern.compile("^(一|二|三|四|五|六|七|八|九|十)、");

    private static final Pattern Category_PATTERN = Pattern.compile("^(\\d+\\.)*\\d+.*\t\\d+");

    public static boolean isTitle(final String line){
        return TITLE_PATTERN1.matcher(line).find() || ( TITLE_PATTERN2.matcher(line).find() && StrUtil.length(line) < 20);
    }

    public static boolean isCategory(final String line){
        return Category_PATTERN.matcher(line).find();
    }


    public static void main(String[] args) throws Exception{
        final String path = "/Users/peng/Documents/2025医疗/建设内容重复审查/历史项目/建设方案1.docx";
        try(final InputStreamWrap wrap = InputStreamWrap.of(FileUtil.getInputStream(path))){
            final List<DocTextBlock> list = DocxUtil.parseSeq(wrap);
            for(final DocTextBlock b: list){
                System.out.println(b.toString());
            }
        }
    }

    public static void main2(String[] args) {
        boolean is = isTitle("5.1诉智办数智信访底座");
        System.out.println("hello");
    }

    public static List<DocTextBlock> parse(final InputStreamWrap wrap) throws Exception{
        final List<DocTextBlock> ret = new ArrayList<>();
        DocTextBlock last = null;
        String lastContent = "";
        try(final XWPFDocument document = new XWPFDocument(wrap.inputStream)){
            for (final XWPFParagraph ph : document.getParagraphs()) {
                final String text = ph.getText();
                if(last == null || isTitle(text)){
                    last = new DocTextBlock();
                    last.setTitle(text);
                    ret.add(last);
                    continue;
                }
                if(StrUtil.isNotBlank(text)){
                    if(StrUtil.endWith(lastContent, "。") || StrUtil.length(lastContent) < 30){
                        last.addItem(text);
                    }else{
                        last.lastAppend(text);
                    }
                    lastContent = text;
                }
            }
        }
        return ret;
    }

    public static List<DocTextBlock> parseSeq(final InputStreamWrap wrap) throws Exception{
        final List<DocTextBlock> ret = new ArrayList<>();
        DocTextBlock last = null;
        String lastContent = "";
        try(final XWPFDocument document = new XWPFDocument(wrap.inputStream)){
            for (IBodyElement element : document.getBodyElements()) {
                if (element instanceof XWPFParagraph) {
                    final XWPFParagraph ph = (XWPFParagraph) element;
                    final String text = ph.getText();
                    if(last == null || isTitle(text)){
                        last = new DocTextBlock();
                        last.setTitle(text);
                        ret.add(last);
                        continue;
                    }
                    if(StrUtil.isNotBlank(text)){
                        if(StrUtil.endWith(lastContent, "。") || StrUtil.length(lastContent) < 30){
                            last.addItem(text);
                        }else{
                            last.lastAppend(text);
                        }
                        lastContent = text;
                    }
                } else if (element instanceof XWPFTable) {
                    final XWPFTable table = (XWPFTable) element;
                }
            }
        }
        return ret;
    }
}
