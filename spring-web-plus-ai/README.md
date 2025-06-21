# 

https://docs.langchain4j.dev/integrations/embedding-models/in-process
https://docs.langchain4j.dev/integrations/scoring-reranking-models/in-process

Qwen3-Embedding
https://huggingface.co/onnx-community/Qwen3-Embedding-0.6B-ONNX
https://huggingface.co/zhiqing/Qwen3-Embedding-0.6B-ONNX
https://huggingface.co/zhiqing/Qwen3-Reranker-0.6B-ONNX

todo 参考 docx解析
https://github.com/RDSunhy/android-word2html/blob/master/android_word2html/src/main/java/com/shy/poi/word2html/WordUtils.java

https://www.form-create.com/
```
id:TEXT
data:{"id":"STEP_ZERO","role":null,"content":"AI正在分析用户输入","type":"LOADING","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:null
data:{"role":"assistant","content":"我是","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"你的","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"数据库","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"管理工具AI助手","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"，可以帮助你查询","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"数据库信息或回答","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"其他问题。有什么","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"我可以帮你的吗","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:null
data:{"role":"assistant","content":"？","type":"MARKDOWN","toolCalls":null,"questionId":376804,"chatId":102076,"title":"你是谁"}
retry:3000

id:TEXT
data:{"id":"STEP_ZERO","role":null,"content":"AI分析用户输入已完成","type":"LOADING_COMPLETE","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":null,"type":null,"toolCalls":null,"questionId":376804,"chatId":102076,"title":null,"answerId":384740}

id:[DONE]
data:[DONE]
retry:3000

```

2
```
id:TEXT
data:{"id":"STEP_ZERO","role":null,"content":"AI正在分析用户输入","type":"LOADING","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":"{\"dataSourceId\":35382,\"databaseName\":\"CHAT2DB_DEMO\",\"schemaName\":\"\"}","type":"DATABASE_INFO","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:null
data:{"role":"assistant","content":"","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"SELECT","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" AGE","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":",","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" COUNT(*) AS cnt","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" FROM STUDENT_MAT","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" GROUP BY AGE UNION","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" SELECT AGE, COUNT","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"(*) AS cnt FROM","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" STUDENT_POR","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" GROUP BY AGE ORDER","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" BY AGE","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"","type":"MARKDOWN","toolCalls":null,"questionId":376805,"chatId":102077,"title":null}
retry:3000

id:TEXT
data:{"id":"STEP_ZERO","role":null,"content":"AI分析用户输入已完成","type":"LOADING_COMPLETE","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":null,"type":null,"toolCalls":null,"questionId":376805,"chatId":102077,"title":null,"answerId":384741}

id:[DONE]
data:[DONE]
retry:3000
```

