package com.github.pdaodao.aicompare.service;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.hankcs.hanlp.HanLP;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocReadService {
    public String parse(final InputStreamWrap inputStreamWrap){
        final ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
        final dev.langchain4j.data.document.Document document = parser.parse(inputStreamWrap.inputStream);
        return document.text();
    }

    public String summary(final String docText){
        return HanLP.getSummary(docText, 300);
    }

    public List<String> split(final String docText){
        if(StrUtil.isBlank(docText)){
            return null;
        }
        final DocumentByParagraphSplitter sp = new DocumentByParagraphSplitter(300, 100);
        final String[] sts = sp.split(docText);
        final List<String> ret = new ArrayList<>();
        String last = "";
        for(final String text: sts){
            if(StrUtil.length(text) < 50 && StrUtil.length(last) < 100){
                last = last + text;
                continue;
            }
            if(StrUtil.length(last) > 50){
                ret.add(last);
                last = "";
            }
            ret.add(text);
        }
        return ret;
    }
}
