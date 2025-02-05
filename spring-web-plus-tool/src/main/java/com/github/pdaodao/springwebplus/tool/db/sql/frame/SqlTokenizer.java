package com.github.pdaodao.springwebplus.tool.db.sql.frame;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 修改自 TableNameParser
 */
public class SqlTokenizer {
    public static final Set<String> SEPARATORS = new HashSet<>();
    public static final LinkedCaseInsensitiveMap<Boolean> SqlDyKey = new LinkedCaseInsensitiveMap<>();

    /**
     * 该表达式会匹配 SQL 中不是 SQL TOKEN 的部分，比如换行符，注释信息，结尾的 {@code ;} 等。
     * <p>
     * 排除的项目包括：
     * 1、以 -- 开头的注释信息
     * 2、;
     * 3、空白字符
     * 4、使用 /* * / 注释的信息
     * 5、把 ,() 也要分出来
     */
    private static final Pattern NON_SQL_TOKEN_PATTERN = Pattern.compile("(--[^\\v]+)|;|(\\s+)|((?s)/[*].*?[*]/)"
            + "|>=|<=|=|<|>|((?s)\\$+[\\{].*?[\\}])"
            + "|(((\\b|\\B)(?=[,()]))|((?<=[,()])(\\b|\\B)))"
    );

    static {
        SEPARATORS.add(",");
        SEPARATORS.add("SELECT");
        SEPARATORS.add("FROM");
        SEPARATORS.add("JOIN");
        SEPARATORS.add("LEFT JOIN");
        SEPARATORS.add("RIGHT JOIN");
        SEPARATORS.add("INNER JOIN");
        SEPARATORS.add("CROSS JOIN");
        SEPARATORS.add("FULL JOIN");
        SEPARATORS.add("UNION");
        SEPARATORS.add("UNION ALL");
        SEPARATORS.add("JOIN ON");
        SEPARATORS.add("ON");
        SEPARATORS.add("WHERE");
        SEPARATORS.add("AND");
        SEPARATORS.add("OR");
        SEPARATORS.add("(");
        SEPARATORS.add(")");
        SEPARATORS.add("GROUP BY");
        SEPARATORS.add("ORDER BY");
        SEPARATORS.add("HAVING");
        SEPARATORS.add("LIMIT");
        SEPARATORS.add("OFFSET");
    }



    private final List<SqlToken> tokens;

    private final String sql;

    /**
     * @param sql 需要解析的 SQL 语句
     */
    public SqlTokenizer(String sql) {
        this.sql = sql;
        this.tokens = fetchAllTokens(sql);
    }

    public static void main(String[] args) {
        String sql = "SELECT sl, group_no as dept_code FROM t1 order by a desc, b asc  group by a,b,c limit ${ limit } OFFSET 3";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        sqlTokenizer.merge();
    }

