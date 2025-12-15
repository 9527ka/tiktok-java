package kernel.web;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

public class JdbcTemplateWithPaging {
	private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateWithPaging.class);
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcOperations namedParameterJdbcTemplate;

	public List<Map<String, Object>> queryPage(String sql, Object[] args, int start, int limit) {
		if ((start <= 0) && (limit <= 0)) {
			return this.jdbcTemplate.queryForList(sql, args);
		}
		if (start <= 1) {
			sql = getLimitString(sql, false);
			args = ArrayUtils.add(args, args.length, Integer.valueOf(limit));
		} else {
			sql = getLimitString(sql, true);
			args = ArrayUtils.add(args, args.length, Integer.valueOf(start + limit));
			args = ArrayUtils.add(args, args.length, Integer.valueOf(start));
		}

		logger.info("paging sql : \n" + sql);
		return this.jdbcTemplate.queryForList(sql, args);
	}

	public List<Map<String, Object>> queryPage(String sql, int start, int limit) {
		Object[] args = new Object[0];
		return queryPage(sql, args, start, limit);
	}

	public <T> List<T> queryPage(String sql, int start, int limit, RowMapper<T> rowMapper) throws DataAccessException {
		if ((start <= 0) && (limit <= 0)) {
			return this.jdbcTemplate.query(sql, rowMapper);
		}
		Object[] args = new Object[0];
		if (start <= 1) {
			sql = getLimitString(sql, false);
			args = ArrayUtils.add(args, args.length, Integer.valueOf(limit));
		} else {
			sql = getLimitString(sql, true);
			args = ArrayUtils.add(args, args.length, Integer.valueOf(start + limit));
			args = ArrayUtils.add(args, args.length, Integer.valueOf(start));
		}

		Pattern pattern = Pattern.compile("\\?");

		Matcher matcher = pattern.matcher(sql);

		for (int i = 0; i < args.length; matcher = pattern.matcher(sql)) {
			sql = matcher.replaceFirst(args[i].toString());

			i++;
		}

		logger.info("paging sql : \n" + sql);
		return this.jdbcTemplate.query(sql, rowMapper);
	}

	private String getLimitString(String sql, boolean hasOffset) {
		sql = sql.trim();
		boolean isForUpdate = false;
		if (sql.toLowerCase().endsWith(" for update")) {
			sql = sql.substring(0, sql.length() - 11);
			isForUpdate = true;
		}

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		if (hasOffset) {
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		} else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (hasOffset) {
			pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
		} else {
			pagingSelect.append(" ) where rownum <= ?");
		}

		if (isForUpdate) {
			pagingSelect.append(" for update");
		}
		return pagingSelect.toString();
	}

	public int queryCountBySql(String sqlStr) {
		return ((Integer) this.jdbcTemplate.queryForObject(sqlStr, Integer.class)).intValue();
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