```
{"model":"qwen2.5-coder-32b-instruct",
"chatId":102076,
"dataSourceId":35382,
"databaseName":"CHAT2DB_DEMO",
"schemaName":"",
"questionType":"ORDINARY_CHAT",
"input":"按年龄对学生进行统计 结果显示为柱状图",
"source":"DASHBOARD_DRAWER_CHAT",
"tableList":[]}
```

 ```
id:TEXT
data:{"id":"STEP_ZERO","role":null,"content":"AI正在分析用户输入","type":"LOADING","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:null
data:{"role":"assistant","content":"基于","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"用户","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"输入","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"，匹配到如下","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"表：\n```json","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\n[\"STUDENT","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"_MAT\", \"ST","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"UDENT_POR\"]","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\n```","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"","type":"MARKDOWN","toolCalls":[{"function":{"tableNames":["STUDENT_MAT","STUDENT_POR"]},"type":"function","name":"query_table"}],"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:TEXT
data:{"id":"STEP_ZERO","role":null,"content":"AI分析用户输入已完成","type":"LOADING_COMPLETE","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":"STEP_QUERY_TABLE","role":null,"content":"正在查询表结构","type":"LOADING","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":"{\"STUDENT_POR\":{\"dataSourceId\":35382,\"databaseName\":\"CHAT2DB_DEMO\",\"schemaName\":\"\",\"tableName\":\"STUDENT_POR\",\"tableSchema\":\"CREATE TABLE \\\"STUDENT_POR\\\" (\\n\\t \\\"ID\\\" INTEGER(32,0) NOT NULL,\\n\\t \\\"SCHOOL\\\" CHARACTER VARYING(5,0) NULL,\\n\\t \\\"SEX\\\" CHARACTER(1,0) NULL,\\n\\t \\\"AGE\\\" INTEGER(32,0) NULL,\\n\\t \\\"ADDRESS\\\" CHARACTER(1,0) NULL,\\n\\t \\\"FAMSIZE\\\" CHARACTER VARYING(5,0) NULL,\\n\\t \\\"PSTATUS\\\" CHARACTER(1,0) NULL,\\n\\t \\\"MEDU\\\" INTEGER(32,0) NULL,\\n\\t \\\"FEDU\\\" INTEGER(32,0) NULL,\\n\\t \\\"MJOB\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"FJOB\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"REASON\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"GUARDIAN\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"TRAVELTIME\\\" INTEGER(32,0) NULL,\\n\\t \\\"STUDYTIME\\\" INTEGER(32,0) NULL,\\n\\t \\\"FAILURES\\\" INTEGER(32,0) NULL,\\n\\t \\\"SCHOOLSUP\\\" CHARACTER(3,0) NULL,\\n\\t \\\"FAMSUP\\\" CHARACTER(3,0) NULL,\\n\\t \\\"PAID\\\" CHARACTER(3,0) NULL,\\n\\t \\\"ACTIVITIES\\\" CHARACTER(3,0) NULL,\\n\\t \\\"NURSERY\\\" CHARACTER(3,0) NULL,\\n\\t \\\"HIGHER\\\" CHARACTER(3,0) NULL,\\n\\t \\\"INTERNET\\\" CHARACTER(3,0) NULL,\\n\\t \\\"ROMANTIC\\\" CHARACTER(3,0) NULL,\\n\\t \\\"FAMREL\\\" INTEGER(32,0) NULL,\\n\\t \\\"FREETIME\\\" INTEGER(32,0) NULL,\\n\\t \\\"GOOUT\\\" INTEGER(32,0) NULL,\\n\\t \\\"DALC\\\" INTEGER(32,0) NULL,\\n\\t \\\"WALC\\\" INTEGER(32,0) NULL,\\n\\t \\\"HEALTH\\\" INTEGER(32,0) NULL,\\n\\t \\\"ABSENCES\\\" INTEGER(32,0) NULL,\\n\\t \\\"G1\\\" INTEGER(32,0) NULL,\\n\\t \\\"G2\\\" INTEGER(32,0) NULL,\\n\\t \\\"G3\\\" INTEGER(32,0) NULL\\n);\",\"tableType\":\"TABLE\"},\"STUDENT_MAT\":{\"dataSourceId\":35382,\"databaseName\":\"CHAT2DB_DEMO\",\"schemaName\":\"\",\"tableName\":\"STUDENT_MAT\",\"tableSchema\":\"CREATE TABLE \\\"STUDENT_MAT\\\" (\\n\\t \\\"ID\\\" INTEGER(32,0) NOT NULL,\\n\\t \\\"SCHOOL\\\" CHARACTER VARYING(5,0) NULL,\\n\\t \\\"SEX\\\" CHARACTER(1,0) NULL,\\n\\t \\\"AGE\\\" INTEGER(32,0) NULL,\\n\\t \\\"ADDRESS\\\" CHARACTER(1,0) NULL,\\n\\t \\\"FAMSIZE\\\" CHARACTER VARYING(5,0) NULL,\\n\\t \\\"PSTATUS\\\" CHARACTER(1,0) NULL,\\n\\t \\\"MEDU\\\" INTEGER(32,0) NULL,\\n\\t \\\"FEDU\\\" INTEGER(32,0) NULL,\\n\\t \\\"MJOB\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"FJOB\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"REASON\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"GUARDIAN\\\" CHARACTER VARYING(20,0) NULL,\\n\\t \\\"TRAVELTIME\\\" INTEGER(32,0) NULL,\\n\\t \\\"STUDYTIME\\\" INTEGER(32,0) NULL,\\n\\t \\\"FAILURES\\\" INTEGER(32,0) NULL,\\n\\t \\\"SCHOOLSUP\\\" CHARACTER(3,0) NULL,\\n\\t \\\"FAMSUP\\\" CHARACTER(3,0) NULL,\\n\\t \\\"PAID\\\" CHARACTER(3,0) NULL,\\n\\t \\\"ACTIVITIES\\\" CHARACTER(3,0) NULL,\\n\\t \\\"NURSERY\\\" CHARACTER(3,0) NULL,\\n\\t \\\"HIGHER\\\" CHARACTER(3,0) NULL,\\n\\t \\\"INTERNET\\\" CHARACTER(3,0) NULL,\\n\\t \\\"ROMANTIC\\\" CHARACTER(3,0) NULL,\\n\\t \\\"FAMREL\\\" INTEGER(32,0) NULL,\\n\\t \\\"FREETIME\\\" INTEGER(32,0) NULL,\\n\\t \\\"GOOUT\\\" INTEGER(32,0) NULL,\\n\\t \\\"DALC\\\" INTEGER(32,0) NULL,\\n\\t \\\"WALC\\\" INTEGER(32,0) NULL,\\n\\t \\\"HEALTH\\\" INTEGER(32,0) NULL,\\n\\t \\\"ABSENCES\\\" INTEGER(32,0) NULL,\\n\\t \\\"G1\\\" INTEGER(32,0) NULL,\\n\\t \\\"G2\\\" INTEGER(32,0) NULL,\\n\\t \\\"G3\\\" INTEGER(32,0) NULL\\n);\",\"tableType\":\"TABLE\"}}","type":"TABLE","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":"STEP_QUERY_TABLE","role":null,"content":"查询表结构已完成","type":"LOADING_COMPLETE","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":"{\"dataSourceId\":35382,\"databaseName\":\"CHAT2DB_DEMO\",\"schemaName\":\"\"}","type":"DATABASE_INFO","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":"STEP_GENERATE_CODE","role":null,"content":"生成代码中","type":"LOADING","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:null
data:{"role":"assistant","content":"为了","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"按","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"年龄","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"对学生进行统计并","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"生成柱状图","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"，我们可以先编写","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"一个SQL查询来","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"获取每个年龄段的学生","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"数量。然后，","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"你可以使用这个查询","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"的结果来生成柱","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"状图。\n\n以下是","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"按年龄对学生进行","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"统计的SQL查询","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"：\n\n```sql","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\nSELECT AGE, COUNT","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"(*) AS student_count","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\nFROM STUDENT","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"_MAT\nGROUP BY","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" AGE\nORDER BY","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" AGE;\n```","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\n\n这个查询会返回","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"每个年龄段的学生数量","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"，并按年龄排序","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"。\n\n如果你需要将","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"这个查询应用到","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"另一个表（例如","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" `STUDENT_P","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"OR`），你可以","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"使用类似的查询：","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\n\n```sql\nSELECT","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" AGE, COUNT(*)","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" AS student_count","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\nFROM STUDENT_P","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"OR\nGROUP BY","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" AGE\nORDER BY","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":" AGE;\n```","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"\n\n接下来，你可以使用","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"这些查询的结果来","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"生成柱状图","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"。如果你有具体的","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"图表生成工具或","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"库（如Python","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"的Matplotlib、","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"Tableau等），","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"你可以将上述查询","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"的结果导入到这些","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"工具中生成柱","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"状图。\n\n如果你","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"需要进一步的帮助来","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"生成柱状图","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"，请告诉我你使用的","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"工具或库。","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:null
data:{"role":"assistant","content":"","type":"MARKDOWN","toolCalls":null,"questionId":376806,"chatId":102076,"title":null}
retry:3000

id:TEXT
data:{"id":null,"role":null,"content":"\nSQL验证成功","type":"MARKDOWN","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":"STEP_GENERATE_CODE","role":null,"content":"生成代码已完成","type":"LOADING_COMPLETE","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":"STEP_RENDER_DASHBOARD","role":null,"content":"查询数据中","type":"LOADING","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":"","type":"MARKDOWN","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":"{\"answerPartId\":543699,\"chartSchema\":{\"chartType\":\"Column\",\"summary\":\"按年龄对学生进行统计的结果，使用柱状图展示，x坐标为年龄，y坐标为学生数量\",\"xField\":\"AGE\",\"yField\":\"STUDENT_COUNT\"},\"databaseInfo\":{\"dataSourceId\":35382,\"databaseName\":\"CHAT2DB_DEMO\",\"schemaName\":\"\",\"sql\":\"SELECT AGE, COUNT(*) AS student_count\\nFROM STUDENT_MAT\\nGROUP BY AGE\\nORDER BY AGE;\"},\"metaData\":{\"dataList\":[[\"15\",\"82\"],[\"16\",\"104\"],[\"17\",\"98\"],[\"18\",\"82\"],[\"19\",\"23\"],[\"20\",\"3\"],[\"21\",\"1\"],[\"22\",\"1\"]],\"headerList\":[{\"autoIncrement\":0,\"columnName\":\"AGE\",\"columnType\":\"INTEGER\",\"dataType\":\"NUMERIC\",\"name\":\"AGE\",\"tableName\":\"STUDENT_MAT\"},{\"autoIncrement\":0,\"columnName\":\"STUDENT_COUNT\",\"columnType\":\"BIGINT\",\"dataType\":\"NUMERIC\",\"name\":\"STUDENT_COUNT\",\"tableName\":\"\"}]}}","type":"DASHBOARD","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":"STEP_RENDER_DASHBOARD","role":null,"content":"数据查询已完成","type":"LOADING_COMPLETE","toolCalls":null,"questionId":null,"chatId":null,"title":null,"answerId":null}

id:TEXT
data:{"id":null,"role":null,"content":null,"type":null,"toolCalls":null,"questionId":376806,"chatId":102076,"title":null,"answerId":384742}

id:[DONE]
data:[DONE]
retry:3000
```

https://app.chat2db-ai.com/api/v1/ai/chat/get?id=102077
```
{
"success": true,
"errorCode": null,
"errorMessage": null,
"errorDetail": null,
"solutionLink": null,
"data": {
"id": 102077,
"title": "Chat with Demo data",
"organizationId": 17590,
"userId": 6667878,
"dataSourceId": 35382,
"dataSourceCollectionId": null,
"databaseName": null,
"schemaName": null,
"tableNames": null,
"modelName": "",
"viewShareId": "ef1ec7f91ea74677beb278d8ff1e6038",
"editShareId": "deb2557b95404d1893e04bf9abe89732",
"source": "DATASOURCE_CONSOLE_CHAT",
"chatDetails": [
{
"question": {
"id": 376805,
"chatId": 102077,
"content": "按年龄对学生进行统计 结果显示为柱状图",
"type": "NL_2_SQL",
"extParams": null
},
"answers": [
{
"id": 384741,
"chatId": 102077,
"questionId": 376805,
"content": "[\"STUDENT_MAT\", \"STUDENT_POR\"]SELECT AGE, COUNT(*) AS cnt FROM STUDENT_MAT GROUP BY AGE UNION SELECT AGE, COUNT(*) AS cnt FROM STUDENT_POR GROUP BY AGE ORDER BY AGE",
"dashboardId": null,
"dashboard": null,
"type": "CONTENT",
"goodFeedback": false,
"badFeedback": false,
"noFeedBack": true,
"regenerate": false,
"parts": [
{
"id": 543694,
"chatId": 102077,
"answerId": 384741,
"questionId": 376805,
"partType": "MARKDOWN",
"text": "[\"STUDENT_MAT\", \"STUDENT_POR\"]",
"metaData": {
"headerList": null,
"dataList": null
},
"tableMap": null,
"functionCall": "null",
"chartSchema": null,
"databaseInfo": {
"dataSourceId": 35382,
"alias": null,
"databaseName": "CHAT2DB_DEMO",
"schemaName": "",
"databaseType": null,
"tableName": null,
"sql": null
},
"step": 1,
"status": "FINISH",
"errorCode": null,
"errorMsg": null,
"createTime": 1750466537000,
"modifyTime": 1750466537000,
"recommends": null
},
{
"id": 543695,
"chatId": 102077,
"answerId": 384741,
"questionId": 376805,
"partType": "MARKDOWN",
"text": "SELECT AGE, COUNT(*) AS cnt FROM STUDENT_MAT GROUP BY AGE UNION SELECT AGE, COUNT(*) AS cnt FROM STUDENT_POR GROUP BY AGE ORDER BY AGE",
"metaData": {
"headerList": null,
"dataList": null
},
"tableMap": null,
"functionCall": "null",
"chartSchema": null,
"databaseInfo": {
"dataSourceId": 35382,
"alias": null,
"databaseName": "CHAT2DB_DEMO",
"schemaName": "",
"databaseType": null,
"tableName": null,
"sql": null
},
"step": 2,
"status": "FINISH",
"errorCode": null,
"errorMsg": null,
"createTime": 1750466539000,
"modifyTime": 1750466539000,
"recommends": null
}
]
}
]
}
],
"editable": true,
"dataSourceCollectionVO": null,
"fileName": null,
"filePath": null
},
"traceId": null
}
```