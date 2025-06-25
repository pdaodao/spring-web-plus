package com.github.pdaodao.aicompare.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.markdown.MarkdownRenderData;
import com.deepoove.poi.plugin.markdown.MarkdownRenderPolicy;
import com.deepoove.poi.plugin.markdown.MarkdownStyle;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.github.pdaodao.aicompare.service.ZtChatService;
import com.github.pdaodao.aicompare.core.DocText;
import com.github.pdaodao.aicompare.core.DocTextBlock;
import com.github.pdaodao.aicompare.core.DocTextQuery;
import com.github.pdaodao.aicompare.core.DocxUtil;
import com.github.pdaodao.aicompare.service.DocStoreService;
import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.ResponseUtil;
import com.github.pdaodao.springwebplus.tool.data.Tuple3;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Tag(name = "对比文档管理")
@AllArgsConstructor
@RequestMapping("/compare/api/v1/doc")
public class AiDocCompareController {
    private final EmbeddingModel embeddingModel;
    private final DocStoreService docStoreService;
    private final ZtChatService chatService;

    @IgnoreLogin
    @Operation(summary = "上传历史文档")
    @PostMapping("/loadHis")
    @Parameters({@Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
            schema = @Schema(name = "file", format = "binary"))})
    public String loadHis(@RequestParam("file") final MultipartFile file) throws Exception{
        FileUploadUtil.checkIsDoc(file);
        FileUploadUtil.checkSize(file, 50);
        final long t1 = System.currentTimeMillis();
        final List<DocText> textList = new ArrayList<>();
        long index = docStoreService.count() + 1;
        try(final InputStreamWrap wrap = InputStreamWrap.of(file.getInputStream())){
            final List<DocTextBlock> blocks = DocxUtil.parse(wrap);
            for(final DocTextBlock block: blocks){
                final List<String> sps = block.split(1000);
                if(CollUtil.isEmpty(sps)){
                    continue;
                }
                for(final String sp: sps){
                    final DocText d = new DocText();
                    d.setNamespace("doc");
                    d.setTeamId(0l);
                    d.setDatasetId(0l);
                    d.setTitle(file.getOriginalFilename());
                    d.setContent(sp);
                    d.setDocId(index++);
                    textList.add(d);
                }
            }
        }
        int j = 0;
        final List<DocText> textBatchList = new ArrayList<>();
        for(final DocText d: textList){
            j++;
            textBatchList.add(d);
            if(textBatchList.size() == 20 || j == textList.size()){
               if(textBatchList.isEmpty()){
                   continue;
               }
               final List<float[]> vs = embeddingModel.embed(textBatchList.stream().map(t -> t.getContent()).collect(Collectors.toList()));
               for(int jj = 0; jj < textBatchList.size(); jj++){
                   textBatchList.get(jj).setEmbedding(vs.get(jj));
               }
               docStoreService.saveList(textBatchList);
               textBatchList.clear();
            }
        }
        return "解析保存成功耗时(s)"+((System.currentTimeMillis() - t1)/1000)+", 文本块数为:"+j;
    }