    // 尽量去掉  1 = 1 的逻辑
    private static List<SqlTokenStep> dropOneEqualOne(final List<SqlTokenStep> tts) {
        if (tts == null || tts.size() < 3) {
            return tts;
        }
        final boolean[] bits = new boolean[tts.size()];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = true;
        }
        for (int j = 0; j < tts.size(); j++) {
            final String current = tts.get(j).toString().trim().replaceAll("\n", "");
            if ("1 = 1".equals(current)) {
                // and|or 1 = 1
                if (j > 0 && isAndOr(tts.get(j - 1)) && bits[j - 1]) {
                    bits[j] = false;
                    bits[j - 1] = false;
                    continue;
                }
                // 1 = 1 and|or
                if (j + 1 < tts.size() && isAndOr(tts.get(j + 1)) && bits[j + 1]) {
                    bits[j] = false;
                    bits[j + 1] = false;
                    continue;
                }
                // ( 1 = 1 )
                if (j - 1 > 0 && j + 1 < tts.size() && "(".equals(tts.get(j - 1).toString()) && ")".equals(tts.get(j + 1).toString())) {
                    // and?or
                    if (j - 3 > 0 && StrUtil.isBlank(tts.get(j - 2).toString()) && isAndOr(tts.get(j - 3)) && bits[j - 3]) {
                        bits[j - 3] = false;
                        bits[j - 2] = false;
                        bits[j - 1] = false;
                        bits[j] = false;
                        bits[j + 1] = false;
                        continue;
                    }
                    // (1=1) and/or
                    if (j + 3 < tts.size() && StrUtil.isBlank(tts.get(j + 2).toString()) && isAndOr(tts.get(j + 3)) && bits[j + 3]) {
                        bits[j - 1] = false;
                        bits[j] = false;
                        bits[j + 1] = false;
                        bits[j + 2] = false;
                        bits[j + 3] = false;
                        continue;
                    }
                }
            }
        }
        final List<SqlTokenStep> ret = new ArrayList<>();
        for (int i = 0; i < tts.size(); i++) {
            if (bits[i] == true) {
                ret.add(tts.get(i));
            }
        }
        return ret;
    }

    private static boolean isAndOr(SqlTokenStep step) {
        if (step == null) {
            return false;
        }
        if ("and".equalsIgnoreCase(step.toString())) {
            return true;
        }
        return "or".equalsIgnoreCase(step.toString());
    }

    public List<SqlToken> getTokens() {
        return tokens;
    }

    /**
     * 合并
     *
     * @return
     */
    public List<SqlTokenStep> merge() {
        final List<SqlTokenizer.SqlTokenStep> list = toSteps();
        final List<SqlTokenizer.SqlTokenStep> result = new ArrayList<>();
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            SqlTokenStep step = list.get(i);
            if ("LIMIT".equalsIgnoreCase(step.toFormatString())) {
                step.addChildren(list.get(++i));
                if (i + 1 < size &&
                        (list.get(i + 1).toFormatString().equalsIgnoreCase(",")
                                || list.get(i + 1).toFormatString().equalsIgnoreCase("OFFSET"))) {
                    step.addChildren(list.get(++i));
                    step.addChildren(list.get(++i));
                }
            }
            if ("ORDER BY".equalsIgnoreCase(step.toFormatString())) {
                step.addChildren(list.get(++i));
                if (i + 1 < size) {
                    for (int ii = i + 1; ii < size; ii++) {
                        if (list.get(ii).toFormatString().equalsIgnoreCase(",")) {
                            step.addChildren(list.get(++i));
                            step.addChildren(list.get(++i));
                            ii++;
                            ii++;
                        } else {
                            break;
                        }
                    }
                }
            }
            result.add(step);
        }
        return result;
    }

    /**
     * 合并
     *
     * @return
     */
    public List<SqlTokenStep> toSteps() {
        final List<SqlTokenizer.SqlTokenStep> tokenStepList = new ArrayList<>();
        if (tokens == null || tokens.size() < 1) {
            return tokenStepList;
        }
        SqlTokenizer.SqlTokenStep currentTokenStep = new SqlTokenizer.SqlTokenStep();
        int leftBracketDiffSize = 0;
        final int size = tokens.size();
        final Stack<Integer> leftStack = new Stack<>();

        String nextIgnoreKeyWord = null;

        for (int index = 0; index < size; index++) {
            SqlTokenizer.SqlToken token = tokens.get(index);
            if (token.isNoSql()) {
                currentTokenStep.addChildren(token);
                continue;
            }
            final String tokenValueUpper = token.getValue().toUpperCase();
            if (lastIsNosql(index) && nextIsNosql(index)) {
                if ("BETWEEN".equals(tokenValueUpper)) {
                    nextIgnoreKeyWord = "AND";
                }
            }
            // 处理 <where></where> <if></if>
            if("<".equals(tokenValueUpper) && nextIsDyKey(index)){
                tokenStepList.add(currentTokenStep);
                currentTokenStep = new SqlTokenStep();
                currentTokenStep.addChildren(token);
                nextIgnoreKeyWord = ">";
                continue;
            }
            /// end dy

            if ("(".equals(token.getValue())) {
                if (isLeftBreakCanBreak(index)) {
                    leftStack.push(leftBracketDiffSize + 0);
                    leftBracketDiffSize = 0;
                    tokenStepList.add(currentTokenStep);
                    currentTokenStep = new SqlTokenStep();
                    currentTokenStep.addChildren(token);
                    tokenStepList.add(currentTokenStep);
                    currentTokenStep = new SqlTokenStep();
                    continue;
                }
                leftBracketDiffSize++;
            }

            if (")".equals(token.getValue())) {
                leftBracketDiffSize--;
                if (leftBracketDiffSize < 0 && !leftStack.empty()) {
                    leftBracketDiffSize = leftStack.pop();
                    tokenStepList.add(currentTokenStep);
                    currentTokenStep = new SqlTokenStep();
                    currentTokenStep.addChildren(token);
                    tokenStepList.add(currentTokenStep);
                    currentTokenStep = new SqlTokenStep();
                    continue;
                }
            }

            if (leftBracketDiffSize == 0 && !tokenValueUpper.equals(")")) {
                if (isBlockSeparator(index) > 0) {
                    if (tokenValueUpper.equals(nextIgnoreKeyWord) || "*".equals(nextIgnoreKeyWord)) {
                        currentTokenStep.addChildren(token);
                        nextIgnoreKeyWord = null;
                        continue;
                    }
                    nextIgnoreKeyWord = null;

                    int position = isBlockSeparator(index);
                    if (position == 2) {
                        tokenStepList.add(currentTokenStep);
                        currentTokenStep = new SqlTokenizer.SqlTokenStep();
                        currentTokenStep.addChildren(token);
                        continue;
                    }
                    if (position == 1) {
                        tokenStepList.add(currentTokenStep);
                        currentTokenStep = new SqlTokenizer.SqlTokenStep();
                        currentTokenStep.addChildren(token);
                        tokenStepList.add(currentTokenStep);
                        currentTokenStep = new SqlTokenizer.SqlTokenStep();
                        continue;
                    }
                    if (position == 3) {
                        currentTokenStep.addChildren(token);
                        tokenStepList.add(currentTokenStep);
                        currentTokenStep = new SqlTokenizer.SqlTokenStep();
                        continue;
                    }
                    currentTokenStep.addChildren(token);
                }
            }
            currentTokenStep.addChildren(token);

        }
        if (currentTokenStep.getChildren().size() > 0) {
            tokenStepList.add(currentTokenStep);
        }
        return tokenStepList;
    }

    private boolean nextIsNoSqlOrLeft(int index) {
        if (index >= tokens.size() - 1) {
            return false;
        }
        return tokens.get(index + 1).isNoSql() || tokens.get(index + 1).getValue().equals("(");
    }

    public String nextSqlToken(int index) {
        if (index >= tokens.size() - 1) {
            return null;
        }
        for (int i = index + 1; i < tokens.size(); i++) {
            if (!tokens.get(i).isNoSql()) {
                return tokens.get(i).getValue();
            }
        }
        return "";
    }

    private String lastSqlToken(int index) {
        for (int i = index - 1; i >= 0; i--) {
            if (!tokens.get(i).isNoSql()) {
                return tokens.get(i).getValue();
            }
        }
        return "";
    }

    private boolean lastIsNosql(int index) {
        if (index > 0) {
            return tokens.get(index - 1).isNoSql();
        }
        return true;
    }

    private boolean nextIsDyKey(int index) {
        if (index < tokens.size() - 2) {
            return SqlDyKey.containsKey(tokens.get(index + 1).toString());
        }
        return false;
    }


    private boolean nextIsNosql(int index) {
        if (index < tokens.size() - 2) {
            return tokens.get(index + 1).isNoSql();
        }
        return true;
    }

    /**
     * 左括号 ( 是否可以独立
     *
     * @param index
     * @return
     */
    private boolean isLeftBreakCanBreak(int index) {
        if (index == 0 || index > tokens.size() - 1) {
            return false;
        }
        // 向右边找找
        String right = nextSqlToken(index).toUpperCase();
        if (right.length() > 1 && SEPARATORS.contains(right)) {
            return true;
        }
        // 向左边找找
        String left = lastSqlToken(index).toUpperCase();
        return left.length() > 1 && SEPARATORS.contains(left);
    }

    /**
     * @param index
     * @return 0 非关键字   1 独立的关键字  2 复合关键字的前部  3 复合关键字的后部
     */
    private int isBlockSeparator(int index) {
        if (index > tokens.size() - 1) {
            return 0;
        }
        for (int i = index; i < tokens.size() - 1; i++) {
            if (!tokens.get(i).isNoSql()) {
                index = i;
                break;
            }
        }
        if (index > tokens.size() - 1) {
            return 0;
        }
        final String up = tokens.get(index).getValue().toUpperCase();
        if (up.length() == 1 && SEPARATORS.contains(up)) {
            return 1;
        }
        if (!nextIsNoSqlOrLeft(index)) {
            return 0;
        }
        if (index == 0) {
            return 1;
        }

        if (SEPARATORS.contains(up + " " + nextSqlTokenValue(index).toUpperCase())) {
            return 2;
        }
        if (SEPARATORS.contains(lastSqlTokenValue(index).toUpperCase() + " " + up)) {
            return 3;
        }
        if (SEPARATORS.contains(up)) {
            return 1;
        }
        return 0;
    }

    private String lastSqlTokenValue(int index) {
        for (int i = index - 1; i >= 0; i--) {
            if (!tokens.get(i).isNoSql()) {
                if (tokens.get(i).getValue().length() < 2) {
                    return "";
                }
                return tokens.get(i).getValue().toUpperCase();
            }
        }
        return "";
    }

    private String nextSqlTokenValue(int index) {
        for (int i = index + 1; i < tokens.size(); i++) {
            if (!tokens.get(i).isNoSql()) {
                if (tokens.get(i).getValue().length() < 2) {
                    return "";
                }
                return tokens.get(i).getValue().toUpperCase();
            }
        }
        return "";
    }

    public String toSql(List<SqlToken> tts) {
        StringBuilder sb = new StringBuilder();
        for (SqlToken token : tts) {
            sb.append(token.getValue());
        }
        return sb.toString();
    }

    public String stepToSql(List<SqlTokenStep> tts) {
        for (int i = 0; i < 3; i++) {
            tts = dropOneEqualOne(tts);
        }
        StringBuilder sb = new StringBuilder();
        int next = 0;
        for (SqlTokenStep token : tts) {
            next++;
            if(token.toString().equalsIgnoreCase("where")
                    && ( next == tts.size() || !nextStepHasFilter(tts, next))){
                continue;
            }
            sb.append(token.toString());
        }
        return sb.toString();
    }

    private boolean nextStepHasFilter(final List<SqlTokenStep> tts, final int next){
        for(int i = next ; i < tts.size(); i++){
            if(StrUtil.isBlank(tts.get(i).toString())){
                continue;
            }
            final String ss = tts.get(i).toString().toLowerCase().trim();
            if(ss.startsWith("order ") || ss.startsWith("limit") || ss.startsWith("having") || ss.startsWith("group")){
                return false;
            }
            return true;
        }
        return true;
    }


    /**
     * 接受一个新的访问者，并访问当前 SQL 的表名称
     * <p>
     * 现在我们改成了访问者模式，不在对以前的 SQL 做改动
     * 同时，你可以方便的获得表名位置的索引
     *
     * @param visitor 访问者
     */
    public void accept(TokenVisitor visitor) {
        int index = 0;
        while (hasMoreTokens(index)) {
            visitor.visit(tokens.get(index));
            index++;
        }
    }

    /**
     * 从 SQL 语句中提取出 所有的 SQL Token
     *
     * @param sql SQL
     * @return 语句
     */
    private List<SqlToken> fetchAllTokens(String sql) {
        List<SqlToken> tokens = new ArrayList<>();
        Matcher matcher = NON_SQL_TOKEN_PATTERN.matcher(sql);
        int last = 0;
        while (matcher.find()) {
            int start = matcher.start();
            if (start != last) {
                tokens.add(new SqlToken(last, start, sql.substring(last, start)));
            }
            last = matcher.end();
            if (start != last) {
                String t = sql.substring(start, last);
                if (t.startsWith("--") || (t.startsWith("/*") && !t.startsWith("/*+"))) {
                    continue;
                }
                tokens.add(new SqlToken(start, last, t));
            }
        }
        if (last != sql.length()) {
            tokens.add(new SqlToken(last, sql.length(), sql.substring(last)));
        }
        return tokens;
    }

    private boolean hasIthToken(List<TableNameParser.SqlToken> tokens, int currentIndex) {
        return hasMoreTokens(currentIndex) && tokens.size() > currentIndex + 3;
    }

    private boolean hasMoreTokens(int index) {
        return index < tokens.size();
    }

    /**
     * token 访问器
     */
    public interface TokenVisitor {
        void visit(SqlToken token);
    }

    public static class SqlTokenStep {
        private final List<SqlToken> children = new ArrayList<>();

        public SqlTokenStep addChildren(SqlToken token) {
            if (token != null) {
                children.add(token);
            }
            return this;
        }

        public List<SqlToken> getChildren() {
            return children;
        }

        public List<SqlTokenStep> merge() {
            final List<SqlTokenStep> result = new ArrayList<>();
            SqlTokenStep current = new SqlTokenStep();
            for (int i = 0; i < children.size(); i++) {
                SqlToken token = children.get(i);
                String up = token.getValue().toUpperCase();
                if ("=;>;<;>=;<;<=;!=;<>;!LIKE;LIKE;BETWEEN;IN;".contains(up + ";")) {
                    result.add(current);
                    current = new SqlTokenStep();
                    current.addChildren(token);
                    result.add(current);
                    current = new SqlTokenStep();
                    continue;
                }
                current.addChildren(token);
            }
            if (current.getChildren().size() > 0) {
                result.add(current);
            }
            return result;
        }

        public void addChildren(SqlTokenStep step) {
            if (step.getChildren().size() > 0) {
                children.addAll(step.getChildren());
            }
        }

        public SqlToken firstToken() {
            if (children.size() < 1) {
                return null;
            }
            return children.get(0);
        }

        public boolean hasVariable() {
            for (SqlToken token : children) {
                if (!token.isNoSql() && token.getValue().contains("$")) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            children.forEach(t -> sb.append(t));
            return sb.toString();
        }

        public String toFormatString() {
            final StringBuilder sb = new StringBuilder();
            boolean isBlank = false;
            for (SqlToken token : children) {
                if (StrUtil.isBlank(token.getValue()) || "\n".equals(token.getValue())) {
                    if (!isBlank) {
                        sb.append(" ");
                    }
                    isBlank = true;
                    continue;
                }
                isBlank = false;
                sb.append(token.getValue());
            }
            return sb.toString().trim();
        }
    }


    /**
     * SQL 词
     */
    public static class SqlToken {
        private final int start;
        private final int end;
        private final String value;

        public SqlToken(int start, int end, String value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }


        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getValue() {
            return value;
        }

        public boolean isNoSql() {
            return StrUtil.isBlank(value) || "\n".equals(value);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
