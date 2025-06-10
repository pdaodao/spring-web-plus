package com.github.pdaodao.springwebplus.ai.doc;

import com.github.pdaodao.springwebplus.ai.pojo.DocText;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface DocumentTransformer {
    DocText transform(DocText document);

    default List<DocText> transformAll(List<DocText> documents) {
        return documents.stream()
                .map(this::transform)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
