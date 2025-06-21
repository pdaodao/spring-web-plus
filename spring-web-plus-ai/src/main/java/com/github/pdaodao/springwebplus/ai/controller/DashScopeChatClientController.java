/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.github.pdaodao.springwebplus.ai.core.DocText;
import com.github.pdaodao.springwebplus.ai.core.DocTextQuery;
import com.github.pdaodao.springwebplus.ai.service.DocReadService;
import com.github.pdaodao.springwebplus.ai.service.DocStoreService;
import com.github.pdaodao.springwebplus.ai.store.DocEmbeddingUtil;
import com.github.pdaodao.springwebplus.ai.store.elasticsearch.EsDocVectorStore;
import com.github.pdaodao.springwebplus.ai.store.elasticsearch.EsUtil;
import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
import com.github.pdaodao.springwebplus.base.util.ResponseUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.data.Tuple2;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController
//@RequestMapping("/chat/api/v1/client")
public class DashScopeChatClientController {

	private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

	private final ChatClient dashScopeChatClient;
	private final EmbeddingModel embeddingModel;

	public DashScopeChatClientController(ChatModel chatModel, EmbeddingModel embeddingModel) {

		this.embeddingModel = embeddingModel;

		// 构造时，可以设置 ChatClient 的参数
		// {@link org.springframework.ai.chat.client.ChatClient};
		this.dashScopeChatClient = ChatClient.builder(chatModel)
				// 实现 Logger 的 Advisor
				.defaultAdvisors(
						new SimpleLoggerAdvisor()
				)
				// 设置 ChatClient 中 ChatModel 的 Options 参数
				.defaultOptions(
						DashScopeChatOptions.builder()
								.withTopP(0.7)
								.build()
				)
				.build();
	}

	// 也可以使用如下的方式注入 ChatClient
	// public DashScopeChatClientController(ChatClient.Builder chatClientBuilder) {
	//
	//  	this.dashScopeChatClient = chatClientBuilder.build();
	// }

	/**
	 * ChatClient 简单调用
	 */
	@IgnoreLogin
	@GetMapping("/simple/chat")
	public String simpleChat() {
		return dashScopeChatClient.prompt(DEFAULT_PROMPT).call().content();
	}

	@IgnoreLogin
	@PostMapping("/loadHis")
	@Parameters({@Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
			schema = @Schema(name = "file", format = "binary"))})
	public String loadHis(@RequestParam("file") final MultipartFile file) throws Exception{
		final DocReadService docReadService = SpringUtil.getBean(DocReadService.class);
		final DocStoreService docStoreService = SpringUtil.getBean(DocStoreService.class);
		try(final InputStreamWrap wrap = InputStreamWrap.of(file.getInputStream())){
			final String docContent = docReadService.parse(wrap);
			final List<String> sps =  docReadService.split(docContent);
			final List<DocText> textList = new ArrayList<>();
			long i = 0;
			for(final String sp: sps){
				if(StrUtil.length(sp) < 1 || StrUtil.length(sp) > 2000){
					continue;
				}
				final DocText text = new DocText();
				text.setNamespace("doc");
				text.setDocId(i++);
				text.setTeamId(0l);
				text.setDatasetId(0l);
				text.setContent(sp);
				text.setEmbedding(embeddingModel.embed(sp));
				textList.add(text);
			}
			docStoreService.saveList(textList);
			return "hello";
		}
	}

	@IgnoreLogin
	@GetMapping("/emb")
	public String emb(){
		float[] ret = embeddingModel.embed("信访部门日常工作中需要梳理大量的报告，如每月的信访登记情况报告，进京访超级访登记表，各街道重点信访人员及反馈核心问题报告，信访重点领域报告、重点事项报告等，其中重点领域报告、重点事项报告需要从群众的投诉文本中进行深度挖掘才能生成，这一过程往往需要大量的人工精力投入才能达到业务目标。\n" +
				"大模型具备强大的数据处理能力和模式识别功能，可以从海量的信访记录中挖掘出潜在的趋势和规律，帮助决策者更好地理解公众需求和社会热点。利用大模型可以快速地根据输入的数据和信息生成详细的分析报告，减少人工撰写的时间成本。引入大模型来自动生成智能报告不仅可以显著提高信访工作的效率和服务质量，还能有效应对现有的一些管理难题，从而推动政府治理现代化进程。");

		return "hello";
	}

	/**
	 * ChatClient 流式调用
	 */
	@GetMapping("/stream/chat")
	public Flux<String> streamChat(HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		return dashScopeChatClient.prompt(DEFAULT_PROMPT).stream().content();
	}


	/**
	 * 图片分析接口 - 通过 URL
	 */
	@GetMapping("/image/analyze/url")
	public String analyzeImageByUrl(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt,
									@RequestParam String imageUrl) {
		try {
			// 创建包含图片的用户消息
			List<Media> mediaList = List.of(new Media(MimeTypeUtils.IMAGE_JPEG, new URI(imageUrl)));
			UserMessage message = UserMessage.builder()
					.text(prompt)
					.media(mediaList)
					.build();

			// 设置消息格式为图片
			message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

			// 创建提示词，启用多模态模型
			Prompt chatPrompt = new Prompt(message,
					DashScopeChatOptions.builder()
							.withModel("qwen-vl-max-latest")  // 使用视觉模型
							.withMultiModel(true)             // 启用多模态
							.withVlHighResolutionImages(true) // 启用高分辨率图片处理
							.withTemperature(0.7)
							.build());
			// 调用模型进行图片分析
			return dashScopeChatClient.prompt(chatPrompt).call().content();
		} catch (Exception e) {
			return "图片分析失败: " + e.getMessage();
		}
	}

	/**
	 * 图片分析接口 - 通过文件上传
	 */
	@PostMapping("/image/analyze/upload")
	public String analyzeImageByUpload(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt,
									   @RequestParam("file") MultipartFile file) {
		try {
			// 验证文件类型
			if (!file.getContentType().startsWith("image/")) {
				return "请上传图片文件";
			}

			// 创建包含图片的用户消息
			Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), file.getResource());
			UserMessage message = UserMessage.builder()
					.text(prompt)
					.media(media)
					.build();

			// 设置消息格式为图片
			message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

			// 创建提示词，启用多模态模型
			Prompt chatPrompt = new Prompt(message,
					DashScopeChatOptions.builder()
							.withModel("qwen-vl-max-latest")  // 使用视觉模型
							.withMultiModel(true)             // 启用多模态
							.withVlHighResolutionImages(true) // 启用高分辨率图片处理
							.withTemperature(0.7)
							.build());

			// 调用模型进行图片分析
			return dashScopeChatClient.prompt(chatPrompt).call().content();

		} catch (Exception e) {
			return "图片分析失败: " + e.getMessage();
		}
	}

}
