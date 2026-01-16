package com.github.pdaodao.springwebplus.ai.entity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.StringListJsonHandler;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "术语")
@TableName(value = "ai_chat_term_text", autoResultMap = true)
public class AiChatTermText extends SnowIdWithTimeUserEntity implements WithTeam {
    @Schema(description = "术语名称")
    @NotBlank(message = "名称不能为空")
    private String title;

    @Schema(description = "术语描述")
    @NotBlank(message = "描述不能为空")
    private String remark;

    @Schema(description = "同义词")
    @TableFieldSize(500)
    @TableField(typeHandler = StringListJsonHandler.class)
    private List<String> subs;

    @TableFieldIndex
    @Schema(description = "租户-团队id")
    private String teamId;

    public String toEmbeddingText(){
        final StringBuilder sb = new StringBuilder();
        sb.append(title);
        if(CollUtil.isNotEmpty(subs)){
            sb.append("、").append(StrUtil.join("、", subs));
        }
        sb.append(" ").append(remark);
        return sb.toString();
    }
}