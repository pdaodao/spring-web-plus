package com.github.pdaodao.springwebplus.ai;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.store.EmbeddingUtil;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileUrlResource;

import java.util.List;

public class ReaderMain {

    public static final String filePath(){
        return "/Users/peng/Documents/2025医疗/建设内容重复审查/历史项目/建设方案1.docx";
    }

    public static void main(String[] args) throws Exception{
         ApachePoiDocumentParser parser = new ApachePoiDocumentParser();
//        ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
        final dev.langchain4j.data.document.Document document = parser.parse(FileUtil.getInputStream(filePath()));

        DocumentBySentenceSplitter sp = new DocumentBySentenceSplitter(600, 100);
        final String[] sts = sp.split(document.text());
        for(final String text: sts){
            final float[] ft = EmbeddingUtil.embeding(text);
            System.out.println(text.replaceAll("\n", ""));
        }

        System.out.println("hello");
    }

    public static void main2(String[] args) throws Exception{
        TikaDocumentReader reader = new TikaDocumentReader(new FileUrlResource("/Users/peng/Documents/2025医疗/建设内容重复审查/历史项目/建设方案1.docx"));

        final List<Document> list = reader.get();
        // TextSplitter sp = new SentenceSplitter(500);
        TextSplitter sp = new TokenTextSplitter(true);
        final List<Document> sps = sp.split(list.get(0));
        for(final Document d: sps){
            System.out.println(d.getText());
            System.out.println(StrUtil.repeat('=', 100)+StrUtil.length(d.getText()));
        }

        System.out.println("hello");
    }
}