//    @IgnoreLogin
//    @GetMapping("cosineSimilarity")
//    @Operation(summary = "cosineSimilarity")
//    public String vectorSimilarTest(final String input1, final String input2){
//        final float[] v1 = embeddingModel.embed(input1);
//        final float[] v2 = embeddingModel.embed(input2);
//        final double score = SimpleVectorStore.EmbeddingMath.cosineSimilarity(v1, v2);
//        return "cosineSimilarity:"+score;
//    }

    @IgnoreLogin
    @GetMapping("down")
    @Operation(summary = "下载对比结果")
    public void downFile(final String filename, final HttpServletResponse response) throws Exception{
        final String filePath = FilePathUtil.pathJoin(FilePathUtil.userDir(), "docs/"+filename);
        Preconditions.checkArgument(FileUtil.exist(filePath), "文件不存在");
        ResponseUtil.writeFile(filePath, filename, response, true, 0);
    }

    @IgnoreLogin
    @PostMapping("/compare")
    @Operation(summary = "文档对比")
    @Parameters({@Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
            schema = @Schema(name = "file", format = "binary"))})
    public String compare(@RequestParam("file") final MultipartFile file,
                          @Parameter(description = "相似度") @RequestParam(required = false, defaultValue = "0.76") final Float score) throws Exception{
        FileUploadUtil.checkIsDoc(file);
        FileUploadUtil.checkSize(file, 50);
        final long t1 = System.currentTimeMillis();
        final List<Tuple3<Integer, String, String>> diffList = new ArrayList<>();
        try(final InputStreamWrap wrap = InputStreamWrap.of(file.getInputStream())) {
            final List<DocTextBlock> blocks = DocxUtil.parse(wrap);
            final List<DocTextQuery> queryList = new ArrayList<>();
            final List<DocTextQuery> batchQueryList = new ArrayList<>();
            int i = 0;
            for(final DocTextBlock b: blocks) {
                i++;
                if (b.getItems() == null) {
                    continue;
                }
                final List<String> sps = b.split(1000);
                for(final String sp: sps){
                    if(StrUtil.length(sp) < 10){
                        continue;
                    }
                    final DocTextQuery q = new DocTextQuery();
                    q.setContent(sp);
                    q.setMinScore(score);
                    q.setTopK(1);
                    batchQueryList.add(q);
                    if(batchQueryList.size() == 20 || i == blocks.size()){
                        if(CollUtil.isEmpty(batchQueryList)){
                            continue;
                        }
                        final List<String> texts = batchQueryList.stream().map(t -> t.getContent()).collect(Collectors.toList());
                        final List<float[]> vs = embeddingModel.embed(texts);
                        for(int j = 0; j < batchQueryList.size(); j++){
                            batchQueryList.get(j).setEmbedding(vs.get(j));
                        }
                        queryList.addAll(batchQueryList);
                        batchQueryList.clear();
                    }
                }
            }
            int index = 1;
            for(final DocTextQuery q: queryList){
                final List<DocText> result = docStoreService.query(q);
                if (CollUtil.isNotEmpty(result)) {
                    final String content = result.get(0).getTitle()+"\n"+result.get(0).getContent();
                    diffList.add(Tuple3.of(index++, q.getContent(), content));
                }
            }
        }
        final String fileName = genDocReport(diffList);
        return "疑似重复数:"+diffList.size()+",耗时(s):"+((System.currentTimeMillis() - t1) / 1000)+",对比结果文件为:"+fileName;
    }

    @IgnoreLogin
    @Operation(summary = "生成开发模块")
    @PostMapping("/genModule")
    @Parameters({@Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
            schema = @Schema(name = "file", format = "binary"))})
    public String genModule(@RequestParam("file") final MultipartFile file) throws Exception{
        FileUploadUtil.checkIsDoc(file);
        FileUploadUtil.checkSize(file, 50);
        final long t1 = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder();
        try(final InputStreamWrap wrap = InputStreamWrap.of(file.getInputStream())) {
            final List<DocTextBlock> blocks = DocxUtil.parse(wrap);
            for(final DocTextBlock b: blocks){
                if(b.isCategory()){
                    continue;
                }
                sb.append(b.toString());
            }
            Preconditions.assertTrue(sb.length() > 30000, "文档内容超过30000无法处理.");
            final String answer = chatService.client()
                    .prompt()
                    .system("你是系统开发设计架构师，根据需求提炼开发功能表格，为了方便开发团队依据设计文档进行系统开发，表格需要详细列出各个功能模块、子功能以及具体的功能项和描述:")
                    .user(sb.toString())
                    .call().content();
            final String f = markdownDoc(answer);
            long cost = (System.currentTimeMillis() - t1) / 1000;
            return "字符数:"+sb.length()+"耗时(s):"+cost+";结果文件为:"+f;
        }
    }

    private String markdownDoc(final String content) throws Exception{
        MarkdownRenderData code = new MarkdownRenderData();
        code.setMarkdown(content);
        code.setStyle(MarkdownStyle.newStyle());
        Map<String, Object> data = new HashMap<>();
        data.put("md", code);
        Configure config = Configure.builder().bind("md", new MarkdownRenderPolicy()).build();
        final String fileName = DateTimeUtil.formatDate(new Date())+ IdUtil.snowId()+".docx";
        final String filePath = FilePathUtil.pathJoin(FilePathUtil.userDir(), "docs/"+fileName);

        final String templateFile = FilePathUtil.pathJoin(FilePathUtil.userDir(), "docs/mark.docx");
        XWPFTemplate.compile(templateFile, config)
                .render(data)
                .writeToFile(filePath);
        return fileName;
    }

    public String genDocReport(final List<Tuple3<Integer, String, String>> diffList) throws Exception{
        final Map<String, Object> retMap = new HashMap<>();
        retMap.put("ds", diffList);
        retMap.put("count", diffList.size());
        final LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        final Configure config = Configure.builder()
                .bind("ds", policy).build();
        final String fileName = DateTimeUtil.formatDate(new Date())+ IdUtil.snowId()+".docx";
        final String filePath = FilePathUtil.pathJoin(FilePathUtil.userDir(), "docs/"+fileName);
        final String templateFile = FilePathUtil.pathJoin(FilePathUtil.userDir(), "docs/compdiff.docx");
        // 加载模板
        final XWPFTemplate template = XWPFTemplate.compile(templateFile, config)
                .render(retMap);
        template.writeToFile(filePath);
        return fileName;
    }
}
